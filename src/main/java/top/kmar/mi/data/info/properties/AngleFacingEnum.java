package top.kmar.mi.data.info.properties;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

/**
 * 表示直角管道中其中一个开口相对于水平方向上的开口的朝向
 * @author EmptyDreams
 */
public enum AngleFacingEnum implements IStringSerializable {
	
	UP,
	DOWN,
	LEFT,
	RIGHT;
	
	/** 获取相反的方向 */
	@Nonnull
	public AngleFacingEnum opposite() {
		switch (this) {
			case UP: return DOWN;
			case DOWN: return UP;
			case LEFT: return RIGHT;
			case RIGHT: return LEFT;
			default: throw new IllegalArgumentException("输入了未知的方向：" + this);
		}
	}
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
	/**
	 * 转化为EnumFacing
	 * @param facing 管道朝向（仅限水平方向）
	 * @throws IllegalArgumentException 如果facing不为水平方向
	 */
	@Nonnull
	public EnumFacing toEnumFacing(EnumFacing facing) {
		switch (this) {
			case UP: return EnumFacing.UP;
			case DOWN: return EnumFacing.DOWN;
			case LEFT:
				switch (facing) {
					case EAST: return EnumFacing.SOUTH;
					case WEST: return EnumFacing.NORTH;
					case SOUTH: return EnumFacing.WEST;
					case NORTH: return EnumFacing.EAST;
					default: throw new IllegalArgumentException("输入了不可能存在的方向：" + facing);
				}
			case RIGHT:
				switch (facing) {
					case EAST: return EnumFacing.NORTH;
					case WEST: return EnumFacing.SOUTH;
					case SOUTH: return EnumFacing.EAST;
					case NORTH: return EnumFacing.WEST;
					default: throw new IllegalArgumentException("输入了不可能存在的方向：" + facing);
				}
			default: throw new IllegalArgumentException("未知的状态：" + this);
		}
	}
	
	/**
	 * 将EnumFacing转化为AngleFacingEnum
	 * @param facing 管道开口朝向
	 * @param after 管道另一开口朝向
	 * @throws IllegalArgumentException 如果输入的facing或after不符合规范
	 */
	@Nonnull
	public static AngleFacingEnum valueOf(EnumFacing facing, EnumFacing after) {
		if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
			if (after == EnumFacing.UP || after == EnumFacing.DOWN) {
				throw new IllegalArgumentException(
						"输入了不可能的方向：facing=" + facing + ",after=" + after);
			} else {
				return valueOf(after, facing);
			}
		}
		switch (after) {
			case UP: return AngleFacingEnum.UP;
			case DOWN: return AngleFacingEnum.DOWN;
			case EAST:
				switch (facing) {
					case NORTH: return LEFT;
					case SOUTH: return RIGHT;
					default: throw new IllegalArgumentException(
							"输入了不可能的方向：facing=" + facing + ",after=" + after);
				}
			case WEST:
				switch (facing) {
					case NORTH: return RIGHT;
					case SOUTH: return LEFT;
					default: throw new IllegalArgumentException(
							"输入了不可能的方向：facing=" + facing + ",after=" + after);
				}
			case NORTH:
				switch (facing) {
					case EAST: return RIGHT;
					case WEST: return LEFT;
					default: throw new IllegalArgumentException(
							"输入了不可能的方向：facing=" + facing + ",after=" + after);
				}
			case SOUTH:
				switch (facing) {
					case EAST: return LEFT;
					case WEST: return RIGHT;
					default: throw new IllegalArgumentException(
							"输入了不可能的方向：facing=" + facing + ",after=" + after);
				}
			default: throw new IllegalArgumentException("输入了未知的方向：" + after);
		}
	}
	
	/**
	 * 判断两个方向是否可以进行组合
	 * @param facing 正方向
	 * @param after 后方向
	 */
	public static boolean match(EnumFacing facing, EnumFacing after) {
		return facing != after && facing.getOpposite() != after;
	}
	
}