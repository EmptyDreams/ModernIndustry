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
import xyz.emptydreams.mi.api.capabilities.fluid.FluidCapability;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.FTStateEnum;
import xyz.emptydreams.mi.content.tileentity.pipes.StraightPipeTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static xyz.emptydreams.mi.api.utils.properties.MIProperty.ALL_FACING;

/**
 * <p>直线型管道
 * <p>关于管道朝向的设定：管道朝向为两个开口的任意一个开口的方向
 * @author EmptyDreams
 */
public class StraightPipe extends Pipe {
	
	public StraightPipe(String name, String... ores) {
		super(name, FTStateEnum.STRAIGHT, ores);
		setDefaultState(blockState.getBaseState().withProperty(ALL_FACING, EnumFacing.NORTH));
	}
	
	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return new AxisAlignedBB(3 / 16d, 3 / 16d, 3 / 16d, 10 / 16d, 10 / 16d, 10 / 16d);
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		StraightPipeTileEntity te = (StraightPipeTileEntity) worldIn.getTileEntity(pos);
		return state.withProperty(ALL_FACING, te.getFacing());
	}
	
	@Override
	public TileEntity createTileEntity(ItemStack stack, EntityPlayer player,
	                                   World world, BlockPos pos, EnumFacing side,
	                                   float hitX, float hitY, float hitZ) {
		StraightPipeTileEntity te = (StraightPipeTileEntity) world.getTileEntity(pos);
		EnumFacing facing = side.getOpposite();
		@SuppressWarnings("ConstantConditions")
		IFluid cap = te.getFTCapability();
		if (link(world, pos, cap, facing)) {
			link(world, pos, cap, side);
			return null;
		}
		if (link(world, pos, cap, side)) {
			return null;
		}
		for (EnumFacing value : EnumFacing.values()) {
			if (link(world, pos, cap, value)) {
				link(world, pos, cap, value.getOpposite());
				return null;
			}
		}
		te.setFacing(MathUtil.getPlayerFacing(player, pos));
		return null;
	}
	
	/**
	 * 尝试连接两个方块
	 * @param world 当前世界
	 * @param pos 当前方块坐标
	 * @param cap 当前方块的IFluid对象
	 * @param facing 要连接的方块相对于当前方块的方向
	 * @return 是否连接成功
	 */
	public static boolean link(World world, BlockPos pos, IFluid cap, EnumFacing facing) {
		TileEntity thatTE = world.getTileEntity(pos.offset(facing));
		if (thatTE == null) return false;
		IFluid that = thatTE.getCapability(FluidCapability.TRANSFER, null);
		if (that == null) return false;
		EnumFacing side = facing.getOpposite();
		if (!that.canLink(side)) return false;
		if (!cap.link(facing)) return false;
		if (!that.link(side)) return unlink(cap, facing);
		thatTE.markDirty();
		return true;
	}
	
	private static boolean unlink(IFluid cap, EnumFacing facing) {
		cap.unlink(facing);
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing facing = state.getValue(ALL_FACING);
		switch (facing) {
			case DOWN:
			case UP:
				return new AxisAlignedBB(1 / 4d, 0, 1 / 4d, 3 / 4d, 1, 3 / 4d);
			case NORTH:
			case SOUTH:
				return new AxisAlignedBB(1 / 4d, 1 / 4d, 0, 3 / 4d, 3 / 4d, 1);
			case WEST:
			case EAST:
				return new AxisAlignedBB(0, 1 / 4d, 1 / 4d, 1, 3 / 4d, 3 / 4d);
			default:
				throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
		}
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
	                                  AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
	                                  @Nullable Entity entityIn, boolean isActualState) {
		EnumFacing facing = state.getValue(ALL_FACING);
		switch (facing) {
			case DOWN:
			case UP:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5 / 16d, 0, 5 / 16d, 11 / 16d, 1, 11 / 16d));
				break;
			case NORTH:
			case SOUTH:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(5 / 16d, 5 / 16d, 0, 11 / 16d, 11 / 16d, 1));
				break;
			case WEST:
			case EAST:
				addCollisionBoxToList(pos, entityBox, collidingBoxes,
						new AxisAlignedBB(0, 5 / 16d, 5 / 16d, 1, 11 / 16d, 11 / 16d));
				break;
			default:
				throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
		}
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ALL_FACING);
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new StraightPipeTileEntity();
	}
}