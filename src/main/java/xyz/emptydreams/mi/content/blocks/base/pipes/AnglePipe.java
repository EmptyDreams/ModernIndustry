package xyz.emptydreams.mi.content.blocks.base.pipes;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.AngleFacingEnum;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.FTStateEnum;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.PropertyAngleFacing;
import xyz.emptydreams.mi.content.tileentity.pipes.AnglePipeTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
		super(name, FTStateEnum.ANGLE, ores);
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
	public TileEntity createTileEntity(ItemStack stack, EntityPlayer player,
	                                   World world, BlockPos pos, EnumFacing side,
	                                   float hitX, float hitY, float hitZ) {
		AnglePipeTileEntity te = (AnglePipeTileEntity) world.getTileEntity(pos);
		//noinspection ConstantConditions
		IFluid cap = te.getFTCapability();
		EnumFacing facing = side.getOpposite();
		te.setFacing(player.getHorizontalFacing().getOpposite());
		link(world, pos, cap, facing);
		for (EnumFacing value : EnumFacing.values()) {
			link(world, pos, cap, value);
		}
		te.markDirty();
		return te;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing facing = state.getValue(HORIZONTAL);
		EnumFacing after = state.getValue(ANGLE_FACING).toEnumFacing(facing);
		switch (facing) {
			case EAST:
				switch (after) {
					case UP: return new AxisAlignedBB(1/4d, 1/4d, 1/4d, 1, 1, 3/4d);
					case DOWN: return new AxisAlignedBB(1/4d, 0, 1/4d, 1, 3/4d, 3/4d);
					case NORTH: return new AxisAlignedBB(1/4d, 1/4d, 1/4d, 1, 3/4d, 1);
					case SOUTH: return new AxisAlignedBB(1/4d, 1/4d, 0, 1, 3/4d, 3/4d);
				}
			case WEST:
				switch (after) {
					case UP: return new AxisAlignedBB(0, 1/4d, 1/4d, 3/4d, 1, 3/4d);
					case DOWN: return new AxisAlignedBB(0, 0, 1/4d, 3/4d, 3/4d, 3/4d);
					case NORTH: return new AxisAlignedBB(0, 1/4d, 1/4d, 3/4d, 3/4d, 1);
					case SOUTH: return new AxisAlignedBB(0, 1/4d, 0, 3/4d, 3/4d, 3/4d);
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
					case EAST: return new AxisAlignedBB(0, 1/4d, 1/4d, 3/4d, 3/4d, 1);
					case WEST: return new AxisAlignedBB(1/4d, 1/4d, 1/4d, 1, 3/4d, 1);
				}
		}
		throw new IllegalArgumentException("不合理的方向组合：facing=" + facing + ",after=" + after);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
	                                  AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
	                                  @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState) state = state.getActualState(worldIn, pos);
		addCollisionBoxToList(pos, entityBox, collidingBoxes,
				new AxisAlignedBB(5/16d, 5/16d, 5/16d, 11/16d, 11/16d, 11/16d));
		EnumFacing facing = state.getValue(HORIZONTAL);
		EnumFacing after = state.getValue(ANGLE_FACING).toEnumFacing(facing);
		addCollisionBoxToList(facing, pos, entityBox, collidingBoxes);
		addCollisionBoxToList(after, pos, entityBox, collidingBoxes);
	}
	
	public void addCollisionBoxToList(EnumFacing facing, BlockPos pos,
	                                         AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes) {
		switch (facing) {
			case DOWN:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5/16d, 0, 5/16d, 11/16d, 5/16d, 11/16d));
				break;
			case UP:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5/16d, 11/16d, 5/16d, 11/16d, 1, 11/16d));
				break;
			case NORTH:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
					new AxisAlignedBB(5/16d, 5/16d, 0, 11/16d, 11/16d, 5/16d));
				break;
			case SOUTH:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5/16d, 5/16d, 11/16d, 11/16d, 11/16d, 1));
				break;
			case WEST:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(0, 5/16d, 5/16d, 5/16d, 11/16d, 11/16d));
				break;
			case EAST:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(11/16d, 5/16d, 5/16d, 1, 11/16d, 11/16d));
				break;
		}
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AnglePipeTileEntity();
	}
}