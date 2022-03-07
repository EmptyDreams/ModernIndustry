package top.kmar.mi.content.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import top.kmar.mi.api.capabilities.ele.EleCapability;
import top.kmar.mi.api.capabilities.ele.EleStateEnum;
import top.kmar.mi.api.capabilities.ele.IStorage;
import top.kmar.mi.api.dor.ByteDataOperator;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.electricity.EleWorker;
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.electricity.clock.OverloadCounter;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.VoltageRange;
import top.kmar.mi.api.electricity.interfaces.IEleTransfer;
import top.kmar.mi.api.electricity.interfaces.IVoltage;
import top.kmar.mi.api.net.IAutoNetwork;
import top.kmar.mi.api.register.others.AutoTileEntity;
import top.kmar.mi.api.utils.IOUtil;
import top.kmar.mi.api.utils.StringUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.utils.data.io.Storage;
import top.kmar.mi.api.utils.data.io.instance.ObjectData;
import top.kmar.mi.data.info.BiggerVoltage;
import top.kmar.mi.data.info.CableCache;
import top.kmar.mi.data.info.EnumBiggerVoltage;
import top.kmar.mi.data.info.EnumVoltage;
import top.kmar.mi.data.info.IETForEach;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 默认电线
 * @author EmptyDreams
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY_TRANSFER")
public class EleSrcCable extends TileEntity implements IAutoNetwork, ITickable {
	
	//MC反射调用
	@SuppressWarnings("unused")
	public EleSrcCable() { }
	
	public EleSrcCable(int meMax, double loss) {
		this.meMax = meMax;
		this.loss = loss;
	}
	
