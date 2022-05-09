package top.kmar.mi.api.graph.listeners.mouse;

import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.MouseData;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public interface IMouseReleasedListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 * @param code 鼠标按键，为1时代表右键，为0代表左键
	 */
	@Nonnull
	IDataReader mouseReleased(float mouseX, float mouseY, float code);
	
	@Override
	default IDataReader invoke(IListenerData data) {
		MouseData real = (MouseData) data;
		return mouseReleased(real.getMouseX(), real.getMouseY(), real.getCode());
	}
	
}