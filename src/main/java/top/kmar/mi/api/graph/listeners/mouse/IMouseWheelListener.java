package top.kmar.mi.api.graph.listeners.mouse;

import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.MouseData;

import javax.annotation.Nonnull;

/**
 * 鼠标滚轮事件
 * @author EmptyDreams
 */
public interface IMouseWheelListener extends IMouseListener {
	
	/**
	 * 鼠标滚轮滚动时触发
	 * @param wheel 滚轮向下滚动为负，向上为正
	 */
	@Nonnull
	IDataReader mouseWheel(int wheel);
	
	@Override
	default IDataReader invoke(IListenerData data) {
		return mouseWheel(((MouseData) data).getWheel());
	}
	
}