package top.kmar.mi.api.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.api.utils.expands.IOExpandsKt;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;

/**
 * 基础TE，提供了一些默认的功能
 * @author EmptyDreams
 */
public class BaseTileEntity extends TileEntity {
    
    private boolean tickable = true;
    
    /** 将当前方块添加到 Tick 任务中 */
    protected void addTickable() {
        if (!tickable) {
            assert this instanceof ITickable;
            WorldExpandsKt.addTickable(world, this);
            tickable = true;
        }
    }
    
    protected void removeTickable() {
        if (tickable) {
            assert this instanceof ITickable;
            WorldExpandsKt.removeTickable(world, this);
            tickable = false;
        }
    }
    
    /**
     * 方块更新时若是同种方块则不更新TE
     * @param world 所在世界
     * @param pos 方块坐标
     * @param oldState 旧的state
     * @param newSate 新的state
     * @return 是否更新TE
     */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (!world.isRemote) {
            IOExpandsKt.writeObject(compound, this, ".");
        }
        return super.writeToNBT(compound);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(".")) {
            IOExpandsKt.readObject(compound, this, ".");
        }
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{world=" + (world == null ? "null" : world.getProviderName()) +
                "pos=" + pos + '}';
    }
    
}