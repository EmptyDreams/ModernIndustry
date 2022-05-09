package top.kmar.mi.api.graph.listeners.mouse;

import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.graph.listeners.IListenerData;

import javax.annotation.Nonnull;

/**
 * 鼠标从控件离开时触发的事件
 * @author EmptyDreams
 */
public interface IMouseExitedListener extends IMouseListener {
	
	@Nonnull
	IDataReader mouseExited();
	
	@Override
	default IDataReader invoke(IListenerData data) {
		return mouseExited();
	}
	
}