package top.kmar.mi.api.graph.listeners.key;

import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.graph.listeners.IListenerData;
import top.kmar.mi.api.graph.listeners.KeyboardData;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public interface IKeyReleaseListener extends IKeyListener {
	
	@Override
	default KeyboardData invoke(@NotNull IListenerData data) {
		KeyboardData real = (KeyboardData) data;
		return release(real.getCode(), real.isFocus());
	}
	
	/**
	 * 按键被释放
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	@Nonnull
	KeyboardData release(int keyCode, boolean isFocus);
	
}