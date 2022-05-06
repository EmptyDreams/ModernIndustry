package top.kmar.mi.api.graph.listeners.mouse;

import top.kmar.mi.api.graph.listeners.IListenerData;

/**
 * 鼠标从控件离开时触发的事件
 * @author EmptyDreams
 */
public interface IMouseExitedListener extends IMouseListener {
	
	void mouseExited();
	
	@Override
	default void invoke(IListenerData data) {
		mouseExited();
	}
	
}