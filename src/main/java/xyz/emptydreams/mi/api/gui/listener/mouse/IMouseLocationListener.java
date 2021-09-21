package xyz.emptydreams.mi.api.gui.listener.mouse;

import xyz.emptydreams.mi.api.gui.listener.MouseData;

/**
 * 鼠标在控件内时的事件
 * @author EmptyDreams
 */
public interface IMouseLocationListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	void mouseLocation(float mouseX, float mouseY);
	
	@Override
	default void active(MouseData data) {
		mouseLocation(data.mouseX, data.mouseY);
	}
	
}