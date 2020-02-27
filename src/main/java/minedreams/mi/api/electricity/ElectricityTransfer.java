package minedreams.mi.api.electricity;

import javax.annotation.Nonnull;
import java.util.*;

import minedreams.mi.api.electricity.cache.WireLinkInfo;
import minedreams.mi.api.electricity.clock.OverloadCounter;
import minedreams.mi.api.electricity.info.*;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.api.net.info.InfoBooleans;
import minedreams.mi.api.net.message.MessageList;
import minedreams.mi.register.te.AutoTileEntity;
import minedreams.mi.tools.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

/**
 * 电力传输设备的TE
 *
 * @author EmptyDremas
 * @version 1.0
 */
@AutoTileEntity("PARENT_ELECTRICITY_TRANSFER")
public class ElectricityTransfer extends Electricity {
	
	/** 计数器 */
	public final OverloadCounter COUNTER = new OverloadCounter() {
		@Override
		public void overload() {
			World world = getWorld();
			BlockPos pos = getPos();
			for (int i = 0; i < 2; ++i) {
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				world.markBlockRangeForRenderUpdate(pos, pos);
				pos = Tools.randomPos(world, pos, Tools.ALL);
				if (pos == null) break;
			}
		}
	};
	
	public ElectricityTransfer() { }
	
	public ElectricityTransfer(int meMax, int biggerMaxTime) {
		this.meMax = meMax;
		this.biggerMaxTime = biggerMaxTime;
	}
	
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
	protected final Map<EnumFacing, TileEntity> linkBlock = new HashMap<>(6);
	/** 最大电流量 */
	protected int meMax = 5000;
	/** 当前电流量 */
	private int me = 0;
	/** 电力损耗指数，指数越大损耗越多 */
	protected int loss = 0;
	/** 所属电路缓存 */
	WireLinkInfo cache = new WireLinkInfo();
	
	/**
	 * 判断一个方块能否连接当前电线
	 * @param ele 要连接的方块
	 */
	public final boolean canLink(TileEntity ele) {
		if (ele == null) return false;
		
		if (ele instanceof ElectricityTransfer) {
			if (ele.equals(next) || ele.equals(prev)) return true;
			return next == null || prev == null;
		}
		
		if (ele instanceof Electricity) {
			EnumFacing facing = Tools.whatFacing(getPos(), ele.getPos());
			return linkBlock.getOrDefault(facing, null) == null;
		}
		return false;
	}
	
	/**
	 * 获取下一根电线
	 * @param ele 调用该方法的运输设备，当{@link #getLinkAmount()} <= 1时可以为null
	 *
	 * @throws IllegalArgumentException 如果 ele == null 且 {@link #getLinkAmount()} > 1
	 */
	public final ElectricityTransfer next(ElectricityTransfer ele) {
		if (ele == null) {
			if (next == null) {
				if (prev == null) return null;
				return prev;
			}
			if (prev == null) return next;
			throw new IllegalArgumentException("ele == null，信息不足！");
		} else {
			if (next != null && next.equals(ele)) return prev;
			if (prev != null && prev.equals(ele)) return next;
			return null;
		}
	}
	
	@Override
	protected final void sonRun() { }
	
	/** 在客户端存储电线连接数量 */
	private int _amount = 0;
	
	/**
	 * 获取已经连接的电线的数量
	 */
	public int getLinkAmount() {
		if (world.isRemote) {
			return _amount;
		} else {
			if (next == null) {
				if (prev == null) return 0;
				return 1;
			}
			if (prev == null) return 1;
			return 2;
		}
	}
	
	/**
	 * 获取电线连接的所有发电机
	 * @return 返回的列表可以随意修改
	 */
	@Nonnull
	public List<ElectricityMaker> getLinkMaker() {
		List<ElectricityMaker> list = new ArrayList<>(6);
		for (TileEntity entity : linkBlock.values()) {
			if (entity instanceof ElectricityMaker) list.add((ElectricityMaker) entity);
		}
		return list;
	}
	
