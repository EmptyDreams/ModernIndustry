package minedreams.mi.api.electricity.src.tileentity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minedreams.mi.api.electricity.EleWorker;
import minedreams.mi.api.electricity.clock.OverloadCounter;
import minedreams.mi.api.electricity.interfaces.IEleTransfer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import minedreams.mi.api.electricity.src.cache.WireLinkInfo;
import minedreams.mi.api.electricity.src.info.IETForEach;
import minedreams.mi.api.net.IAutoNetwork;
import minedreams.mi.api.net.NetworkRegister;
import minedreams.mi.api.net.WaitList;
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
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
@AutoTileEntity("IN_FATHER_ELECTRICITY_TRANSFER")
public class EleSrcCable extends Electricity implements IAutoNetwork {
	
	public EleSrcCable() {
		NetworkRegister.register(this);
	}
	public EleSrcCable(int meMax, int loss) {
		this();
		this.meMax = meMax;
		this.loss = loss;
	}
	
	/** 计数器 */
	private OverloadCounter counter = new OverloadCounter() {
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
	//六个方向是否连接
	private boolean up = false;
	private boolean down = false;
	private boolean east =false;
	private boolean west = false;
	private boolean south = false;
	private boolean north = false;
	/** 电线连接的方块，不包括电线方块 */
	private List<TileEntity> linkedBlocks = new ArrayList<>(5);
	/** 上一根电线 */
	private TileEntity prev = null;
	private IEleTransfer prevShip = null;
	/** 下一根电线 */
	private TileEntity next = null;
	private IEleTransfer nextShip = null;
	/** 最大电流量 */
	protected int meMax = 5000;
	/** 当前电流量 */
	private int me = 0;
	/** 电力损耗指数，指数越大损耗越多 */
	protected int loss = 0;
	/** 所属电路缓存 */
	WireLinkInfo cache = null;
	/** 在客户端存储电线连接数量 */
	private int _amount = 0;
	
	/**
	 * 判断一个方块能否连接当前电线
	 * @param target 要连接的方块
	 */
	public boolean canLink(TileEntity target) {
		if (EleWorker.isTransfer(target)) {
			return prev == null || prev.equals(target) ||
					       next == null || next.equals(target);
		}
		return EleWorker.isOutputer(target) || EleWorker.isInputer(target);
	}
	
	/**
	 * 获取下一根电线
	 * @param from 调用该方法的运输设备，当{@link #getLinkAmount()} <= 1时可以为null
	 *
	 * @throws IllegalArgumentException 如果 ele == null 且 {@link #getLinkAmount()} > 1
	 */
	public TileEntity next(TileEntity from) {
		if (from == null) {
			if (next == null) {
				if (prev == null) return null;
				return prev;
			}
			if (prev == null) return next;
			throw new IllegalArgumentException("from == null，信息不足！");
		}
		if (from.equals(next)) return prev;
		if (from.equals(prev)) return next;
		return null;
	}
	
	/** 获取已经连接的电线的数量 */
	public int getLinkAmount() {
		if (world.isRemote) {
			return _amount;
		} else {
			if (prev == null) {
				if (next == null) return 0;
				return 1;
			}
			if (next == null) return 1;
			return 2;
		}
	}
	
	/**
	 * 连接一个方块. 这个方块可能是任意类型的方块，这个需要用户自行检测
	 * @param target 要连接的方块
	 * @return 连接成功返回true，否则返回false
	 */
	public boolean link(TileEntity target) {
		if (target == null || target == this || world.isRemote) return false;
		if (cache == null) update();
		IEleTransfer et = EleWorker.getTransfer(target);
		if (et != null) {
			if (!et.canLink(target, this)) return false;
			if (next == null) {
				if (prev == null || !prev.equals(target)) {
					next = target;
					nextShip = et;
					cache.merge(nextShip.getLineCache(next));
					updateLinkShow();
					return true;
				}
			} else if (next.equals(target)) {
				return true;
			} else if (prev == null) {
				prev = target;
				prevShip = et;
				cache.merge(prevShip.getLineCache(prev));
				updateLinkShow();
				return true;
			} else return prev.equals(target);
			return false;
		}
		if (EleWorker.isInputer(target) || EleWorker.isOutputer(target)) {
			if (!linkedBlocks.contains(target)) linkedBlocks.add(target);
			updateLinkShow();
			return true;
		}
		return false;
	}
	
	/**
	 * 连接一个方块
	 * @param pos 要连接的方块
	 * @return 连接成功返回true，否则返回false
	 */
	public final boolean link(BlockPos pos) {
		return link(pos == null ? null : world.getTileEntity(pos));
	}
	
	public void updateLinkShow() {
		setEast(false);
		setWest(false);
		setNorth(false);
		setSouth(false);
		setUp(false);
		setDown(false);
		if (next != null) {
			switch (Tools.whatFacing(pos, next.getPos())) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
		if (prev != null) {
			switch (Tools.whatFacing(pos, prev.getPos())) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
		for (TileEntity block : linkedBlocks) {
			switch (Tools.whatFacing(pos, block.getPos())) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
		markDirty();
		players.clear();
	}
	
	/**
	 * 删除指定连接，若pos不在连接列表中，则不会发生任何事情
	 * @param pos 要删除的连接坐标，为null时不会做任何事情
	 */
	public final void deleteLink(BlockPos pos) {
		if (pos == null) return;
		if (next != null && pos.equals(next.getPos())) {
			if (next instanceof EleSrcCable) {
				EleSrcCable next = (EleSrcCable) this.next;
				if (next.getLinkAmount() == 1) {
					cache.plusMakerAmount(-nextShip.getOutputerAround(next).size());
					next.setCache(null);
					this.next = null;
				} else {
					this.next = null;
					WireLinkInfo.calculateCache(this);
				}
			} else {
				if (nextShip.getLinkAmount(next) == 1) {
					cache.plusMakerAmount(-nextShip.getLineCache(next).getOutputerAmount());
					nextShip.setLineCache(next, nextShip.createLineCache(next));
					this.next = null;
				} else {
					cache.disperse(nextShip.getLineCache(next));
					this.next = null;
					WireLinkInfo.calculateCache(this);
				}
				
			}
		} else if (prev != null && pos.equals(prev.getPos())) {
			if (prev instanceof EleSrcCable) {
				EleSrcCable prev = (EleSrcCable) this.prev;
				if (prev.getLinkAmount() == 1) {
					cache.plusMakerAmount(-prevShip.getOutputerAround(prev).size());
					prev.setCache(null);
					this.prev = null;
				} else {
					this.prev = null;
					WireLinkInfo.calculateCache(this);
				}
			} else {
				if (prevShip.getLinkAmount(prev) == 1) {
					cache.plusMakerAmount(-prevShip.getLineCache(prev).getOutputerAmount());
					prevShip.setLineCache(next, prevShip.createLineCache(prev));
					prev = null;
				} else {
					cache.disperse(prevShip.getLineCache(prev));
					prev = null;
					WireLinkInfo.calculateCache(this);
				}
			}
		} else {
			linkedBlocks.remove(world.getTileEntity(pos));
		}
		updateLinkShow();
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
	public final void forEach(TileEntity prev, IETForEach run) {
		forEach(prev, run, true);
	}
	
	/**
	 * 向指定方向遍历线路
	 * @param prev 上一根电线
	 * @param run 要运行的内容
	 * @param isNow 是否遍历当前电线
	 */
	private void forEach(TileEntity prev, IETForEach run, boolean isNow) {
		TileEntity next = next(prev);
		prev = this;
		if (!(next instanceof EleSrcCable))
			if (isNow && !run.run(this, true, next)) return;
		else
			if (isNow && !run.run(this, false, null)) return;
		for (EleSrcCable et = (EleSrcCable) next; !(et == null || et == this); et = (EleSrcCable) next) {
			next = et.next(prev);
			prev = et;
			if (next instanceof EleSrcCable) {
				if (run.run(et, false, null)) continue;
			} else {
				run.run(et, true, next);
				break;
			}
			break;
		}
	}
	
	//--------------------常规--------------------//
	
	private static final WireLinkInfo CLIENT_CACHE = new WireLinkInfo();
	@Override
	public void update() {
		if (cache == null) {
			if (world.isRemote) cache = CLIENT_CACHE;
			else {
				WireLinkInfo.calculateCache(this);
				if (cachePos != null) {
					if (cachePos[0] != null) next = world.getTileEntity(cachePos[0]);
					if (cachePos[1] != null) prev = world.getTileEntity(cachePos[1]);
					if (cacheFacing != null) {
						for (EnumFacing facing : cacheFacing)
							linkedBlocks.add(world.getTileEntity(Tools.getBlockPos(pos, facing, 1)));
					}
					cacheFacing = null;
					cachePos = null;
				}
			}
		}
	}
	
	/** 设置最大电流指数 */
	public final void setMeMax(int max) { meMax = max; }
	/** 获取最大电流指数 */
	public final int getMeMax() { return meMax; }
	/** 获取当前电流量 */
	public final int getTransfer() { return me; }
	/** 通过电流 */
	public final void transfer(int me) { this.me += me; }
	/** 电流归零 */
	public final void clearTransfer() { me = 0; }
	/** 设置线路缓存 */
	public final void setCache(WireLinkInfo info) { this.cache = info; }
	/** 获取线路缓存 */
	public final WireLinkInfo getCache() { return cache; }
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
	/** 获取损耗值 */
	public final int getLoss(IVoltage voltage) {
		return voltage.getLossIndex() * loss / 2;
	}
	/** 设置电力损耗指数 */
	public final void setLoss(int loss) { this.loss = loss; }
	/** 获取上一根电线 */
	public final TileEntity getPrev() { return prev; }
	/** 获取下一根电线 */
	public final TileEntity getNext() { return next; }
	/** 获取连接的方块. 返回的列表可以随意修改 */
	public final List<TileEntity> getLinkedBlocks() { return new ArrayList<>(linkedBlocks); }
	/** 获取计数器 */
	public final OverloadCounter getCounter() { return counter; }
	/** 设置计数器 */
	public final void setCounter(OverloadCounter counter) {
		WaitList.checkNull(counter, "counter");
		this.counter = counter;
	}
	
	/*
	 * 因为在radFromNBT阶段世界没有加载完毕，调用world.getTileEntity会返回null，
	 * 所有建立一个临时列表在用户调用next方法的时候检查是否需要更新参数，
	 * 当cachePos==null的时候标志已经更新，不需要再次更新
	 */
	private BlockPos[] cachePos = new BlockPos[2];
	private EnumFacing[] cacheFacing;
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	private final List<String> players = new ArrayList<>(1);
	
	@Override
	public void receive(@Nonnull NBTTagCompound message) {
		up = message.getBoolean("up");
		down = message.getBoolean("down");
		east = message.getBoolean("east");
		west = message.getBoolean("west");
		south = message.getBoolean("south");
		north = message.getBoolean("north");
		_amount = message.getInteger("amount");
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	/**
	 * 这其中写有更新内部数据的代码，重写时应该调用
	 *
	 * @return null
	 */
	@Override
	public NBTTagCompound send() {
		if (world.isRemote) return null;
		
		if (players.size() == world.playerEntities.size()) return null;
		Set<String> sendPlayers = new HashSet<>();
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("up", up);
		compound.setBoolean("down", down);
		compound.setBoolean("south", south);
		compound.setBoolean("north", north);
		compound.setBoolean("west", west);
		compound.setBoolean("east", east);
		
		//遍历所有玩家
		for (EntityPlayer player : world.playerEntities) {
			//如果玩家已经更新过则跳过
			if (players.contains(player.getName())) continue;
			
			//判断玩家是否在范围之内（判断方法借用World中的代码）
			double d = player.getDistance(pos.getX(), pos.getY(), pos.getZ());
			if (d < 4096) {
				if (player instanceof EntityPlayerMP) {
					players.add(player.getName());
					sendPlayers.add(player.getName());
				}
			}
		}
		
		compound.setInteger("playerAmount", sendPlayers.size());
		int i = 0;
		for (String player : sendPlayers) {
			compound.setString("player" + i, player);
			++i;
		}
		
		return compound;
	}
	
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
			cacheFacing[i] = EnumFacing.getFront(compound.getInteger("facing_" + i));
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
			Tools.writeBlockPos(compound, next.getPos(), "next");
		}
		if (prev != null) {
			Tools.writeBlockPos(compound, prev.getPos(), "prev");
		}
		
		int size = 0;
		for (TileEntity te : linkedBlocks) {
			compound.setInteger("facing_" + size++, Tools.whatFacing(pos, te.getPos()).getIndex());
		}
		compound.setInteger("maker_size", size);
		
		return compound;
	}
	
	@Override
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}
	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}
	
}
