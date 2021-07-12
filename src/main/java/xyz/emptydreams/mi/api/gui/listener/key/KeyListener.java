package xyz.emptydreams.mi.api.gui.listener.key;

import xyz.emptydreams.mi.api.gui.listener.IListener;

/**
 * 键盘事件
 * @author EmptyDreams
 */
public interface KeyListener extends IListener {
	
	/**
	 * 按键被按下
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	void pressed(int keyCode, boolean isFocus);
	
	/**
	 * 按键被释放
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	void release(int keyCode, boolean isFocus);
	
}