	/**
	 * 连接一个方块. 这个方块可能是用电器也可能是传输设备或原版方块，
	 * 这个需要用户自行检测，该方法中不应该检查是否允许连接
	 * @param entity 调用方块
	 * @return 连接成功返回true，否则返回false
	 */
	public boolean link(TileEntity entity) {
		if (world.isRemote) return false;
		if (entity == null) return false;
		if (entity == this) return false;
		if (entity instanceof ElectricityTransfer) {
			ElectricityTransfer et = (ElectricityTransfer) entity;
			if (entity.equals(getNext()) || entity.equals(getPrev())) return true;
			if (linkForce(et)) {
				if (et.linkForce(this)) {
					et.updateLink();
					markDirty();
					return true;
				} else {
					deleteLink(et.getPos());
				}
			} else {
				return false;
			}
		} else {
			if (!(EleUtils.canLink(new LinkInfo(getWorld(), getPos(), entity.getPos(),
							getBlockType(), entity.getBlockType()),
					true, false))) return false;
			
			if (entity instanceof ElectricityUser) {
				((ElectricityUser) entity).link(this);
			}
			
			EnumFacing facing = Tools.whatFacing(getPos(), entity.getPos());
			if (getLinkBlock().containsKey(facing)) {
				if (getLinkBlock().get(facing) != null) return false;
			}
			getLinkBlock().put(facing, entity);
			switch (facing) {
				case DOWN: setDown(true); break;
				case UP: setUp(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case WEST: setWest(true); break;
				default: setEast(true); break;
			}
			markDirty();
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
		return true;
	}
	
	/**
	 * 连接一个方块. 这个方块可能是用电器也可能是传输设备或原版方块，这个需要用户自行检测
	 * @param pos 调用方块
	 * @return 连接成功返回true，否则返回false
	 */
	public final boolean link(BlockPos pos) {
		return link(pos == null ? null : world.getTileEntity(pos));
	}
	
	/**
	 * 强制连接一个运输设备
	 *
	 * @return 是否连接成功
	 *
	 * @throws NullPointerException 如果et == null
	 */
	public final boolean linkForce(ElectricityTransfer et) {
		WaitList.checkNull(et, "et");
		
		if (next == null) next = et;
		else if (prev == null) prev = et;
		else return false;
		
		updateLink();
		return true;
	}
	
	/**
	 * 根据已经连接的电线更新数据
	 */
	public void updateLink() {
		setEast(false);
		setWest(false);
		setNorth(false);
		setSouth(false);
		setUp(false);
		setDown(false);
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
		for (Map.Entry<EnumFacing, TileEntity> entry : linkBlock.entrySet()) {
			if (entry.getValue() != null) {
				switch (entry.getKey()) {
					case EAST: setEast(true); break;
					case WEST: setWest(true); break;
					case SOUTH: setSouth(true); break;
					case NORTH: setNorth(true); break;
					case UP: setUp(true); break;
					default: setDown(true);
				}
			}
		}
		markDirty();
	}
	
	/**
	 * 因为在radFromNBT阶段世界没有加载完毕，调用world.getTileEntity会返回null，
	 * 所有建立一个临时列表在用户调用next方法的时候检查是否需要更新参数，
	 * 当cachePos==null的时候标志已经更新，不需要再次更新
	 */
	private BlockPos[] cachePos = new BlockPos[2];
	private EnumFacing[] cacheFacing;
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		up = compound.getBoolean("up");
		down = compound.getBoolean("down");
		east = compound.getBoolean("east");
		west = compound.getBoolean("west");
		south = compound.getBoolean("south");
		north = compound.getBoolean("north");
		
		if (compound.getBoolean("hasNext")) {
			cachePos[0] = Tools.readBlockPos(compound, "next");
		}
		if (compound.getBoolean("hasPrev")) {
			cachePos[1] = Tools.readBlockPos(compound, "prev");
		}
		
		int size = compound.getInteger("maker_size");
		cacheFacing = new EnumFacing[size];
		for (int i = 0; i < size; ++i) {
			cacheFacing[i] = EnumFacing.getFront(compound.getInteger("maker_facing_" + i));
		}
		
		cache.readFromNBT(compound);
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
			Tools.writeBlockPos(compound, next.getPos(), "next");
		}
		if (prev != null) {
			Tools.writeBlockPos(compound, prev.getPos(), "prev");
		}
		
		int size = 0;
		for (Map.Entry<EnumFacing, TileEntity> entry : linkBlock.entrySet()) {
			if (entry.getValue() instanceof ElectricityMaker) {
				compound.setInteger("maker_facing_" + size, entry.getKey().getIndex());
				++size;
			}
		}
		compound.setInteger("maker_size", size);
		
		if (cache.isNeedSave()) {
			cache.setIsNeedSave(false);
			cache.writeToNBT(compound, this);
		}
		
		return compound;
	}
	
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	private final List<String> players = new ArrayList<>(1);
	
