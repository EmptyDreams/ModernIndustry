package xyz.emptydreams.mi.content.blocks.base.pipes;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.content.tileentity.pipes.ShuntPipeTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xyz.emptydreams.mi.api.utils.properties.MIProperty.ALL_FACING;
import static xyz.emptydreams.mi.content.blocks.base.pipes.StraightPipe.link;

/**
 * @author EmptyDreams
 */
public class ShuntPipe extends Pipe {
	
	public ShuntPipe(String name, String... ores) {
		super(name, ores);
		setDefaultState(blockState.getBaseState().withProperty(ALL_FACING, EnumFacing.UP));
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ALL_FACING);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		ShuntPipeTileEntity te = (ShuntPipeTileEntity) worldIn.getTileEntity(pos);
		//noinspection ConstantConditions
		return state.withProperty(ALL_FACING, te.getSide());
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = state.getActualState(source, pos);
		switch (state.getValue(ALL_FACING)) {
			case DOWN: case UP: return new AxisAlignedBB(0, 1/4d, 0, 1, 3/4d, 1);
			case NORTH: case SOUTH: return new AxisAlignedBB(0, 0, 1/4d, 1, 1, 3/4d);
			default: return new AxisAlignedBB(1/4d, 0, 0, 3/4d, 1, 1);
		}
	}
	
	@Override
	public boolean initTileEntity(ItemStack stack, EntityPlayer player,
	                              World world, BlockPos pos, EnumFacing side,
	                              float hitX, float hitY, float hitZ) {
		ShuntPipeTileEntity te = new ShuntPipeTileEntity();
		putBlock(world, pos, getDefaultState(), te, player, stack);
		IFluid cap = te.getFTCapability();
		EnumFacing facing = side.getOpposite();
		link(world, pos, cap, facing);
		for (EnumFacing value : EnumFacing.values()) {
			link(world, pos, cap, value);
		}
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new ShuntPipeTileEntity();
	}
	
}