package xyz.emptydreams.mi.api.gui.listener.mouse;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.component.IComponent;

/**
 * 鼠标按键触发器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class MouseListenerTrigger {
	
	/** 尝试触发鼠标松开事件 */
	public static void activateReleased(IComponent component, float mouseX, float mouseY, int mouseButton) {
		component.activateListener(MouseReleasedListener.class, it -> {
			MouseReleasedListener listener = (MouseReleasedListener) it;
			listener.mouseReleased(mouseX, mouseY, mouseButton);
		});
	}
	
	/** 尝试触发鼠标离开事件 */
	public static void activateExited(IComponent component, float mouseX, float mouseY) {
		component.activateListener(MouseExitedListener.class, it -> {
			MouseExitedListener listener = (MouseExitedListener) it;
			listener.mouseExited(mouseX, mouseY);
		});
	}
	
	/** 尝试触发鼠标左键单击事件 */
	public static void activateAction(IComponent component, float mouseX, float mouseY) {
		component.activateListener(MouseActionListener.class, it -> {
			MouseActionListener listener = (MouseActionListener) it;
			listener.mouseAction(mouseX, mouseY);
		});
	}
	
	/** 尝试触发鼠标点击事件 */
	public static void activateClick(IComponent component, float mouseX, float mouseY, int mouseButton) {
		component.activateListener(MouseClickListener.class, it -> {
			MouseClickListener listener = (MouseClickListener) it;
			listener.mouseClick(mouseX, mouseY, mouseButton);
		});
	}
	
	/** 尝试触发鼠标进入的事件 */
	public static void activateEntered(IComponent component, float mouseX, float mouseY) {
		component.activateListener(MouseExitedListener.class, it -> {
			MouseEnteredListener listener = (MouseEnteredListener) it;
			listener.mouseEntered(mouseX, mouseY);
		});
	}
	
}
