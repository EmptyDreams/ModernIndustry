package top.kmar.mi.content.blocks.base.pipes;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import top.kmar.mi.content.tileentity.pipes.StraightPipeTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static top.kmar.mi.data.properties.MIProperty.getALL_FACING;
import static top.kmar.mi.data.properties.MIProperty.getAXIS;

/**
 * <p>直线型管道
 * <p>关于管道朝向的设定：管道朝向为两个开口的任意一个开口的方向
 * @author EmptyDreams
 */
public class StraightPipe extends Pipe {
    
    public StraightPipe(String name, String... ores) {
        super(name, ores);
        setDefaultState(blockState.getBaseState().withProperty(getAXIS(), EnumFacing.Axis.X));
    }
    
    @SuppressWarnings("ConstantConditions")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        StraightPipeTileEntity te = (StraightPipeTileEntity) worldIn.getTileEntity(pos);
        return state.withProperty(getAXIS(), te.getFacing());
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = state.getActualState(source, pos);
        EnumFacing facing = state.getValue(getALL_FACING());
        switch (facing) {
            case DOWN: case UP:
                return new AxisAlignedBB(1 / 4d, 0, 1 / 4d, 3 / 4d, 1, 3 / 4d);
            case NORTH: case SOUTH:
                return new AxisAlignedBB(1 / 4d, 1 / 4d, 0, 3 / 4d, 3 / 4d, 1);
            case WEST: case EAST:
                return new AxisAlignedBB(0, 1 / 4d, 1 / 4d, 1, 3 / 4d, 3 / 4d);
            default:
                throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
        }
    }
    
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getAXIS());
    }
    
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new StraightPipeTileEntity();
    }
    
    @Override
    public int getMetaFromState(@NotNull IBlockState state) {
        return state.getValue(getAXIS()).ordinal();
    }
    
    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis facing = EnumFacing.Axis.values()[meta];
        return getDefaultState().withProperty(getAXIS(), facing);
    }
    
}