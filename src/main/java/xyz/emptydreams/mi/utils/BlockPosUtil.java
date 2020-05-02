package xyz.emptydreams.mi.utils;

import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.WEST;

import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.electricity.src.block.TransferBlock;

/**
 * 这个类种包含一些常用的工具类方法
 * @author EmptyDremas
 * @version V1.0
 */
public final class BlockPosUtil {
	
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
	 * 读取坐标
	 * @param compound 要读取的标签
	 * @param name 名称
	 * @return 坐标
	 */
	public static BlockPos readBlockPos(NBTTagCompound compound, String name) {
		return new BlockPos(compound.getInteger(name + "_x"),
				compound.getInteger(name + "_y"), compound.getInteger(name + "_z"));
	}
	
	/**
	 * 写入一个坐标到标签中
	 * @param compound 要写入的标签
	 * @param pos 坐标
	 * @param name 名称
	 */
	public static void writeBlockPos(NBTTagCompound compound, BlockPos pos, String name) {
		compound.setInteger(name + "_x", pos.getX());
		compound.setInteger(name + "_y", pos.getY());
		compound.setInteger(name + "_z", pos.getZ());
	}
	
	/**
	 * 判断other在now的哪个方向
	 */
	public static EnumFacing whatFacing(BlockPos now, BlockPos other) {
		for (EnumFacing facing : EnumFacing.values()) {
			if (now.offset(facing).equals(other)) return facing;
		}
		throw new IllegalArgumentException("now和other不相邻！");
	}
	
	/** 去除数组中的null元素 */
	public static BlockPos[] removeNull(BlockPos[] array) {
		if (array == null) return null;
		int i = 0;
		for (Object t : array) {
			if (t != null) ++i;
		}
		if (i == 0) return null;
		BlockPos[] ts = new BlockPos[i];
		i = 0;
		for (BlockPos t : array) {
			if (t != null) {
				ts[i] = t;
				++i;
			}
		}
		return ts;
	}
	
	/** 获取反向的EnumFacing */
	public static EnumFacing upsideDown(EnumFacing facing) {
		switch (facing) {
			case UP : return DOWN;
			case DOWN : return UP;
			case EAST : return WEST;
			case WEST : return EAST;
			case NORTH : return SOUTH;
			default : return NORTH;
		}
	}
	
}
