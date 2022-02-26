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
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.IOUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.api.utils.data.math.Range3D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.min;
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
    
    @Override
    public int insert(FluidData data, EnumFacing facing, boolean simulate) {
        if (!fluidData.matchFluid(data)) return 0;
        int result = min(getMaxAmount() - fluidData.getAmount(), data.getAmount());
        if (!simulate) fluidData.plusAmount(result);
        return result;
    }
    
    @Override
    public int extract(FluidData data, EnumFacing facing, boolean simulate) {
        if (!fluidData.matchFluid(data)) return 0;
        int result = min(fluidData.getAmount(), data.getAmount());
        if (!simulate) fluidData.minusAmount(result);
        return result;
    }
    
    @Override
    public FluidData extract(int amount, EnumFacing facing, boolean simulate) {
        int result = min(fluidData.getAmount(), amount);
        if (!simulate) fluidData.minusAmount(result);
        return fluidData.copy(result);
    }
    
    /**
     * 获取管道内存储的流体数据
     * @return 返回值经过保护性复制
     */
    public FluidData getFluidData() {
        return fluidData.copy();
    }
    
    @Override
    public boolean isEmpty() {
        return fluidData.isEmpty();
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
    private final List<UUID> players = new ArrayList<>(1);
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
    public void removeLink(EnumFacing facing) {
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
    
    /** 获取连接总数 */
    public int getLinkedAmount() {
        return (int) Arrays.stream(values()).filter(this::isLinked).count();
    }
    
}