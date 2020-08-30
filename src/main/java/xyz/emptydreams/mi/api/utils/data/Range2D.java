package xyz.emptydreams.mi.api.utils.data;

import static xyz.emptydreams.mi.api.utils.data.Range3D.sq;

/**
 * 二维坐标系中的范围
 * @author EmptyDreams
 */
public final class Range2D {
	
	private final int x;
	private final int y;
	private final int r;
	
	public Range2D(int x, int y, int r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}
	
	public Range2D(Point2D point, int r) {
		this(point.getX(), point.getY(), r);
	}
	
	/** 获取X轴坐标 */
	public int getX() { return x; }
	/** 获取X轴坐标 */
	public int getY() { return y; }
	/** 获取X轴坐标 */
	public int getRadius() { return r; }
	
	/** 判断点是否在范围内 */
	public boolean isIn(Point2D point) {
		int x2 = sq(getX() - point.getX());
		int y2 = sq(getY() - point.getY());
		double length = Math.sqrt(x2 + y2);
		return length <= getRadius();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Range2D range2D = (Range2D) o;
		
		if (x != range2D.x) return false;
		if (y != range2D.y) return false;
		return r == range2D.r;
	}
	
	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + r;
		return result;
	}
	
	@Override
	public String toString() {
		return "x=" + x +
				", y=" + y +
				", r=" + r;
	}
	
}
