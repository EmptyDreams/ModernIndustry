package top.kmar.mi.api.gui.listener.mouse;

import top.kmar.mi.api.gui.listener.IListener;
import top.kmar.mi.api.gui.listener.MouseData;

/**
 * 鼠标事件
 * @author EmptyDreams
 */
public interface IMouseListener extends IListener {
	
	/**
	 * 子事件应当重写该方法来调用自己的触发方法
	 * @param data 鼠标参数
	 */
	void active(MouseData data);
	
}