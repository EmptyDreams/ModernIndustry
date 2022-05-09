package top.kmar.mi.api.graph.listeners.mouse;

import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.MouseData;

import javax.annotation.Nonnull;

/**
 * 在鼠标进入控件时触发的事件
 * @author EmptyDreams
 */
public interface IMouseEnteredListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	@Nonnull
	IDataReader mouseEntered(float mouseX, float mouseY);
	
	@Override
	default IDataReader invoke(IListenerData data) {
		MouseData real = (MouseData) data;
		return mouseEntered(real.getMouseX(), real.getMouseY());
	}
	
}