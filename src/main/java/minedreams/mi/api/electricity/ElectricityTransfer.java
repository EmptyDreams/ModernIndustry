package minedreams.mi.api.electricity;

import java.util.*;

import minedreams.mi.tools.MISysInfo;
import minedreams.mi.api.electricity.info.ElectricityEnergy;
import minedreams.mi.api.electricity.info.WireLinkInfo;
import minedreams.mi.api.net.message.MessageList;
import minedreams.mi.blocks.te.AutoTileEntity;
import minedreams.mi.blocks.wire.WireBlock;
import minedreams.mi.tools.Tools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * 电力传输设备的父级TE，最典型的例子是{@link minedreams.mi.blocks.te.TileEntityWire}
 *
 * @author EmptyDremas
 * @version 1.0
 */
@AutoTileEntity("PARENT_ELECTRICITY_TRANSFER")
public abstract class ElectricityTransfer extends Electricity {
	
	/**
	 * 更新线路信息，此方法内部使用instanceof判断方块类型并通过强转调用
	 * {@link #updateLinkInfo(ElectricityMaker)}或{@link #updateLinkInfo(ElectricityTransfer)}
	 * @param te 新连接的方块
	 */
	protected final void updateLinkInfo(TileEntity te) {
		if (te instanceof ElectricityMaker) updateLinkInfo((ElectricityMaker) te);
		else if (te instanceof ElectricityTransfer) updateLinkInfo((ElectricityTransfer) te);
	}
	
	/**
	 * 更新线路发电机坐标信息，此方法在连接发电机时必须调用
	 * @param em 新连接的发电机
	 */
	protected final void updateLinkInfo(ElectricityMaker em) {
		infos.makers.add(em);
		em.link(this);
	}
	
	/**
	 * 更新线路连接信息，此方法在连接新电线时必须调用
	 * @param et 新连接的电线
	 */
	protected final void updateLinkInfo(ElectricityTransfer et) {
		infos.transfers.add(et);
	}
	
	/**
	 * 更新电路连接信息，此方法在断开发电机时必须调用
	 * @param em 要删除的发电机
	 */
	protected final void deleteLinkInfo(ElectricityMaker em) { infos.makers.remove(em); }
	
	/**
	 * 更新电路连接信息，此方法在断开电线连接时必须调用
	 * @param et 要删除的电线
	 */
	protected final void deleteLinkInfo(ElectricityTransfer et) { infos.transfers.remove(et); }
	
	//六个方向是否连接
	protected boolean up = false;
	protected boolean down = false;
	protected boolean east =false;
	protected boolean west = false;
	protected boolean south = false;
	protected boolean north = false;
	/** 连接的上一根电线 */
	protected ElectricityTransfer next;
	/** 连接的下一根电线 */
	protected ElectricityTransfer prev;
	/** 已经连接的方块 */
	protected final Map<EnumFacing, TileEntity> linkBlock = new HashMap<EnumFacing, TileEntity>(6);
	/** 最大电流量 */
	protected int meMax = 50000;
	/** 存储线路信息 */
	private WireLinkInfo infos = new WireLinkInfo();
	/** 是否绝缘 */
	protected boolean isInsulation = false;
	
	/** 电力损耗指数，指数越大损耗越多 */
	protected int loss = 0;
	
	public final void setInfos(WireLinkInfo infos) {
		this.infos = infos;
	}
	
	/**
	 * 判断一个方块能否连接当前电线
	 * @param ele 要连接的方块
	 */
	abstract public boolean canLink(TileEntity ele);
	
	/**
	 * 获取下一根电线
	 * @param ele 调用该方法的运输设备
	 */
	abstract public ElectricityTransfer next(ElectricityTransfer ele);
	
	/**
	 * 强制连接一个方块. 这个方块可能是用电器也可能是传输设备或原版方块，
	 * 这个需要用户自行检测，该方法中不应该检查是否允许连接
	 * @param ele 调用方块
	 * @return 连接成功返回true，否则返回false
	 */
	public boolean linkForce(TileEntity ele) {
		updateLinkInfo(ele);
		switch (Tools.whatFacing(pos, ele.getPos())) {
			case DOWN:
				down = true;
				break;
			case UP:
				up = true;
				break;
			case NORTH:
				north = true;
				break;
			case SOUTH:
				south = true;
				break;
			case WEST:
				west = true;
				break;
			default:
				east = true;
				break;
		}
		markDirty();
		return true;
	}
	
