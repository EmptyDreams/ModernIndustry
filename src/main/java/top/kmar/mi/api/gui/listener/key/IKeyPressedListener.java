package top.kmar.mi.api.gui.listener.key;

/**
 * @author EmptyDreams
 */
public interface IKeyPressedListener extends IKeyListener {
	
	/**
	 * 按键被按下
	 * @param keyCode 被按下的按键
	 * @param isFocus 该控件是否获得焦点
	 */
	void pressed(int keyCode, boolean isFocus);
	
}