package minedreams.mi.tools;

import java.util.Random;
import java.util.function.BiConsumer;

import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.WEST;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 这个类种包含一些常用的工具类方法
 * @author EmptyDremas
 * @version V1.0
 */
public final class Tools {
	
	/**
	 * 遍历指定方块周围的所有TE，不包含TE的不会进行遍历
	 * @param world 所在世界
	 * @param pos 中心方块
	 * @param run 要运行的代码，其中TE只遍历到的TE，EnumFacing指TE相对于中心方块的方向
	 */
	public static void forEachAroundTE(World world, BlockPos pos, BiConsumer<? super TileEntity, EnumFacing> run) {
		TileEntity te = world.getTileEntity(pos.up());
		if (te != null) run.accept(te, UP);
		te = world.getTileEntity(pos.down());
		if (te != null) run.accept(te, DOWN);
		te = world.getTileEntity(pos.west());
		if (te != null) run.accept(te, WEST);
		te = world.getTileEntity(pos.north());
		if (te != null) run.accept(te, NORTH);
		te = world.getTileEntity(pos.south());
		if (te != null) run.accept(te, SOUTH);
		te = world.getTileEntity(pos.east());
		if (te != null) run.accept(te, EAST);
	}
	
	/** 只限水平范围 */
	public static final int HORIZONTAL = 0;
	/** 只限垂直范围 */
	public static final int VERTICAL = 1;
	/** 不限范围 */
	public static final int ALL = 2;
	private static final EnumFacing[] _HORIAONTAL = { NORTH, WEST, SOUTH, EAST };
	private static final EnumFacing[] _VERTICAL = { UP, DOWN };
	private static final EnumFacing[] _ALL = { NORTH, WEST, UP, DOWN, SOUTH, EAST };
	
	/**
	 * 从中心方块附近随机查找指定数量的空气坐标
	 *
	 * @param world 所在世界
	 * @param center 中心方块坐标
	 * @param model 模式
	 * @return 数组长度与amount一致，其中可能有空值
	 *
	 * @throws IllegalArgumentException 如果model不在范围之内
	 *
	 * @see #HORIZONTAL
	 * @see #VERTICAL
	 * @see #ALL
	 */
	public static BlockPos randomPos(World world, BlockPos center, int model) {
		final Random random = new Random();
		//最大尝试次数
		final int allSize;
		final EnumFacing[] facing;
		
		switch (model) {
			case HORIZONTAL:
				allSize = 20;
				facing = _HORIAONTAL;
				break;
			case VERTICAL:
				allSize = 10;
				facing = _VERTICAL;
				break;
			case ALL:
				allSize = 30;
				facing = _ALL;
				break;
			default: throw new IllegalArgumentException("model[" + model + "]不在范围之内");
		}
		
		BlockPos temp;
		int index;
		int t = 0;
		IBlockState state;
		do {
			++t;
			index = random.nextInt(facing.length);
			temp = getBlockPos(center, facing[index], 1);
		} while (t < allSize && !world.isAirBlock(temp) && !Blocks.FIRE.canCatchFire(world, temp, DOWN));
		
		return temp;
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
		BlockPos cache = now.west();
		int code = other.hashCode();
		if (cache.hashCode() == code && cache.equals(other))
			return WEST;
		cache = now.east();
		if (cache.hashCode() == code && cache.equals(other))
			return EAST;
		cache = now.south();
		if (cache.hashCode() == code && cache.equals(other))
			return SOUTH;
		cache = now.north();
		if (cache.hashCode() == code && cache.equals(other))
			return NORTH;
		cache = now.down();
		if (cache.hashCode() == code && cache.equals(other))
			return DOWN;
		return UP;
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
	
	/** 在数组中查找某个元素 */
	public static<T> int findValue(T[] array, T t) {
		if (array == null) return -1;
		if (t == null) {
			for (int i = 0; i < array.length; ++i) {
				if (array[i] == null) return i;
			}
		}
		int hash = t.hashCode();
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == null) continue;
			if (array[i].hashCode() == hash && array[i].equals(t))
				return i;
		}
		return -1;
	}
	
	/** 检查数组中是否包含某个元素 */
	public static<T> boolean hasValue(T[] array, T t) {
		if (array == null) return false;
		for (T a : array) {
			if (a == null) {
				if (t == null) return true;
				continue;
			}
			if (a.equals(t)) return true;
		}
		return false;
	}
	
	public static BlockPos getBlockPos(BlockPos pos, EnumFacing facing, int length) {
		switch (facing) {
			case DOWN: return pos.down(length);
			case UP: return pos.up(length);
			case NORTH: return pos.north(length);
			case SOUTH: return pos.south(length);
			case WEST: return pos.west(length);
			default: return pos.east(length);
		}
	}
	
	/** 从state数组获取block数组 */
	public static Block[] toBlocks(IBlockState[] states) {
		Block[] blocks = new Block[states.length];
		for (int i = 0; i < states.length; ++i) {
			blocks[i] = states[i].getBlock();
		}
		return blocks;
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
	
	/**
	 * 获取方块周围的BlockPos
	 * @param pos 当前方块的坐标
	 */
	public static BlockPos[] getBlockPosList(BlockPos pos) {
		BlockPos[] list = new BlockPos[6];
		list[0] = pos.up();
		list[1] = pos.down();
		list[2] = pos.east();
		list[3] = pos.west();
		list[4] = pos.north();
		list[5] = pos.south();
		return list;
	}
	
}
