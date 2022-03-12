package top.kmar.mi.content.blocks.base.pipes;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import top.kmar.mi.content.tileentity.pipes.ShuntPipeTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static top.kmar.mi.data.info.properties.MIProperty.getAXIS;

/**
 * @author EmptyDreams
 */
public class ShuntPipe extends Pipe {
	
	public ShuntPipe(String name, String... ores) {
		super(name, ores);
		setDefaultState(blockState.getBaseState().withProperty(getAXIS(), EnumFacing.Axis.Y));
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getAXIS());
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		ShuntPipeTileEntity te = (ShuntPipeTileEntity) worldIn.getTileEntity(pos);
		//noinspection ConstantConditions
		return state.withProperty(getAXIS(), te.getSide());
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = state.getActualState(source, pos);
		switch (state.getValue(getAXIS())) {
			case Y: return new AxisAlignedBB(0, 1/4d, 0, 1, 3/4d, 1);
			case Z: return new AxisAlignedBB(0, 0, 1/4d, 1, 1, 3/4d);
			default: return new AxisAlignedBB(1/4d, 0, 0, 3/4d, 1, 1);
		}
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new ShuntPipeTileEntity();
	}
	
}