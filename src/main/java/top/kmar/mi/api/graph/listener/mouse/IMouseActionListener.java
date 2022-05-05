package top.kmar.mi.api.graph.listener.mouse;

import top.kmar.mi.api.graph.listener.IListenerData;
import top.kmar.mi.api.graph.listener.MouseData;

/**
 * 鼠标左键单击事件
 * @author EmptyDreams
 */
public interface IMouseActionListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 */
	void mouseAction(float mouseX, float mouseY);
	
	@Override
	default void invoke(IListenerData data) {
		MouseData real = (MouseData) data;
		mouseAction(real.getMouseX(), real.getMouseY());
	}
	
}