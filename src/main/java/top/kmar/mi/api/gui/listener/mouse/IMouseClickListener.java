package top.kmar.mi.api.gui.listener.mouse;

import top.kmar.mi.api.gui.listener.MouseData;

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
	void mouseClick(float mouseX, float mouseY, int code);
	
	@Override
	default void active(MouseData data) {
		mouseClick(data.mouseX, data.mouseY, data.code);
	}
	
}