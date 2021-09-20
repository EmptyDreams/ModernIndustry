package xyz.emptydreams.mi.api.gui.listener;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponentManager;
import xyz.emptydreams.mi.api.gui.listener.key.IKeyPressedListener;
import xyz.emptydreams.mi.api.gui.listener.key.IKeyReleaseListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseClickListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseEnteredListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseExitedListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseLocationListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseReleasedListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseWheelListener;

/**
 * 鼠标按键触发器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class ListenerTrigger {

	/** 尝试触发鼠标松开事件 */
	public static void activateReleased(IComponentManager frame, IComponent component,
	                                    float mouseX, float mouseY, int code) {
		frame.activeMouseListener(IMouseReleasedListener.class, component, mouseX, mouseY, code, 0);
	}
	
	/** 尝试触发鼠标离开事件 */
	public static void activateExited(IComponentManager frame, IComponent component) {
		frame.activeMouseListener(IMouseExitedListener.class, component, 0, 0, -1, 0);
	}
	
	/** 尝试触发鼠标左键单击事件 */
	public static void activateAction(IComponentManager frame, IComponent component, float mouseX, float mouseY) {
		frame.activeMouseListener(IMouseActionListener.class, component, mouseX, mouseY, -1, 0);
	}
	
	/** 尝试触发鼠标点击事件 */
	public static void activateClick(IComponentManager frame, IComponent component,
	                                 float mouseX, float mouseY, int code) {
		frame.activeMouseListener(IMouseClickListener.class, component, mouseX, mouseY, code, 0);
	}
	
	/** 尝试触发鼠标进入的事件 */
	public static void activateEntered(IComponentManager frame, IComponent component, float mouseX, float mouseY) {
		frame.activeMouseListener(IMouseEnteredListener.class, component, mouseX, mouseY, -1, 0);
	}
	
	/** 尝试触发鼠标坐标事件 */
	public static void activateLocation(IComponentManager frame, IComponent component, float mouseX, float mouseY) {
		frame.activeMouseListener(IMouseLocationListener.class, component, mouseX, mouseY, -1, 0);
	}
	
	/** 尝试触发鼠标滚轮事件 */
	public static void activateWheel(IComponentManager frame, IComponent component, int wheel) {
		frame.activeMouseListener(IMouseWheelListener.class, component, 0, 0, -1, wheel);
	}
	
	/** 尝试触发键盘按下事件 */
	public static void activateKeyPressed(IComponentManager frame, IComponent component, int keyCode, boolean isFocus) {
		component.activateListener(frame.getFrame(), IKeyPressedListener.class, it -> it.pressed(keyCode, isFocus));
	}
	
	/** 尝试触发键盘释放事件 */
	public static void activateKeyRelease(IComponentManager frame, IComponent component, int keyCode, boolean isFocus) {
		component.activateListener(frame.getFrame(), IKeyReleaseListener.class, it -> it.release(keyCode, isFocus));
	}
	
}