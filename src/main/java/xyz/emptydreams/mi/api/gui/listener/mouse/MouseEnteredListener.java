package xyz.emptydreams.mi.api.gui.listener.mouse;

/**
 * 在鼠标进入控件时触发的事件
 * @author EmptyDreams
 */
public interface MouseEnteredListener extends MouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于窗体）
	 * @param mouseY 鼠标Y轴坐标（相对于窗体）
	 */
	void mouseEntered(float mouseX, float mouseY);

}
