package top.kmar.mi.content.blocks.base.pipes;

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
import top.kmar.mi.api.capabilities.fluid.FluidCapability;
import top.kmar.mi.api.capabilities.fluid.IFluid;
import top.kmar.mi.content.tileentity.pipes.StraightPipeTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static top.kmar.mi.api.utils.ExpandFunctionKt.getPlacingDirection;
import static top.kmar.mi.data.info.MIProperty.getALL_FACING;

/**
 * <p>直线型管道
 * <p>关于管道朝向的设定：管道朝向为两个开口的任意一个开口的方向
 * @author EmptyDreams
 */
public class StraightPipe extends Pipe {
	
	public StraightPipe(String name, String... ores) {
		super(name, ores);
		setDefaultState(blockState.getBaseState().withProperty(getALL_FACING(), EnumFacing.NORTH));
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		StraightPipeTileEntity te = (StraightPipeTileEntity) worldIn.getTileEntity(pos);
		return state.withProperty(getALL_FACING(), te.getFacing());
	}
	
	@Override
	public boolean initTileEntity(ItemStack stack, EntityPlayer player,
	                                 World world, BlockPos pos, EnumFacing side,
	                                 float hitX, float hitY, float hitZ) {
		StraightPipeTileEntity te = new StraightPipeTileEntity();
		putBlock(world, pos, getDefaultState(), te, player, stack);
		EnumFacing facing = side.getOpposite();
		if (link(world, pos, te, facing)) {
			link(world, pos, te, side);
			return true;
		}
		if (link(world, pos, te, side)) {
			return true;
		}
		for (EnumFacing value : EnumFacing.values()) {
			if (link(world, pos, te, value)) {
				link(world, pos, te, value.getOpposite());
				return true;
			}
		}
		te.setFacing(getPlacingDirection(player, pos));
		return true;
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
		state = state.getActualState(source, pos);
		EnumFacing facing = state.getValue(getALL_FACING());
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
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getALL_FACING());
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new StraightPipeTileEntity();
	}
}