	/** 计数器 */
	private OverloadCounter counter;
	/**
	 * 存储六个方向的连接信息<br>
	 * 从左到右依次为：上、下、东、南、西、北
	 */
	@Storage(byte.class) private int linkInfo = 0b000000;
	/** 电线连接的方块，不包括电线方块 */
	@Storage
	private final List<BlockPos> linkedBlocks = new ArrayList<BlockPos>(5) {
		private static final long serialVersionUID = 8683452581122892180L;
		@Override
		public boolean add(BlockPos tileEntity) {
			return super.add(StringUtil.checkNull(tileEntity, "tileEntity"));
		}
	};
	/** 上一根电线 */
	@Storage private BlockPos prev = null;
	/** 下一根电线 */
	@Storage private BlockPos next = null;
	/** 最大电流量 */
	protected int meMax;
	/** 当前电流量 */
	private int me = 0;
	/** 电力损耗指数，指数越大损耗越多 */
	protected double loss;
	/** 所属电路缓存 */
	CableCache cache = null;
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
		if (prev == null) {
			if (next == null) return 0;
			return 1;
		}
		if (next == null) return 1;
		return 2;
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
		if (et != null) return linkWire(et, targetEntity);
		return linkMachine(targetEntity);
	}
	
	/**
	 * 连接一根电线
	 * @param target 电线坐标
	 * @return 是否连接成功
	 */
	public boolean linkWire(IEleTransfer transfer, TileEntity target) {
		if (!transfer.canLink(target, this)) return false;
		if (next == null) {
			if (!target.getPos().equals(prev)) {
				next = target.getPos();
				updateLinkShow();
			}
			return true;
		} else if (prev == null) {
			prev = target.getPos();
			updateLinkShow();
			return true;
		} else {
			return prev.equals(target.getPos()) || next.equals(target.getPos());
		}
	}
	
	/**
	 * 连接一个机器
	 * @param target 机器坐标
	 * @return 是否连接成功
	 */
	public boolean linkMachine(TileEntity target) {
		EnumFacing facing = WorldUtil.whatFacing(target.getPos(), pos);
		IStorage link = target.getCapability(EleCapability.ENERGY, facing);
		if (link == null) return false;
		if (link.canLink(facing)) {
			if (!linkedBlocks.contains(target.getPos())) {
				linkedBlocks.add(target.getPos());
				if (EleWorker.isOutputer(target)) getCache().addOutputer(getPos(), target.getPos());
				updateLinkShow();
			}
			return true;
		}
		return false;
	}
	
	public void updateLinkShow() {
		linkInfo = 0;
		updateShowData(next);
		updateShowData(prev);
		linkedBlocks.forEach(this::updateShowData);
		updateTickableState();
		players.clear();
		markDirty();
	}
	
	private void updateShowData(BlockPos linked) {
		if (linked != null) {
			switch (WorldUtil.whatFacing(pos, linked)) {
				case EAST: setEast(true); break;
				case WEST: setWest(true); break;
				case SOUTH: setSouth(true); break;
				case NORTH: setNorth(true); break;
				case UP: setUp(true); break;
				default: setDown(true);
			}
		}
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
			CableCache.calculate(this);
		} else if (pos.equals(prev)) {
			prev = null;
			CableCache.calculate(this);
		} else {
			linkedBlocks.remove(pos);
			getCache().removeOuter(this, pos);
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
	@SuppressWarnings("ConstantConditions")
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
	
	private boolean isRemove = false;
	
	private static final CableCache CLIENT_CACHE = new CableCache();
	@Override
	public void update() {
		updateTickableState();
		if (cache == null) {
			if (world.isRemote) {
				cache = CLIENT_CACHE;
			} else {
				CableCache.calculate(this);
			}
		}
		send();
		for (BlockPos block : linkedBlocks) {
			TileEntity entity = world.getTileEntity(block);
			@SuppressWarnings("ConstantConditions")
			IStorage storage = entity.getCapability(EleCapability.ENERGY, WorldUtil.whatFacing(block, pos));
			if (storage != null && storage.canReceive()) {
				EleWorker.useEleEnergy(entity);
			}
		}
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		send();
	}
	
	public void updateTickableState() {
		if (world.isRemote || linkedBlocks.isEmpty()) {
			if (isRemove) return;
			isRemove = true;
			WorldUtil.removeTickable(this);
		} else {
			if (!isRemove) return;
			isRemove = false;
			WorldUtil.addTickable(this);
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
	public final void setCache(CableCache info) { this.cache = info; }
	/** 获取线路缓存 */
	public final CableCache getCache() { return cache; }
	/** 获取上方是否连接方块 */
	public final boolean getUp() { return (linkInfo & 0b100000) == 0b100000; }
	/** 获取下方是否连接方块 */
	public final boolean getDown() { return (linkInfo & 0b010000) == 0b010000; }
	/** 获取东方是否连接方块 */
	public final boolean getEast() { return (linkInfo & 0b001000) == 0b001000; }
	/** 获取南方是否连接方块 */
	public final boolean getSouth() { return (linkInfo & 0b000100) == 0b000100; }
	/** 获取西方是否连接方块 */
	public final boolean getWest() { return (linkInfo & 0b000010) == 0b000010; }
	/** 获取北方是否连接方块 */
	public final boolean getNorth() { return (linkInfo & 0b000001) == 0b000001; }
	/** 设置上方是否连接方块 */
	public final void setUp(boolean value) {
		if (value) linkInfo |= 0b100000;
		else linkInfo &= 0b011111;
	}
	/** 设置下方是否连接方块 */
	public final void setDown(boolean value) {
		if (value) linkInfo |= 0b010000;
		else linkInfo &= 0b101111;
	}
	/** 设置东方是否连接方块 */
	public final void setEast(boolean value) {
		if (value) linkInfo |= 0b001000;
		else linkInfo &= 0b110111;
	}
	/** 设置南方是否连接方块 */
	public final void setSouth(boolean value) {
		if (value) linkInfo |= 0b000100;
		else linkInfo &= 0b111011;
	}
	/** 设置西方是否连接方块 */
	public final void setWest(boolean value) {
		if (value) linkInfo |= 0b000010;
		else linkInfo &= 0b111101;
	}
	/** 设置北方是否连接方块 */
	public final void setNorth(boolean value) {
		if (value) linkInfo |= 0b000001;
		else linkInfo &= 0b111110;
	}
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
		this.counter = StringUtil.checkNull(counter, "counter");
	}
	
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return super.getUpdatePacket();
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		players.clear();
		if (isRemove) {
			isRemove = false;
			WorldUtil.addTickable(this);
		}
		return super.getUpdateTag();
	}

	@Override
	public void receive(@Nonnull IDataReader reader) {
		linkInfo = reader.readByte();
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	private final List<UUID> players = new ArrayList<>(1);
	
	/**
	 * <p>像客户端发送服务端存储的信息
	 * <p><b>这其中写有更新内部数据的代码，重写时应该调用</b>
	 */
	public void send() {
		if (world.isRemote) return;
		if (players.size() == world.playerEntities.size()) return;
		IOUtil.sendBlockMessageIfNotUpdate(this, players, 128, () -> {
			ByteDataOperator operator = new ByteDataOperator(1);
			operator.writeByte((byte) linkInfo);
			return operator;
		});
	}
	
	@Override
	public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == EleCapability.ENERGY) return true;
		return super.hasCapability(capability, facing);
	}
	@SuppressWarnings("unchecked")
	@Override
	public final <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == EleCapability.ENERGY) return (T) storage;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!world.isRemote) ObjectData.write(this, compound, ".");
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey(".")) ObjectData.read(this, compound, ".");
	}
	
	@Override
	public String toString() {
		return "EleSrcCable{ pos=" + pos + '}';
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	private final IStorage storage = new IStorage() {
		@Override
		public boolean canReceive() {
			return false;
		}
		
		@Override
		public boolean canExtract() {
			return false;
		}
		
		@Override
		public int receiveEnergy(EleEnergy energy, boolean simulate) {
			if (simulate) return energy.getEnergy();
			me += energy.getEnergy();
			return energy.getEnergy();
		}
		
		@Nonnull
		@Override
		public IVoltage getVoltage(EleStateEnum state, IVoltage voltage) {
			return voltage;
		}
		
		@Override
		public VoltageRange getReceiveVoltageRange() {
			return VoltageRange.ALL;
		}
		
		@Nonnull
		@Override
		public EleEnergy extractEnergy(int energy, VoltageRange voltage, boolean simulate) {
			return new EleEnergy(0, EnumVoltage.NON);
		}
		
		@Override
		public boolean isReAllowable(EnumFacing facing) {
			return false;
		}
		
		@Override
		public boolean isExAllowable(EnumFacing facing) {
			return false;
		}
		
		@Override
		public boolean canLink(EnumFacing facing) {
			return EleSrcCable.this.canLink(world.getTileEntity(pos.offset(facing)));
		}
		
		@Override
		public boolean link(BlockPos pos) {
			return EleSrcCable.this.link(pos);
		}
		
		@Override
		public boolean unLink(BlockPos pos) {
			EleSrcCable.this.deleteLink(pos);
			return true;
		}
		
		@Override
		public boolean isLink(BlockPos pos) {
			if (pos.equals(prev) || pos.equals(next)) return true;
			return linkedBlocks.contains(pos);
		}
		
		@Nonnull
		@Override
		public Collection<BlockPos> getLinks() {
			List<BlockPos> result = new ArrayList<>(6);
			result.addAll(linkedBlocks);
			if (next != null) result.add(next);
			if (prev != null) result.add(prev);
			return result;
		}
		
	};
	
}