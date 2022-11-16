package top.kmar.mi.content.tileentity.pipes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.pipes.FluidPipeEntity;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.api.utils.container.IndexEnumMap;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;
import top.kmar.mi.data.properties.MIProperty;

/**
 * 直线型管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("StraightPipe")
public class StraightPipeTileEntity extends FluidPipeEntity {
    
    /** 管道连接数据 */
    @AutoSave
    private final IndexEnumMap<EnumFacing> linkedData = new IndexEnumMap<>(EnumFacing.values());
    
    public StraightPipeTileEntity() {
        super(1000);
    }
    
    @Override
    public boolean hasChannel(EnumFacing facing) {
        return facing.getAxis() == getFacing();
    }
    
    @Override
    public boolean linkFluidBlock(@NotNull EnumFacing facing) {
        if (isLink(facing)) return true;
        if (!hasChannel(facing) && !linkedData.isInit()) return false;
        linkedData.set(facing, true);
        setFacing(facing);
        return true;
    }
    
    @Override
    public void unlinkFluidBlock(@NotNull EnumFacing facing) {
        linkedData.set(facing, false);
    }
    
    @Override
    public boolean isLink(EnumFacing facing) {
        return linkedData.get(facing);
    }
    
    /** 设置管道正方向 */
    public void setFacing(EnumFacing facing) {
        IBlockState old = world.getBlockState(pos);
        if (old.getValue(MIProperty.getAXIS()) == facing.getAxis()) return;
        IBlockState newState = old.withProperty(MIProperty.getAXIS(), facing.getAxis());
        WorldExpandsKt.setBlockWithMark(world, pos, newState);
    }
    
    /** 获取管道正方向 */
    public EnumFacing.Axis getFacing() {
        return world.getBlockState(pos).getValue(MIProperty.getAXIS());
    }
    
}