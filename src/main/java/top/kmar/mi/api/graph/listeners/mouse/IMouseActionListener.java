package top.kmar.mi.api.graph.listeners.mouse;

import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.MouseData;

import javax.annotation.Nonnull;

/**
 * 鼠标左键单击事件
 * @author EmptyDreams
 */
public interface IMouseActionListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	@Nonnull
	IDataReader mouseAction(float mouseX, float mouseY);
	
	@Override
	default IDataReader invoke(IListenerData data) {
		MouseData real = (MouseData) data;
		return mouseAction(real.getMouseX(), real.getMouseY());
	}
	
}