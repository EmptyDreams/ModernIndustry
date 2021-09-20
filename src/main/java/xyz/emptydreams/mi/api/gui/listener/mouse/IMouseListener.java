package xyz.emptydreams.mi.api.gui.listener.mouse;

import xyz.emptydreams.mi.api.gui.listener.IListener;

/**
 * 鼠标事件
 * @author EmptyDreams
 */
public interface IMouseListener extends IListener {
	
	/**
	 * 子事件应当重写该方法来调用自己的触发方法
	 * @param mouseX 鼠标X轴坐标（相对于控件）
	 * @param mouseY 鼠标Y轴坐标（相对于控件）
	 * @param code 鼠标按钮代码
	 * @param wheel 滚轮滚动的距离，向下为负，向上为正
	 */
	void active(float mouseX, float mouseY, int code, int wheel);
	
}