package top.kmar.mi.api.gui.listener.mouse;

import top.kmar.mi.api.gui.listener.MouseData;

/**
 * 鼠标滚轮事件
 * @author EmptyDreams
 */
public interface IMouseWheelListener extends IMouseListener {
	
	/**
	 * 鼠标滚轮滚动时触发
	 * @param wheel 滚轮向下滚动为负，向上为正
	 */
	void mouseWheel(int wheel);
	
	@Override
	default void active(MouseData data) {
		mouseWheel(data.wheel);
	}
	
}