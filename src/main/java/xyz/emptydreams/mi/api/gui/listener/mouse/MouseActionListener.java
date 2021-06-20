package xyz.emptydreams.mi.api.gui.listener.mouse;

/**
 * 鼠标左键单击事件
 * @author EmptyDreams
 */
public interface MouseActionListener extends MouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	void mouseAction(float mouseX, float mouseY);
	
}