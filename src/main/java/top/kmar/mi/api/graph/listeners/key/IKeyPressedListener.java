package top.kmar.mi.api.graph.listeners.key;

import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.KeyboardData;

import javax.annotation.Nonnull;

/**
 * 键盘按键被按下的事件
 * @author EmptyDreams
 */
public interface IKeyPressedListener extends IKeyListener {
	
	@Override
	default IDataReader invoke(@NotNull IListenerData data) {
		KeyboardData real = (KeyboardData) data;
		return pressed(real.getCode(), real.isFocus());
	}
	
	/**
	 * 按键被按下
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	@Nonnull
	IDataReader pressed(int keyCode, boolean isFocus);
	
}