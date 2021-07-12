package xyz.emptydreams.mi.api.gui.listener.key;

import xyz.emptydreams.mi.api.gui.listener.IListener;

/**
 * 键盘事件
 * @author EmptyDreams
 */
public interface KeyListener extends IListener {
	
	/** 按键被按下 */
	void pressed(int keyCode, boolean isFocus);
	
	/** 按键被释放 */
	void release(int keyCode, boolean isFocus);
	
}