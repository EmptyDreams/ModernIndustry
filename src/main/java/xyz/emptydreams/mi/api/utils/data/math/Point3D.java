package xyz.emptydreams.mi.api.utils.data.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3i;

/**
 * 三维坐标系中的点
 * @author EmptyDreams
 */
public final class Point3D {
	
	private final int x;
	private final int y;
	private final int z;
	
	public Point3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(Entity entity) {
		this(entity.getPosition());
	}
	
	public Point3D(Vec3i vec3d) {
		this(vec3d.getX(), vec3d.getY(), vec3d.getZ());
	}
	
	/** 计算该点与指定点间的距离 */
	public double distance(Point3D point) {
		int x = point.getX() - getX();
		int y = point.getY() - getY();
		int z = point.getZ() - getZ();
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	/** 获取X轴坐标 */
	public int getX() { return x; }
	/** 获取Y轴坐标 */
	public int getY() { return y; }
	/** 获取Z轴坐标 */
	public int getZ() { return z; }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Point3D point3D = (Point3D) o;
		
		if (x != point3D.x) return false;
		if (y != point3D.y) return false;
		return z == point3D.z;
	}
	
	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}
	
	@Override
	public String toString() {
		return "x=" + x +
				", y=" + y +
				", z=" + z;
	}
	
}