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
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.AngleFacingEnum;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.PropertyAngleFacing;
import xyz.emptydreams.mi.content.tileentity.pipes.AnglePipeTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xyz.emptydreams.mi.api.utils.properties.MIProperty.HORIZONTAL;
import static xyz.emptydreams.mi.content.blocks.base.pipes.StraightPipe.link;

/**
 * <p>直角拐弯的管道
 * <p>关于管道朝向的设定：管道朝向为水平方向上的开口的任意一个开口的方向
 * @author EmptyDreams
 */
public class AnglePipe extends Pipe {
	
	public static final PropertyAngleFacing ANGLE_FACING = PropertyAngleFacing.create("ver");
	
	public AnglePipe(String name, String... ores) {
		super(name, ores);
		setDefaultState(blockState.getBaseState().withProperty(HORIZONTAL, EnumFacing.NORTH)
				.withProperty(ANGLE_FACING, AngleFacingEnum.UP));
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HORIZONTAL, ANGLE_FACING);
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		AnglePipeTileEntity te = (AnglePipeTileEntity) worldIn.getTileEntity(pos);
		AngleFacingEnum after = AngleFacingEnum.valueOf(te.getFacing(), te.getAfter());
		return state.withProperty(HORIZONTAL, te.getFacing()).withProperty(ANGLE_FACING, after);
	}
	
	@Override
	public boolean initTileEntity(ItemStack stack, EntityPlayer player,
	                                 World world, BlockPos pos, EnumFacing side,
	                                 float hitX, float hitY, float hitZ) {
		AnglePipeTileEntity te = new AnglePipeTileEntity(player.getHorizontalFacing().getOpposite(),
											player.rotationPitch > 0 ? EnumFacing.UP : EnumFacing.DOWN);
		putBlock(world, pos, getDefaultState(), te, player, stack);
		EnumFacing facing = side.getOpposite();
		link(world, pos, te, facing);
		for (EnumFacing value : EnumFacing.values()) {
			link(world, pos, te, value);
		}
		return true;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = state.getActualState(source, pos);
		EnumFacing facing = state.getValue(HORIZONTAL);
		EnumFacing after = state.getValue(ANGLE_FACING).toEnumFacing(facing);
		switch (facing) {
			case EAST:
				switch (after) {
					case UP: return new AxisAlignedBB(1/4d, 1/4d, 1/4d, 1, 1, 3/4d);
					case DOWN: return new AxisAlignedBB(1/4d, 0, 1/4d, 1, 3/4d, 3/4d);
					case NORTH: return new AxisAlignedBB(1/4d, 1/4d, 0, 1, 3/4d, 3/4d);
					case SOUTH: return new AxisAlignedBB(1/4d, 1/4d, 1/4d, 1, 3/4d, 1);
				}
			case WEST:
				switch (after) {
					case UP: return new AxisAlignedBB(0, 1/4d, 1/4d, 3/4d, 1, 3/4d);
					case DOWN: return new AxisAlignedBB(0, 0, 1/4d, 3/4d, 3/4d, 3/4d);
					case NORTH: return new AxisAlignedBB(0, 1/4d, 0, 3/4d, 3/4d, 3/4d);
					case SOUTH: return new AxisAlignedBB(0, 1/4d, 1/4d, 3/4d, 3/4d, 1);
				}
			case NORTH:
				switch (after) {
					case UP: return new AxisAlignedBB(1/4d, 1/4d, 0, 3/4d, 1, 3/4d);
					case DOWN: return new AxisAlignedBB(1/4d, 0, 0, 3/4d, 3/4d, 3/4d);
					case EAST: return new AxisAlignedBB(1/4d, 1/4d, 0, 1, 3/4d, 3/4d);
					case WEST: return new AxisAlignedBB(0, 1/4d, 0, 3/4d, 3/4d, 3/4d);
				}
			case SOUTH:
				switch (after) {
					case UP: return new AxisAlignedBB(1/4d, 1/4d, 1/4d, 3/4d, 1, 1);
					case DOWN: return new AxisAlignedBB(1/4d, 0, 1/4d, 3/4d, 3/4d, 1);
					case EAST: return new AxisAlignedBB(1/4d, 1/4d, 1/4d, 1, 3/4d, 1);
					case WEST: return new AxisAlignedBB(0, 1/4d, 1/4d, 3/4d, 3/4d, 1);
					
				}
		}
		throw new IllegalArgumentException("不合理的方向组合：facing=" + facing + ",after=" + after);
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AnglePipeTileEntity();
	}
}