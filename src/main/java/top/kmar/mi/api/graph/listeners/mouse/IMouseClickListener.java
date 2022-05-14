package top.kmar.mi.api.graph.listeners.mouse;

import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.MouseData;

import javax.annotation.Nonnull;

/**
 * 鼠标点击事件
 * @author EmptyDreams
 */
public interface IMouseClickListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 * @param code 鼠标按键，为1时代表右键，为0代表左键
	 */
	@Nonnull
	MouseData mouseClick(float mouseX, float mouseY, int code);
	
	@Override
	default MouseData invoke(IListenerData data) {
		MouseData real = (MouseData) data;
		return mouseClick(real.getMouseX(), real.getMouseY(), real.getCode());
	}
	
}