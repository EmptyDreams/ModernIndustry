package xyz.emptydreams.mi.api.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.emptydreams.mi.blocks.base.TransferBlock;

import java.util.function.BiConsumer;

/**
 * 这个类种包含一些常用的工具类方法
 * @author EmptyDremas
 */
public final class BlockUtil {

	/**
	 * 判断指定方块是否为完整方块或接近于完整方块
	 * @param access 所在世界
	 * @param pos 方块坐标
	 */
	public static boolean isFullBlock(IBlockAccess access, BlockPos pos) {
		IBlockState state = access.getBlockState(pos);
		if (state.isFullBlock() && state.isFullCube()) return true;
		AxisAlignedBB box = state.getBoundingBox(access, pos);
		double width = box.maxX - box.minX;
		double height = box.maxY - box.minY;
		double length = box.maxZ - box.minZ;
		return (width * height * length) >= 0.75;
	}

	/**
	 * 使指定位置着火
	 * @param world 所在世界
	 * @param pos 坐标
	 */
	public static void setFire(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().isReplaceable(world, pos) ||
				    state.getBlock() instanceof TransferBlock ||
				    state.getBlock() == Blocks.AIR) {
			world.setBlockState(pos, Blocks.FIRE.getDefaultState());
		}
	}
	
	/**
	 * 遍历指定方块周围的所有TE，不包含TE的不会进行遍历
	 * @param world 所在世界
	 * @param pos 中心方块
	 * @param run 要运行的代码，其中TE只遍历到的TE，EnumFacing指TE相对于中心方块的方向
	 */
	public static void forEachAroundTE(World world, BlockPos pos, BiConsumer<TileEntity, EnumFacing> run) {
		TileEntity te;
		for (EnumFacing facing : EnumFacing.values()) {
			te = world.getTileEntity(pos.offset(facing));
			if (te != null) run.accept(te, facing);
		}
	}
	
	/**
	 * 判断other在now的哪个方向
	 */
	public static EnumFacing whatFacing(BlockPos now, BlockPos other) {
		for (EnumFacing facing : EnumFacing.values()) {
			if (now.offset(facing).equals(other)) return facing;
		}
		throw new IllegalArgumentException("now[" + now + "]和other" + other + "不相邻！");
	}
	
}