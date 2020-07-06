package xyz.emptydreams.mi.blocks.te;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import xyz.emptydreams.mi.api.electricity.EleWorker;
import xyz.emptydreams.mi.api.electricity.capabilities.EleCapability;
import xyz.emptydreams.mi.api.electricity.capabilities.ILink;
import xyz.emptydreams.mi.api.electricity.capabilities.IStorage;
import xyz.emptydreams.mi.api.electricity.capabilities.LinkCapability;
import xyz.emptydreams.mi.api.electricity.clock.OrdinaryCounter;
import xyz.emptydreams.mi.api.electricity.clock.OverloadCounter;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleTransfer;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.NetworkRegister;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.utils.BlockPosUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.DataType;
import xyz.emptydreams.mi.api.utils.data.TEHelper;
import xyz.emptydreams.mi.data.info.BiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.data.info.IETForEach;
import xyz.emptydreams.mi.data.info.WireLinkInfo;
import xyz.emptydreams.mi.register.te.AutoTileEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static xyz.emptydreams.mi.api.utils.data.DataType.*;

/**
 * @author EmptyDreams
 * @version V2.0
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY_TRANSFER")
public class EleSrcCable extends TileEntity implements IAutoNetwork, ITickable, TEHelper {
	
	public EleSrcCable() {
		NetworkRegister.register(this);
	}
	public EleSrcCable(int meMax, double loss) {
		this();
		this.meMax = meMax;
		this.loss = loss;
	}
	
	/** 计数器 */
	private OverloadCounter counter;
	//六个方向是否连接
	@Storage(BOOLEAN) private boolean up = false;
	@Storage(BOOLEAN) private boolean down = false;
	@Storage(BOOLEAN) private boolean east =false;
	@Storage(BOOLEAN) private boolean west = false;
	@Storage(BOOLEAN) private boolean south = false;
	@Storage(BOOLEAN) private boolean north = false;
	/** 电线连接的方块，不包括电线方块 */
	@Storage
	private final List<BlockPos> linkedBlocks = new ArrayList<BlockPos>(5) {
		private static final long serialVersionUID = 8683452581122892180L;
		@Override
		public boolean add(BlockPos tileEntity) {
			WaitList.checkNull(tileEntity, "tileEntity");
			return super.add(tileEntity);
		}
	};
	/** 上一根电线 */
	@Storage(POS) private BlockPos prev = null;
	private IEleTransfer prevShip = null;
	/** 下一根电线 */
	@Storage(POS) private BlockPos next = null;
	private IEleTransfer nextShip = null;
	/** 最大电流量 */
	protected int meMax = 5000;
	/** 当前电流量 */
	private int me = 0;
	/** 电力损耗指数，指数越大损耗越多 */
	protected double loss = 0;
	/** 所属电路缓存 */
	WireLinkInfo cache = null;
	/** 在客户端存储电线连接数量 */
	private int _amount = 0;
	/** 过载最长时间 */
	protected int biggerMaxTime = 50;
	
	/**
	 * 判断一个方块能否连接当前电线
	 * @param target 要连接的方块
	 */
	public boolean canLink(TileEntity target) {
		if (EleWorker.isTransfer(target)) {
			return prev == null || prev.equals(target.getPos()) ||
					       next == null || next.equals(target.getPos());
		}
		return EleWorker.isOutputer(target) || EleWorker.isInputer(target);
	}
	
	/**
	 * 获取下一根电线
	 * @param from 调用该方法的运输设备，当{@link #getLinkAmount()} <= 1时可以为null
	 *
	 * @throws IllegalArgumentException 如果 ele == null 且 {@link #getLinkAmount()} > 1
	 */
	public TileEntity next(BlockPos from) {
		if (from == null) {
			if (next == null) {
				if (prev == null) return null;
				return getPrev();
			}
			if (prev == null) return getNext();
			throw new IllegalArgumentException("from == null，信息不足！");
		}
		if (from.equals(next) && prev != null) return getPrev();
		if (from.equals(prev) && next != null) return getNext();
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
	public boolean link(BlockPos target) {
		if (target == null || target.equals(pos) || world.isRemote) return false;
		if (cache == null) update();
		TileEntity targetEntity = world.getTileEntity(target);
		if (targetEntity == null) return false;
		IEleTransfer et = EleWorker.getTransfer(targetEntity);
		if (et != null) {
			if (!et.canLink(targetEntity, this)) return false;
			if (next == null) {
				if (prev == null || !prev.equals(target)) {
					next = target;
					nextShip = et;
					cache.merge(nextShip.getLineCache(getNext()));
					updateLinkShow();
					return true;
				}
			} else if (next.equals(target)) {
				return true;
			} else if (prev == null) {
				prev = target;
				prevShip = et;
				cache.merge(prevShip.getLineCache(getPrev()));
				updateLinkShow();
				return true;
			} else return prev.equals(target);
		} else {
			EnumFacing facing = BlockPosUtil.whatFacing(target, pos);
			ILink link = targetEntity.getCapability(LinkCapability.LINK, facing);
			if (link == null) return false;
			if (link.canLink(facing)) {
				if (!linkedBlocks.contains(target)) linkedBlocks.add(target);
				updateLinkShow();
				return true;
			}
		}
		return false;
	}
	
	public void updateLinkShow() {
		setEast(false);
		setWest(false);
		setNorth(false);
		setSouth(false);
		setUp(false);
		setDown(false);
		if (next != null) {
			switch (BlockPosUtil.whatFacing(pos, next)) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
		if (prev != null) {
			switch (BlockPosUtil.whatFacing(pos, prev)) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
		for (BlockPos block : linkedBlocks) {
			switch (BlockPosUtil.whatFacing(pos, block)) {
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
		if (world.isRemote) return;
		if (pos == null) return;
		if (pos.equals(next)) {
			next = null;
			WireLinkInfo.calculateCache(this);
		} else if (pos.equals(prev)) {
			prev = null;
			WireLinkInfo.calculateCache(this);
		} else {
			linkedBlocks.remove(pos);
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
	public final void forEach(BlockPos prev, IETForEach run) {
		forEach(prev, run, true);
	}
	
	/**
	 * 向指定方向遍历线路
	 * @param prev 上一根电线
	 * @param run 要运行的内容
	 * @param isNow 是否遍历当前电线
	 */
	private void forEach(BlockPos prev, IETForEach run, boolean isNow) {
		TileEntity next = next(prev);
		prev = pos;
		if (!(next instanceof EleSrcCable)) {
			if (isNow && !run.run(this, true, next)) return;
		} else {
			if (isNow && !run.run(this, false, null)) return;
		}
		for (EleSrcCable et = (EleSrcCable) next; !(et == null || et == this); et = (EleSrcCable) next) {
			next = et.next(prev);
			prev = et.getPos();
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
			if (world.isRemote) {
				cache = CLIENT_CACHE;
				WorldUtil.removeTickable(this);
			} else {
				WireLinkInfo.calculateCache(this);
				nextShip = EleWorker.getTransfer(getNext());
				prevShip = EleWorker.getTransfer(getPrev());
			}
		}
		
		TileEntity entity;
		IStorage storage;
		for (BlockPos block : linkedBlocks) {
			entity = world.getTileEntity(block);
			if (entity == null) {
				deleteLink(block);
			} else {
				storage = entity.getCapability(EleCapability.ENERGY, BlockPosUtil.whatFacing(block, pos));
				if (storage != null && storage.canReceive()) {
					EleWorker.useEleEnergy(entity);
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
	public final double getLoss(EleEnergy energy) {
		double loss = energy.calculateLoss();
		return loss + loss * this.loss;
	}
	/** 设置电力损耗指数 */
	public final void setLoss(double loss) { this.loss = loss; }
	/** 获取上一根电线 */
	public final TileEntity getPrev() { return prev == null ? null : world.getTileEntity(prev); }
	/** 获取下一根电线 */
	public final TileEntity getNext() { return next == null ? null : world.getTileEntity(next); }
	/** 获取上一根电线的坐标 */
	public final BlockPos getPrevPos() { return prev; }
	/** 获取下一根电线的坐标 */
	public final BlockPos getNextPos() { return next; }
	/** 设置过载最长时间(单位：tick，默认值：50tick)，当设置时间小于0时保持原设置不变 */
	public void setBiggerMaxTime(int bvt) {
		biggerMaxTime = (bvt >= 0) ? bvt : biggerMaxTime;
		getCounter().setMaxTime(getBiggerMaxTime());
	}
	/** 获取最长过载时间 */
	public int getBiggerMaxTime() {
		return biggerMaxTime;
	}
	/** 设置方块类型 */
	public final void setBlockType(Block block) {
		blockType = block;
	}
	
	/** 获取连接的方块. 返回的列表可以随意修改 */
	public final List<TileEntity> getLinkedBlocks() {
		List<TileEntity> blocks = new ArrayList<>(linkedBlocks.size());
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < linkedBlocks.size(); i++) {
			blocks.add(world.getTileEntity(linkedBlocks.get(i)));
		}
		return blocks;
	}
	
	/** 获取计数器 */
	@Nonnull
	public final OverloadCounter getCounter() {
		if (counter == null) {
			BiggerVoltage bigger = new BiggerVoltage(2, EnumBiggerVoltage.FIRE);
			bigger.setFireRadius(1);
			OrdinaryCounter counter = new OrdinaryCounter(getBiggerMaxTime());
			counter.setPos(pos);
			counter.setWorld(world);
			counter.setBigger(new BiggerVoltage(1, EnumBiggerVoltage.FIRE));
			this.counter = counter;
		}
		return counter;
	}
	/** 设置计数器 */
	public final void setCounter(OverloadCounter counter) {
		WaitList.checkNull(counter, "counter");
		this.counter = counter;
	}
	
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
	
	@Override
	public NBTTagCompound getUpdateTag() {
		players.clear();
		return super.getUpdateTag();
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
				} else {
					players.remove(player.getName());
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
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}
	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return TEHelper.super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		TEHelper.super.readFromNBT(compound);
	}
	
	@Override
	public String toString() {
		return "EleSrcCable{ pos=" + pos + '}';
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
}
