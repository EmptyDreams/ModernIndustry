package top.kmar.mi.api.gui.listener.mouse;

import top.kmar.mi.api.gui.listener.MouseData;

/**
 * @author EmptyDreams
 */
public interface IMouseReleasedListener extends IMouseListener {
	
	/**
	 * @param mouseX 鼠标X轴坐标（相对于Gui）
	 * @param mouseY 鼠标Y轴坐标（相对于Gui）
	 * @param code 鼠标按键，为1时代表右键，为0代表左键
	 */
	void mouseReleased(float mouseX, float mouseY, float code);
	
	@Override
	default void active(MouseData data) {
		mouseReleased(data.mouseX, data.mouseY, data.code);
	}
	
}