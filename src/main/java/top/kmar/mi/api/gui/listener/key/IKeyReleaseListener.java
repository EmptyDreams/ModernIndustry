package top.kmar.mi.api.gui.listener.key;

/**
 * @author EmptyDreams
 */
public interface IKeyReleaseListener extends IKeyListener {
	
	/**
	 * 按键被释放
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	void release(int keyCode, boolean isFocus);
	
}