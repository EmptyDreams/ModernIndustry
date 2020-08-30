package xyz.emptydreams.mi.api.utils.data;

/**
 * 二维坐标系中的点
 * @author EmptyDreams
 */
public final class Point2D {
	
	private final int x;
	private final int y;
	
	public Point2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/** 获取X轴坐标 */
	public int getX() { return x; }
	/** 获取Y轴坐标 */
	public int getY() { return y; }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Point2D point2D = (Point2D) o;
		
		if (x != point2D.x) return false;
		return y == point2D.y;
	}
	
	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}
	
	@Override
	public String toString() {
		return "x=" + x +
				", y=" + y;
	}
	
}
