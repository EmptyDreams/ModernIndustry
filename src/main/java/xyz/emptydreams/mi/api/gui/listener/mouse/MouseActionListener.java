package xyz.emptydreams.mi.api.gui.listener.mouse;

/**
 * 鼠标左键单击事件
 * @author EmptyDreams
 */
public interface MouseActionListener extends MouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于窗体）
	 * @param mouseY 鼠标Y轴坐标（相对于窗体）
	 */
	void mouseAction(float mouseX, float mouseY);
	
}
