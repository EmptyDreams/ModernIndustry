package xyz.emptydreams.mi.api.utils.data.math;

/**
 * 存储2D坐标系中的尺寸
 * @author EmptyDreams
 */
public final class Size2D {
	
	private final int width;
	private final int height;
	
	public Size2D(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	@Override
	public String toString() {
		return "width=" + getWidth() + ",height=" + getHeight();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Size2D size2D = (Size2D) o;
		
		if (width != size2D.width) return false;
		return height == size2D.height;
	}
	
	@Override
	public int hashCode() {
		int result = width;
		result = 31 * result + height;
		return result;
	}
}