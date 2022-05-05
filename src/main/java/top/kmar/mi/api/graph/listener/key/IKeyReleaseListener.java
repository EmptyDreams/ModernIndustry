package top.kmar.mi.api.graph.listener.key;

import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.graph.listener.IListenerData;
import top.kmar.mi.api.graph.listener.KeyboardData;

/**
 * @author EmptyDreams
 */
public interface IKeyReleaseListener extends IKeyListener {
	
	@Override
	default void invoke(@NotNull IListenerData data) {
		KeyboardData real = (KeyboardData) data;
		release(real.getCode(), real.isFocus());
	}
	
	/**
	 * 按键被释放
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	void release(int keyCode, boolean isFocus);
	
}