	/**
	 * 强制连接一个方块. 这个方块可能是用电器也可能是传输设备或原版方块，
	 * 这个需要用户自行检测，该方法中不应该检查是否允许连接
	 * @param pos 调用方块
	 * @return 连接成功返回true，否则返回false
	 */
	abstract public boolean linkForce(BlockPos pos);
	
	@Override
	public boolean isOverload(ElectricityEnergy now) {
		return EETransfer.calculationLoss(this) + now.getEnergy() > meMax;
	}
	
	/**
	 * 根据已经连接的电线更新数据，注意：此方法不会将断开连接的地方设置为false
	 */
	public void updateLink() {
		if (next != null) {
			switch (Tools.whatFacing(pos, next.pos)) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
		if (prev != null) {
			switch (Tools.whatFacing(pos, prev.pos)) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
	}
	
	/** 根据state更新内部数据 */
	abstract public void update(IBlockState state);
	
	/** 根据内部数据创建新的state */
	abstract public IBlockState updateState();
	
	/**
	 * 因为在radFromNBT阶段世界没有加载完毕，调用world.getTileEntity会返回null，
	 * 所有建立一个临时列表在用户调用next方法的时候检查是否需要更新参数，
	 * 当cachePos==null的时候标志已经更新，不需要再次更新
	 */
	private BlockPos[] cachePos = new BlockPos[2];
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		up = compound.getBoolean("up");
		down = compound.getBoolean("down");
		east = compound.getBoolean("east");
		west = compound.getBoolean("west");
		south = compound.getBoolean("south");
		north = compound.getBoolean("north");
		
		
		int[] is;
		if (compound.getBoolean("hasNext")) {
			is = compound.getIntArray("next");
			cachePos[0] = new BlockPos(is[0], is[1], is[2]);
		}
		if (compound.getBoolean("hasPrev")) {
			is = compound.getIntArray("prev");
			cachePos[1] = new BlockPos(is[0], is[1], is[2]);
		}
		
		if (compound.getBoolean("infos")) {
			infos = new WireLinkInfo();
			infos.read(compound);
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("up", up);
		compound.setBoolean("down", down);
		compound.setBoolean("east", east);
		compound.setBoolean("west", west);
		compound.setBoolean("south", south);
		compound.setBoolean("north", north);
		
		compound.setBoolean("hasNext", next != null);
		compound.setBoolean("hasPrev", prev != null);
		if (next != null) {
			compound.setIntArray("next", new int[] { next.pos.getX(), next.pos.getY(), next.pos.getZ()});
		}
		if (prev != null) {
			compound.setIntArray("prev", new int[] { prev.pos.getX(), prev.pos.getY(), prev.pos.getZ()});
		}
		
		
		compound.setBoolean("infos", infos.needWrite);
		if (infos.needWrite) {
			infos.write(compound);
		}
		
		return compound;
	}
	
	/**
	 * 这其中写有更新内部数据的代码，重写时应该调用
	 *
	 * @return null
	 */
	@Override
	public MessageList send(boolean isClient) {
		if (!isClient) {
			if (cachePos != null) {
				if (cachePos[0] != null) next = (ElectricityTransfer) world.getTileEntity(cachePos[0]);
				if (cachePos[1] != null) prev = (ElectricityTransfer) world.getTileEntity(cachePos[1]);
				cachePos = null;
			}
			infos.update(world);
		}
		return null;
	}
	
	@Override
	public ElectricityEnergy getEnergy() {
		return ElectricityEnergy.craet((int) energy.me, energy.voltage);
	}
	
	/**
	 * 从指定发电机方块查找到指定方块的路径
	 * @param maker 发电机方块
	 * @param find 要寻找的方块
	 * @return 经过的路径，若指定连接的方块是ET则包括指定连接的线缆方块，顺序按连接顺序排序，
	 *          若返回值为空则代表没有找到路径
	 */
	public static List<ElectricityTransfer> findETFromBlock(ElectricityMaker maker, Electricity find) {
		List<ElectricityTransfer> list = null;
		ElectricityTransfer et;
		o : for (Electricity e : maker.getLinks()) {
			if (e instanceof ElectricityTransfer) {
				List<ElectricityTransfer> temp = new ArrayList<>();
				et = (ElectricityTransfer) e;
				temp.add(et);
				if (e.equals(find)) {
					if (list == null) list = temp;
					else if (list.size() > temp.size()) list = temp;
					continue;
				}
				ElectricityTransfer t0 = et.getPrev();
				if (t0.equals(find)) {
					temp.add((t0));
					if (list == null) list = temp;
					else if (list.size() > temp.size()) list = temp;
					continue;
				}
				ElectricityTransfer et0;
				for (int l = 0; l < 2; ++l) {
					while ((et0 = t0.next(et)) != null) {
						temp.add(et0);
						if (et0.equals(find)) {
							if (list == null) list = temp;
							else if (list.size() > temp.size()) list = temp;
							continue o;
						}
						for (TileEntity t1 : et0.getLinkBlock().values()) {
							if (find.equals(t1)) {
								if (list == null) list = temp;
								else if (list.size() > temp.size()) list = temp;
								continue o;
							}
						}
					}
					temp.clear();
					temp.add(et);
				}
			} else if (e.equals(find)) {
				list = new ArrayList<>(1);
				break;
			}
		}
		return list;
	}
	
	/** 存储当前电线独立的能量消耗 */
	private ElectricityMaker.Energy energy = new ElectricityMaker.Energy();
	
	/**
	 * 向当前方块发送电力，<b>此方法应由系统调用！！！</b>
	 * @param from 调用方块
	 * @param eet 传输的能量
	 * @return 总能量详单，包括需要能量的方块的有序列表，排列顺序按照遍历先后排序，
	 *          该返回值最终会返回null，非空返回值只用于递归过程。
	 */
	public EETransfer transTo(Electricity from, EETransfer eet) {
		//判断条件
		if (from == null) from = this;
		else if (from == this) return eet;
		if (eet == null) eet = new EETransfer(this);
		
		//保存需要电力的机器
		Map<ElectricityTransfer, List<ElectricityUser>> users = new LinkedHashMap<>();
		//计算需要的能量，当电线没有连接任何用电器时传输损耗计入下一个电线的损耗中
		energy = new ElectricityMaker.Energy();
		energy.me += EETransfer.calculationLoss(this);
		if (linkBlock.size() == 0) {
			//若没有连接任何方块
			eet.need += energy.me;
			ElectricityTransfer et = next(this);
			if (et == null) return eet;
			return et.transTo(this, eet);
		} else {
			//如果连接的有方块
			ElectricityUser user;
			for (TileEntity te : linkBlock.values()) {
				//若连接的是用电器
				if (te instanceof ElectricityUser) {
					user = (ElectricityUser) te;
					//检查是否需要电力
					if (user.needEle) {
						user.needEle = false;
						//检查电压是否符合，不符合的话增加超载时长，符合的话清零计时
						if (user.canUse(getVoltage())) {
							user.biggerTime = 0;
							if (users.containsKey(this)) {
								users.get(this).add(user);
							} else {
								List<ElectricityUser> u = new ArrayList<>(4);
								u.add(user);
								users.put(this, u);
							}
						} else {
							++user.biggerTime;
						}
					}
				}
				energy.me += EleUtils.energy(world, te.getPos(), te.getBlockType(), te);
			}
			eet.need += energy.me;
			ElectricityTransfer et = next(this);
			//若存在下一根电线则递归调用
			//若不存在下一根电线则继续运行并添加标记
			if (et != null) return et.transTo(this, eet);
		}
		
		//计算路径并添加相关标记
		List<ElectricityTransfer> list;
		List<ElectricityUser> notEnough = new ArrayList<>();
		o : for (ElectricityMaker maker : infos.makers) {
			switch (maker.output(energy, false)) {
				case YES:
					//更新途径的电缆的信息
					list = findETFromBlock(maker, this);
					list.forEach(et -> et.nowEE.setEnergy(et.nowEE.getEnergy() + energy.me));
					users.values().forEach(l -> l.forEach(eu -> {
						eu.useElectricity(eu.getMe(), eu.getVoltage());
					}));
					maker.output(energy, true);
					break o;
				case NOT_ENOUGH:
					//若电力不足以完全支持运行
					list = findETFromBlock(maker, this);
					//存储当前电缆连接的用电器
					List<ElectricityUser> lu;
					//存储当前需要的电能
					ElectricityMaker.Energy e = new ElectricityMaker.Energy();
					e.voltage = energy.voltage;
					for (ElectricityTransfer et : list) {
						lu = users.getOrDefault(et, null);
						double d = e.me;
						e.me += EETransfer.calculationLoss(this);
						//若线缆没有连接任何电器则直接进入下一根线缆
						if (lu == null) continue;
						//保存当前用电器
						ElectricityUser eu;
						//记录需要删除的元素
						Set<Integer> removes = new HashSet<>();
						for (int i = 0; i < lu.size(); ++i) {
							eu = lu.get(i);
							e.me += EleUtils.energy(world, eu.getPos(), eu.getBlockType(), eu);
							switch (maker.output(e, false)) {
								case YES:
									//若可以完全运行则删除当前元素保证不会重复计算
									removes.add(i);
									break;
								case FAILURE:
									//若完全不能运行则回滚数据并输出电能
									e.me = d;
									maker.output(e, true);
									e.me = 0;
									break;
								case NOT_ENOUGH:
									notEnough.add(eu);
									break;
							}
						}
						for (Integer remove : removes) {
							lu.remove(remove.intValue());
						}
					}
					break;
				default: break;
			}
			
		}
		/*
			遍历不能完全运行的用电器
			因为算法问题，不能保证所有用电器能完美的使用可用能源，
			可能会出现能源分配不均等现象
		 */
		ElectricityUser eu;
		for (ElectricityMaker maker : infos.makers) {
			Set<Integer> removes = new HashSet<>();
			for (int i = 0; i < notEnough.size(); ++i) {
				eu = notEnough.get(i);
				if (eu.canUse(maker.getMeBox(), maker.getVoltage())) {
					eu.useElectricity(maker.getMeBox(), maker.getVoltage());
					removes.add(i);
				}
			}
			for (Integer remove : removes) {
				notEnough.remove(remove.intValue());
			}
		}
		return null;
	}
	
	/**
	 * 取消所有连接
	 * @param isDelte 是否取消连接方块对该方块的连接
	 */
	public final void deleteAllLink(boolean isDelte) {
		if (next != null) {
			deleteLinkInfo(next);
			if (isDelte) next.deleteLink(this);
			next = null;
		}
		if (prev != null) {
			deleteLinkInfo(prev);
			if (isDelte) prev.deleteLink(this);
			prev = null;
		}
	}
	
	/**
	 * 删除指定连接，该函数实现与{@link #deleteLink(TileEntity)}功能相同，
	 * 不过该函数使用坐标判断是否删除而不是使用TE，若pos不在连接列表中，
	 * 则不会发生任何事情
	 *
	 * @param pos 要删除的连接坐标，为null时不会做任何事情
	 */
	public final void deleteLink(BlockPos pos) {
		if (pos == null) return;
		if (pos.equals((next == null) ? null : next.pos)) {
			deleteLinkInfo(next);
			next = null;
		} else if (pos.equals((prev == null) ? null : prev.pos)) {
			deleteLinkInfo(prev);
			prev = null;
		} else {
			EnumFacing key = null;
			TileEntity te = null;
			for (Map.Entry<EnumFacing, TileEntity> m : linkBlock.entrySet()) {
				if (pos.equals(m.getValue().getPos())) {
					key = m.getKey();
					te = m.getValue();
					break;
				}
			}
			if (te == null) return;
			if (te instanceof ElectricityMaker) {
				deleteLinkInfo((ElectricityMaker) te);
			}
			linkBlock.remove(key);
		}
	}
	
	/**
	 * 删除与该电线的特定连接
	 *
	 * @param e 要删除的连接，可以为null，为null时不会发生任何事情
	 *
	 * @throws IndexOutOfBoundsException e不在连接列表中时抛出
	 */
	public final void deleteLink(TileEntity e) {
		if (e == null) return;
		if (e instanceof ElectricityTransfer) {
			if (e.equals(next)) {
				deleteLinkInfo(next);
				next = null;
			} else if (e.equals(prev)) {
				deleteLinkInfo(prev);
				prev = null;
			} else {
				if (FMLCommonHandler.instance().getSide().isClient())
					MISysInfo.err("[" + pos + "未知的连接：" + e.getPos());
				else
					throw new IndexOutOfBoundsException("[" + pos + "未知的连接：" + e.getPos());
			}
		} else {
			if (linkBlock.containsValue(e)) {
				EnumFacing key = null;
				TileEntity te = null;
				for (Map.Entry<EnumFacing, TileEntity> m : linkBlock.entrySet()) {
					if (m.getValue().equals(e)) {
						key = m.getKey();
						te = m.getValue();
						break;
					}
				}
				if (key == null) {
					throw new IndexOutOfBoundsException("未知的连接：" + e);
				} else {
					if (te instanceof ElectricityMaker) {
						deleteLinkInfo((ElectricityMaker) te);
					}
					linkBlock.remove(key);
				}
			}
		}
	}
	
	/** 获取连接的所有的电线 */
	public final BlockPos[] getLinks() { return new BlockPos[]{ ((next == null) ? null :
			                                                       next.pos), ((prev == null) ? null : prev.pos)}; }
	/** 获取连接的方块，不包括传输设备 */
	public Map<EnumFacing, TileEntity> getLinkBlock() { return linkBlock; }
	/** 获取上一根电线 */
	public final ElectricityTransfer getPrev() { return prev; }
	/** 获取下一根电线 */
	public final ElectricityTransfer getNext() { return next; }
	/** 获取上方是否连接方块 */
	public final boolean getUp() { return up; }
	/** 获取下方是否连接方块 */
	public final boolean getDown() { return down; }
	/** 获取东方是否连接方块 */
	public final boolean getEast() { return east; }
	/** 获取西方是否连接方块 */
	public final boolean getWest() { return west; }
	/** 获取南方是否连接方块 */
	public final boolean getSouth() { return south; }
	/** 获取北方是否连接方块 */
	public final boolean getNorth() { return north; }
	/** 获取电力损耗指数 */
	public final int getLoss() { return loss; }
	/** 获取是否绝缘 */
	public final boolean isInsulation() {
		return ((WireBlock) getBlockType()).isInsulation();
	}
	/** 设置是否绝缘 */
	public final void setInsulation(boolean isInsulation) { this.isInsulation = isInsulation; }
	/** 设置上方是否连接方块 */
	public final void setUp(boolean value) {
		up = value;
	}
	/** 设置下方是否连接方块 */
	public final void setDown(boolean value) { down = value; }
	/** 设置东方是否连接方块 */
	public final void setEast(boolean value) { east = value; }
	/** 设置西方是否连接方块 */
	public final void setWest(boolean value) { west = value; }
	/** 设置北方是否连接方块 */
	public final void setNorth(boolean value) { north = value; }
	/** 设置南方是否连接方块 */
	public final void setSouth(boolean value) { south = value; }
	/** 设置电力损耗指数 */
	public final void setLoss(int loss) { this.loss = loss; }
	
	@Override
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}
	
	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}
	
	/**
	 * 电力传输工具类，其中提供了一些额外的方法与参数
	 */
	public final static class EETransfer {
		
		/** 存储用电器 */
		protected Map<ElectricityUser, ElectricityEnergy> users = new LinkedHashMap<>();
		
		/** 存储发电机 */
		protected List<ElectricityMaker> makers = new ArrayList<>();
		
		/** 最先调用的ET对象，可以为null */
		protected final ElectricityTransfer ET;
		
		/** 当前方块的ET，用于计算损耗 */
		protected ElectricityTransfer nowET;
		
		/** 电压 */
		public final double VOLTAGE;
		
		/** 已经损失的电能 */
		public double loss = 0;
		
		/** 用户请求的电能 */
		public int need = 0;
		
		/**
		 * @param et 调用的ET可为null
		 */
		public EETransfer(ElectricityTransfer et) {
			ET = et;
			VOLTAGE = et.getEnergy().getVoltage();
		}
		
		/** 计算传输设备消耗的电能 */
		public static double calculationLoss(ElectricityTransfer et) {
			return ((double) et.getEnergy().getVoltage()) / et.getLoss();
		}
		
	}
	
}
