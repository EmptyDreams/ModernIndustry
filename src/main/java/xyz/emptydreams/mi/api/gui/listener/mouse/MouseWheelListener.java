package xyz.emptydreams.mi.api.gui.listener.mouse;

/**
 * 鼠标滚轮事件
 * @author EmptyDreams
 */
public interface MouseWheelListener extends MouseListener {
	
	/**
	 * 鼠标滚轮滚动时触发
	 * @param wheel 向下滚动为负，向上为正
	 */
	void mouseWheel(int wheel);
	
}