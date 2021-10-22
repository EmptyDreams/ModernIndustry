package xyz.emptydreams.mi.api.utils.data.math;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * 三维坐标系中的范围.<br>
 *     <b>x、y、z表示球心坐标，r表示球的半径，单位：米（m）</b>
 * @author EmptyDreams
 */
public final class Range3D {
	
	private final int x;
	private final int y;
	private final int z;
	private final int r;
	
	/**
	 * 创建一个区域表示器
	 * @param point 球心坐标
	 * @param r 球心半径
	 */
	public Range3D(Point3D point, int r) {
		this(point.getX(), point.getY(), point.getZ(), r);
	}
	
	/**
	 * 创建一个区域表示器
	 * @param pos 球心坐标
	 * @param r 球半径
	 */
	public Range3D(BlockPos pos, int r) {
		this(pos.getX(), pos.getY(), pos.getZ(), r);
	}
	
	/**
	 * 创建一个区域表示器
	 * @param x 球心X轴坐标
	 * @param y 球心Y轴坐标
	 * @param z 球心Z轴坐标
	 * @param r 球半径
	 */
	public Range3D(int x, int y, int z, int r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
	}
	
	/** 获取X轴坐标 */
	public int getX() { return x; }
	/** 获取Y轴坐标 */
	public int getY() { return y; }
	/** 获取Z轴坐标 */
	public int getZ() { return z; }
	/** 获取半径 */
	public int getRadius() { return r; }
	
	/** 判断指定的点是否在范围内 */
	public boolean isIn(Point3D point) {
		int x2 = sq(getX() - point.getX());
		int y2 = sq(getY() - point.getY());
		int z2 = sq(getZ() - point.getZ());
		double length = MathHelper.sqrt(x2 + y2 + z2);
		return length <= getRadius();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Range3D range3D = (Range3D) o;
		
		if (x != range3D.x) return false;
		if (y != range3D.y) return false;
		if (z != range3D.z) return false;
		return r == range3D.r;
	}
	
	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		result = 31 * result + r;
		return result;
	}
	
	@Override
	public String toString() {
		return "x=" + x +
				", y=" + y +
				", z=" + z +
				", r=" + r;
	}
	
	/** 平方 */
	public static int sq(int number) {
		return number * number;
	}
	
}