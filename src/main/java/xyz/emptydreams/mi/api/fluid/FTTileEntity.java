package xyz.emptydreams.mi.api.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.capabilities.fluid.FluidCapability;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.data.FluidData;
import xyz.emptydreams.mi.api.fluid.data.FluidQueue;
import xyz.emptydreams.mi.api.fluid.data.TransportReport;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.IOUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.enums.IndexEnumMap;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.min;
import static net.minecraft.util.EnumFacing.*;

/**
 * 流体管道的TileEntity的父类
 * @author EmptyDreams
 */
public abstract class FTTileEntity extends BaseTileEntity implements IAutoNetwork, ITickable, IFluid {
    
    /** 运送流体时流体运送方向优先级列表 */
    public static final EnumFacing[] PUSH_EACH_PRIORITY = new EnumFacing[]{DOWN, NORTH, WEST, SOUTH, EAST, UP};
    /** 吸取流体时吸取方向优先级列表 */
    public static final EnumFacing[] POP_EACH_PRIORITY = new EnumFacing[]{UP, EAST, SOUTH, WEST, NORTH, DOWN};
    
    /** 六个方向的连接数据 */
    @Storage protected final IndexEnumMap<EnumFacing> linkData = new IndexEnumMap<>();
    /** 六个方向的管塞数据 */
    @Storage protected final EnumMap<EnumFacing, ItemStack> plugData = new EnumMap<>(EnumFacing.class);
    /** 管道内存储的流体量 */
    @Storage protected FluidData fluidData = FluidData.empty();
    
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
    
    /**
     * {@inheritDoc}
     * <p>缺省算法因为自身缺陷，不会维护流体在管道中的顺序，所以有可能有一部分原本在流体管道中的流体逆流回传入的 queue 中。
     * <p>该方法会优先将放置在队尾的流体送入管道，因为这样做可以尽可能地保证流体在管道中地顺序。
     */
    @Override
    public int insert(FluidQueue queue, EnumFacing facing, boolean simulate, TransportReport report) {
        if (!isOpen(facing.getOpposite())) return 0;
        int result = 0;
        FluidData newData = queue.popTail(getMaxAmount());
        report.insert(facing, newData);
        if (isEmpty()) {
            result += newData.getAmount();
            if (!simulate) fluidData.plus(newData);
        } else {
            queue.pushTail(fluidData);
            if (!simulate) fluidData = newData;
            for (EnumFacing value : PUSH_EACH_PRIORITY) {
                if (queue.isEmpty()) break;
                if (value == facing.getOpposite()) continue;
                IFluid fluid = getFacingLinked(value);
                if (fluid == null) continue;
                result += fluid.insert(queue, value, simulate, report);
            }
        }
        return result;
    }
    
    /** 该方法保证返回的队列中头部为最先取出的流体 */
    @Override
    public FluidQueue extract(int amount, EnumFacing facing, boolean simulate, TransportReport report) {
        FluidQueue result = FluidQueue.empty();
        if (!isOpen(facing)) return result;
        int copy = amount;
        for (EnumFacing value : POP_EACH_PRIORITY) {
            if (copy == 0) break;
            if (value == facing) continue;
            IFluid fluid = getFacingLinked(value);
            if (fluid == null) continue;
            FluidQueue queue = fluid.extract(
                    copy, value.getOpposite(), simulate, report);
            copy -= result.pushTail(queue);
        }
        FluidData value = fluidData.copy(min(amount, fluidData.getAmount()));
        fluidData = result.popTail(value.getAmount());
        result.pushHead(value);
        return result;
    }
    
    /** 获取经过保护性拷贝的管道内存储的流体数据 */
    public FluidData getFluidData() {
        return fluidData.copy();
    }
    
    @Override
    public boolean isEmpty() {
        return fluidData.isEmpty();
    }
    
    @Override
    public boolean isFull() {
        return fluidData.getAmount() == getMaxAmount();
    }
    
    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        netRange = new Range3D(pos.getX(), pos.getY(), pos.getZ(), 128);
    }
    
    @Override
    public final void receive(@Nonnull IDataReader reader) {
        linkData.setValue(reader.readByte());
        syncClient(reader);
        updateBlockState(true);
    }
    
    /**
     * <p>存储已经更新过的玩家列表
     * <p>不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
     * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
     */
    private final List<UUID> players = new ArrayList<>();
    /** 存储网络数据传输的更新范围，只有在范围内的玩家需要进行更新 */
    private Range3D netRange;
    
    /** 用于服务端写入需要同步的数据，写入的数据会发送给客户端 */
    abstract protected void sync(IDataWriter writer);
    
    /** 用于客户端同步数据 */
    @SideOnly(Side.CLIENT)
    abstract protected void syncClient(IDataReader reader);
    
    /** 向客户端发送服务端存储的信息并更新显示 */
    public final void send() {
        if (world.isRemote) return;
        if (players.size() == world.playerEntities.size()) return;
        ByteDataOperator operator = new ByteDataOperator(1);
        operator.writeByte((byte) linkData.getValue());
        sync(operator);
        IOUtil.sendBlockMessageIfNotUpdate(this, operator, players, netRange);
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
     */
    public void updateTickableState() {
        if (isRemove) {
            isRemove = false;
            WorldUtil.addTickable(this);
        } else {
            isRemove = true;
            WorldUtil.removeTickable(this);
        }
    }
    
    @Override
    public void unlink(EnumFacing facing) {
        linkData.set(facing, false);
        updateBlockState(false);
    }
    
    @Override
    public boolean isLinked(EnumFacing facing) {
        return linkData.get(facing);
    }
    
    /**
     * 在指定方向上设置管塞
     * @param plug 管塞物品对象，为null表示去除管塞
     * @param facing 方向
     * @return 是否设置成功（若管塞已经被设置也返回true）
     */
    public boolean setPlug(EnumFacing facing, ItemStack plug) {
        if (!canSetPlug(facing)) return false;
        if (plug == null) plugData.put(facing, null);
        else plugData.put(facing, plug.copy());
        markDirty();
        return true;
    }
    
    /**
     * 判定指定方向上是否有管塞
     * @param facing 指定方向
     */
    public boolean hasPlug(EnumFacing facing) {
        return plugData.get(facing) != null;
    }
    
    /**
     * 获取指定方向上连接的方块的IFluid
     * @return 如果指定方向上没有连接方块则返回null
     */
    @Nullable
    public IFluid getFacingLinked(EnumFacing facing) {
        if (isLinked(facing)) return null;
        BlockPos target = pos.offset(facing);
        TileEntity te = world.getTileEntity(target);
        //noinspection ConstantConditions
        return te.getCapability(FluidCapability.TRANSFER, facing);
    }
    
    /** 判断指定方向是否含有开口 */
    abstract public boolean hasAperture(EnumFacing facing);
    
    /** 判断指定方向上能否通过流体 */
    public boolean isOpen(EnumFacing facing) {
        return hasAperture(facing) && !hasPlug(facing);
    }
    
    /** 判断指定方向上是否可以设置管塞 */
    public boolean canSetPlug(EnumFacing facing) {
        return !(hasPlug(facing) || isLinked(facing));
    }
    
}