	@Override
	public void reveive(@Nonnull MessageList list) {
		InfoBooleans info = (InfoBooleans) list.readInfo("bools");
		List<Boolean> bools = info.getInfos();
		up = bools.get(0);
		down = bools.get(1);
		east = bools.get(2);
		west = bools.get(3);
		south = bools.get(4);
		north = bools.get(5);
		_amount = list.readInt("amount");
		world.markBlockRangeForRenderUpdate(pos, pos);
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
				if (cacheFacing != null) {
					for (EnumFacing facing : cacheFacing)
						linkBlock.put(facing, world.getTileEntity(Tools.getBlockPos(pos, facing, 1)));
				}
				cache.updateInfo(world);
				cacheFacing = null;
				cachePos = null;
			}
			
			if (players.size() == world.playerEntities.size()) return null;
			
			//新建消息
			MessageList ml = new MessageList();
			{
				/* 存储电线的连接方向 */
				InfoBooleans bools = new InfoBooleans();
				
				bools.add(getUp());
				bools.add(getDown());
				bools.add(getEast());
				bools.add(getWest());
				bools.add(getSouth());
				bools.add(getNorth());
				ml.writeInfo("bools", bools);
				ml.writeInt("amount", getLinkAmount());
			}
			
			//遍历所有玩家
			for (EntityPlayer player : world.playerEntities) {
				//如果玩家已经更新过则跳过
				if (players.contains(player.getName())) continue;
				
				//判断玩家是否在范围之内（判断方法借用World中的代码）
				double d = player.getDistance(pos.getX(), pos.getY(), pos.getZ());
				if (d < 4096) {
					if (player instanceof EntityPlayerMP) {
						players.add(player.getName());
						ml.addPlayer((EntityPlayerMP) player);
					}
				}
			}
			return ml;
		}
		return null;
	}
	
	/**
	 * 遍历整条线路，以当前电线为起点
	 * @param run 要运行的指令
	 */
	public final void forEachAll(IETForEach run) {
		if (getLinkAmount() == 1) {
			forEach(null, run);
		} else {
			forEach(next, run, true);
			forEach(prev, run, false);
		}
	}
	
	/**
	 * 向指定方向遍历线路
	 * @param prev 上一根电线
	 * @param run 要运行的内容
	 */
	public final void forEach(ElectricityTransfer prev, IETForEach run) {
		forEach(prev, run, true);
	}
	
	/**
	 * 向指定方向遍历线路
	 * @param prev 上一根电线
	 * @param run 要运行的内容
	 * @param isNow 是否遍历当前电线
	 */
	private void forEach(ElectricityTransfer prev, IETForEach run, boolean isNow) {
		ElectricityTransfer old = prev;
		if (isNow && !run.run(this)) return;
		prev = this;
		for (ElectricityTransfer et = next(old); !(et == null || et == this); et = et.next(prev), prev = old) {
			if (et == this) break;
			old = et;
			if (run.run(et)) continue;
			break;
		}
	}
	
	/**
	 * 删除指定连接，若pos不在连接列表中，则不会发生任何事情
	 * @param pos 要删除的连接坐标，为null时不会做任何事情
	 */
	public final void deleteLink(BlockPos pos) {
		if (pos == null) return;
		if (next != null && pos.equals(next.pos)) {
			if (next.getLinkAmount() == 1) {
				cache.plusMakerAmount(-next.getLinkMaker().size());
				next.setCache(null);
				next = null;
			} else {
				next = null;
				WireLinkInfo.calculateCache(this);
			}
			updateLink();
		} else if (prev != null && pos.equals(prev.pos)) {
			if (prev.getLinkAmount() == 1) {
				cache.plusMakerAmount(-prev.getLinkMaker().size());
				prev.setCache(null);
				prev = null;
			} else {
				prev = null;
				WireLinkInfo.calculateCache(this);
			}
			updateLink();
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
			linkBlock.remove(key);
			updateLink();
		}
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		players.clear();
	}
	
	/** 设置线路缓存 */
	public final void setCache(WireLinkInfo info) { this.cache = info; }
	/** 获取线路缓存 */
	public final WireLinkInfo getCache() { return cache; }
	
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
	/** 获取损耗值 */
	public final int getLoss(EnumVoltage voltage) {
		return voltage.getLossIndex() * loss / 2;
	}
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
	/** 设置上一个电线 */
	public final void setPrev(ElectricityTransfer et) { prev = et; }
	/** 设置下一根电线 */
	public final void setNext(ElectricityTransfer et) { next = et; }
	/** 设置最大电流指数 */
	public final void setMeMax(int max) { meMax = max; }
	/** 获取最大电流指数 */
	public final int getMeMax() { return meMax; }
	/** 获取当前电流量 */
	public final int getMe() { return me; }
	/** 通过电流 */
	public final void transfer(int me) { this.me += me; }
	
	@Override
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}
	
	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}
	
}
