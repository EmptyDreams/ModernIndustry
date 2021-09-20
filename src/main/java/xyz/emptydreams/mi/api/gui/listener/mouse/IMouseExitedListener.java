package xyz.emptydreams.mi.api.gui.listener.mouse;

/**
 * 鼠标从控件离开时触发的事件
 * @author EmptyDreams
 */
public interface IMouseExitedListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	void mouseExited();
	
	default void active(float mouseX, float mouseY, int code, int wheel) {
		mouseExited();
	}
	
}