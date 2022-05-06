package top.kmar.mi.api.graph.listeners.key;

import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.KeyboardData;

/**
 * 键盘按键被按下的事件
 * @author EmptyDreams
 */
public interface IKeyPressedListener extends IKeyListener {
	
	@Override
	default void invoke(@NotNull IListenerData data) {
		KeyboardData real = (KeyboardData) data;
		pressed(real.getCode(), real.isFocus());
	}
	
	/**
	 * 按键被按下
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	void pressed(int keyCode, boolean isFocus);
	
}