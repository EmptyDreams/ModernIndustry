package xyz.emptydreams.mi.api.gui.listener.mouse;

/**
 * 鼠标从控件离开时触发的事件
 * @author EmptyDreams
 */
public interface MouseExitedListener extends MouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于窗体）
	 * @param mouseY 鼠标Y轴坐标（相对于窗体）
	 */
	void mouseExited(float mouseX, float mouseY);
	
}
