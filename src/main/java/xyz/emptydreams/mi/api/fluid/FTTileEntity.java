package xyz.emptydreams.mi.api.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.capabilities.fluid.FluidCapability;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.block.BlockAddition;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.api.utils.data.math.Point3D;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;
import xyz.emptydreams.mi.content.tileentity.pipes.data.DataManager;
import xyz.emptydreams.mi.content.tileentity.pipes.data.FluidData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static net.minecraft.util.EnumFacing.*;

/**
 * 流体管道的TileEntity的父类
 * @author EmptyDreams
 */
public abstract class FTTileEntity extends BaseTileEntity implements IAutoNetwork, ITickable, IFluid {
	
	/** 六个方向的连接数据 */
	@Storage(byte.class) protected int linkData = 0b000000;
	/** 六个方向的管塞数据 */
	@Storage protected final Map<EnumFacing, ItemStack> plugData = new EnumMap<>(EnumFacing.class);
	/** 存储管道内流体来源 */
	@Storage protected EnumFacing source = null;
	
	public FTTileEntity() { }
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) return true;
		return capability == FluidCapability.TRANSFER && (facing == null || hasAperture(facing));
	}
	
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == FluidCapability.TRANSFER
				&& (facing == null || hasAperture(facing))) {
			return  FluidCapability.TRANSFER.cast(this);
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		netRange = new Range3D(pos.getX(), pos.getY(), pos.getZ(), 128);
	}
	
	@Override
	public final void receive(@Nonnull IDataReader reader) {
		linkData = reader.readByte();
		syncClient(reader);
		updateBlockState(true);
	}
	
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	private final List<String> players = new ArrayList<>(1);
	/** 存储网络数据传输的更新范围，只有在范围内的玩家需要进行更新 */
	private Range3D netRange;
	
	/** 用于写入需要同步的数据 */
	abstract protected void sync(IDataWriter writer);
	
	/** 用于客户端同步数据 */
	@SideOnly(Side.CLIENT)
	abstract protected void syncClient(IDataReader reader);
	
	/**
	 * <p>向客户端发送服务端存储的信息并更新显示
	 */
	public final void send() {
		if (world.isRemote) return;
		if (players.size() == world.playerEntities.size()) return;
		ByteDataOperator operator = new ByteDataOperator(1);
		operator.writeByte((byte) linkData);
		sync(operator);
		IMessage message = BlockMessage.instance().create(operator, new BlockAddition(this));
		MessageSender.sendToClientIf(message, world, player -> {
			if (players.contains(player.getName()) || !netRange.isIn(new Point3D(player))) return false;
			players.add(player.getName());
			return true;
		});
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		send();
		if (isRemove && players.size() != world.playerEntities.size()) {
			isRemove = false;
			WorldUtil.addTickable(this);
		}
		return super.getUpdateTag();
	}
	
	/**
	 * 更新IBlockState
	 * @param isRunOnClient 是否在客户端运行
	 */
	public void updateBlockState(boolean isRunOnClient) {
		markDirty();
		if (world.isRemote) {
			if (!isRunOnClient) return;
		} else {
			players.clear();
			send();
		}
		IBlockState oldState = world.getBlockState(pos);
		IBlockState newState = oldState.getActualState(world, pos);
		WorldUtil.setBlockState(world, pos, newState);
	}
	
	/**
	 * 方法内包含管道正常运行的方法，重写时务必使用{@code super.update()}调用
	 */
	@Override
	public void update() {
		send();
		updateTickableState();
	}
	
	/** 存储该TE是否已经从tickable的列表中移除 */
	private boolean isRemove = false;
	
	/**
	 * 更新管道tickable的状态
	 * @return 如果移除成功则返回true
	 */
	public boolean updateTickableState() {
		if (!isRemove && isEmpty()) {
			isRemove = true;
			WorldUtil.removeTickable(this);
			return true;
		} else if (isRemove) {
			isRemove = false;
			WorldUtil.addTickable(this);
		}
		return false;
	}
	
	/**
	 * 设置指定方向上的连接状态
	 * @param facing 指定方向
	 * @param isLinked 是否连接
	 */
	protected void setLinkedData(EnumFacing facing, boolean isLinked) {
		switch (facing) {
			case DOWN:
				if (isLinked) linkData |= 0b010000;
				else linkData &= 0b101111;
				break;
			case UP:
				if (isLinked) linkData |= 0b100000;
				else linkData &= 0b011111;
				break;
			case NORTH:
				if (isLinked) linkData |= 0b000001;
				else linkData &= 0b111110;
				break;
			case SOUTH:
				if (isLinked) linkData |= 0b000010;
				else linkData &= 0b111101;
				break;
			case WEST:
				if (isLinked) linkData |= 0b000100;
				else linkData &= 0b111011;
				break;
			case EAST:
				if (isLinked) linkData |= 0b001000;
				else linkData &= 0b110111;
				break;
		}
	}
	
	@Override
	public void setSource(EnumFacing facing) {
		source = facing;
	}
	
	@Override
	public EnumFacing getSource() {
		return source;
	}
	
	@Override
	public void unlink(EnumFacing facing) {
		setLinkedData(facing, false);
		updateBlockState(false);
	}
	
	@Override
	public boolean isLinkedUp() {
		return (linkData & 0b100000) == 0b100000;
	}
	@Override
	public boolean isLinkedDown() {
		return (linkData & 0b010000) == 0b010000;
	}
	@Override
	public boolean isLinkedEast() {
		return (linkData & 0b001000) == 0b001000;
	}
	@Override
	public boolean isLinkedWest() {
		return (linkData & 0b000100) == 0b000100;
	}
	@Override
	public boolean isLinkedSouth() {
		return (linkData & 0b000010) == 0b000010;
	}
	@Override
	public boolean isLinkedNorth() {
		return (linkData & 0b000001) == 0b000001;
	}
	
	@Override
	public boolean setPlugUp(ItemStack plug) {
		if (!(plug != null && hasPlugUp() && canSetPlug(UP))) return false;
		setPlugData(UP, plug);
		return true;
	}
	
	@Override
	public boolean setPlugDown(ItemStack plug) {
		if (!(plug != null && hasPlugDown() && canSetPlug(DOWN))) return false;
		setPlugData(DOWN, plug);
		return true;
	}
	
	@Override
	public boolean setPlugNorth(ItemStack plug) {
		if (!(plug != null && hasPlugNorth() && canSetPlug(NORTH))) return false;
		setPlugData(NORTH, plug);
		return true;
	}
	
	@Override
	public boolean setPlugSouth(ItemStack plug) {
		if (!(plug != null && hasPlugSouth() && canSetPlug(SOUTH))) return false;
		setPlugData(SOUTH, plug);
		return true;
	}
	
	@Override
	public boolean setPlugWest(ItemStack plug) {
		if (!(plug != null && hasPlugWest() && canSetPlug(WEST))) return false;
		setPlugData(WEST, plug);
		return true;
	}
	
	@Override
	public boolean setPlugEast(ItemStack plug) {
		if (!(plug != null && hasPlugEast() && canSetPlug(EAST))) return false;
		setPlugData(EAST, plug);
		return true;
	}
	
	@Override
	public boolean hasPlugUp() {
		return plugData.get(UP) != null;
	}
	
	@Override
	public boolean hasPlugDown() {
		return plugData.get(DOWN) != null;
	}
	
	@Override
	public boolean hasPlugNorth() {
		return plugData.get(NORTH) != null;
	}
	
	@Override
	public boolean hasPlugSouth() {
		return plugData.get(SOUTH) != null;
	}
	
	@Override
	public boolean hasPlugWest() {
		return plugData.get(WEST) != null;
	}
	
	@Override
	public boolean hasPlugEast() {
		return plugData.get(EAST) != null;
	}
	
	private void setPlugData(EnumFacing facing, ItemStack plug) {
		plugData.put(facing, plug.copy());
		markDirty();
	}
	
	@Nonnull
	abstract protected DataManager getDataManager(EnumFacing facing);
	
	abstract protected boolean matchFacing(EnumFacing facing);
	
	@Nonnull
	@Override
	public TransportResult extract(int amount, EnumFacing facing, boolean simulate) {
		TransportResult result = new TransportResult();
		if (!matchFacing(facing)) return result;
		List<EnumFacing> next = next(facing);
		IFluid.sortFacing(next);
		@SuppressWarnings("unchecked")
		Iterator<FluidData>[] its = new Iterator[next.size()];
		//通过遍历将其他方向的流体数据取出指定量
		ListIterator<EnumFacing> it = next.listIterator(next.size());
		int k = amount / next.size();
		int i = 0;
		while (it.hasPrevious()) {
			EnumFacing value = it.previous();
			if (!it.hasPrevious()) k = amount - k * (next.size() - 1);
			DataManager manager = getDataManager(value);
			LinkedList<FluidData> nowRe = manager.extract(k, false, simulate);
			its[i++] = nowRe.iterator();
			//处理后续管道
			BlockPos pre = pos.offset(value);
			TileEntity te = world.getTileEntity(pre);
			if (te == null) continue;
			IFluid cap = te.getCapability(FluidCapability.TRANSFER, value.getOpposite());
			if (cap == null) continue;
			result.combine(cap.extract(k, value.getOpposite(), simulate));
		}
		//将从其他方向上取出的数据汇总到主干
		DataManager manager = getDataManager(facing);
		result.setFinal(manager.extract(amount, facing, simulate), facing);
		while (Arrays.stream(its).anyMatch(Iterator::hasNext)) {
			Arrays.stream(its).filter(Iterator::hasNext)
					.forEach(iterator -> manager.insert(iterator.next(), false, simulate));
		}
		return result;
	}
	
	@Nonnull
	@Override
	public TransportResult insert(FluidData data, EnumFacing facing, boolean simulate) {
		TransportResult result = new TransportResult();
		if (!matchFacing(facing)) return result;
		List<EnumFacing> next = next(facing);
		IFluid.sortFacing(next);
		LinkedList<FluidData> out = getDataManager(facing).insert(data, true, simulate);
		List<FluidData>[] datas = split(out, next.size());
		int i = -1;
		for (EnumFacing value : next) {
			++i;
			DataManager manager = getDataManager(value);
			List<FluidData> list = new LinkedList<>();
			for (FluidData fluidData : datas[i]) {
				list.addAll(manager.insert(fluidData, false, simulate));
			}
			BlockPos nextPos = pos.offset(value);
			TileEntity nextTe = world.getTileEntity(nextPos);
			if (nextTe == null)
				return putFluid2World(data, facing.getOpposite(), simulate,
						result, datas[i], nextPos, true, manager, this);
			IFluid cap = nextTe.getCapability(FluidCapability.TRANSFER, facing.getOpposite());
			if (cap == null)
				return putFluid2World(data, facing.getOpposite(), simulate,
						result, datas[i], nextPos, true, manager, this);
			for (FluidData fluidData : datas[i]) {
				result.combine(cap.insert(fluidData, facing, simulate));
			}
		}
		return result;
	}
	
	/* 将数据平均分割为指定份数 */
	protected List<FluidData>[] split(List<FluidData> data, int copies) {
		@SuppressWarnings("unchecked") List<FluidData>[] result = new List[copies];
		if (copies == 1) {
			result[0] = data;
			return result;
		}
		int sum = data.stream().mapToInt(FluidData::getAmount).sum();
		for (int i = 0; i < copies; ++i) {
			int amount = i == (copies - 1) ? sum - (sum / copies * (copies - 1)) : sum / copies;
			result[i] = new LinkedList<>();
			Iterator<FluidData> iterator = data.iterator();
			while (iterator.hasNext()) {
				FluidData datum = iterator.next();
				if (datum.getAmount() > amount) {
					datum.plusAmount(-amount);
					result[i].add(new FluidData(datum.getFluid(), amount));
					break;
				}
				if (datum.getAmount() <= amount) {
					amount -= datum.getAmount();
					result[i].add(datum);
					iterator.remove();
					if (amount == 0) break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 尝试将流体释放到世界中
	 * @param data 要插入的流体
	 * @param facing 流体流动方向
	 * @param simulate 是否为模拟
	 * @param result 结果对象
	 * @param out 要排放的流体
	 * @param next 目标方块
	 * @param isInput 是否是由插入流体引发的操作，为false时不会将流体释放到世界
	 * @param manager 参与计算的数据管理器
	 * @return 输入的TransportResult对象
	 */
	public static TransportResult putFluid2World(FluidData data, EnumFacing facing, boolean simulate,
	                                             TransportResult result, List<FluidData> out,
	                                             BlockPos next, boolean isInput, DataManager manager, TileEntity te) {
		List<FluidData> list = IFluid.putFluid2World(
				te.getWorld(), te.getPos(), next, out, !isInput || simulate);
		result.setFinal(list, facing);
		int outAmount = list.stream().mapToInt(FluidData::getAmount).sum();
		if ((!simulate) && outAmount != 0)
			manager.insert(new FluidData(data.getFluid(), outAmount), facing.getOpposite(), false);
		for (FluidData fluidData : list)
			result.getNode(fluidData.getFluid()).plus(facing, fluidData.getAmount());
		return result;
	}
	
}