package top.kmar.mi.api.gui.listener;

/**
 * 存储鼠标数据
 * @author EmptyDreams
 */
public final class MouseData {
	
	/** 无效的数据 */
	public static final MouseData EMPTY = new MouseData(0, 0, -1, 0);
	
	public final float mouseX;
	public final float mouseY;
	public final int code;
	public final int wheel;
	
	public MouseData(float mouseX, float mouseY, int code, int wheel) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.code = code;
		this.wheel = wheel;
	}
	
	/** 只修改鼠标坐标生成一个新的MouseData */
	public MouseData create(float mouseX, float mouseY) {
		return new MouseData(mouseX, mouseY, code, wheel);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MouseData mouseData = (MouseData) o;
		
		if (Float.compare(mouseData.mouseX, mouseX) != 0) return false;
		if (Float.compare(mouseData.mouseY, mouseY) != 0) return false;
		if (code != mouseData.code) return false;
		return wheel == mouseData.wheel;
	}
	
	@Override
	public int hashCode() {
		int result = (mouseX != 0.0f ? Float.floatToIntBits(mouseX) : 0);
		result = 31 * result + (mouseY != 0.0f ? Float.floatToIntBits(mouseY) : 0);
		result = 31 * result + code;
		result = 31 * result + wheel;
		return result;
	}
	
	@Override
	public String toString() {
		return "mouseX=" + mouseX +
				", mouseY=" + mouseY +
				", code=" + code +
				", wheel=" + wheel;
	}
}