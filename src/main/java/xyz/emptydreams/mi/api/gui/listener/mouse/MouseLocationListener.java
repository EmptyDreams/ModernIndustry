package xyz.emptydreams.mi.api.gui.listener.mouse;

/**
 * 鼠标在控件内时的事件
 * @author EmptyDreams
 */
public interface MouseLocationListener extends MouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	void mouseMLocation(float mouseX, float mouseY);
	
}