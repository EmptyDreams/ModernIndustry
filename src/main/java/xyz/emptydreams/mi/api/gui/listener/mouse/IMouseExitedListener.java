package xyz.emptydreams.mi.api.gui.listener.mouse;

import xyz.emptydreams.mi.api.gui.listener.MouseData;

/**
 * 鼠标从控件离开时触发的事件
 * @author EmptyDreams
 */
public interface IMouseExitedListener extends IMouseListener {
	
	void mouseExited();
	
	@Override
	default void active(MouseData data) {
		mouseExited();
	}
	
}