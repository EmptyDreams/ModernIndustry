package top.kmar.mi.api.graph.listener.mouse;

import top.kmar.mi.api.graph.listener.IListenerData;
import top.kmar.mi.api.graph.listener.MouseData;

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
	default void invoke(IListenerData data) {
		mouseWheel(((MouseData) data).getWheel());
	}
	
}