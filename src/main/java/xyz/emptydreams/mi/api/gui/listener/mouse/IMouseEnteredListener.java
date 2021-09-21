package xyz.emptydreams.mi.api.gui.listener.mouse;

import xyz.emptydreams.mi.api.gui.listener.MouseData;

/**
 * 在鼠标进入控件时触发的事件
 * @author EmptyDreams
 */
public interface IMouseEnteredListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	void mouseEntered(float mouseX, float mouseY);
	
	@Override
	default void active(MouseData data) {
		mouseEntered(data.mouseX, data.mouseY);
	}
	
}