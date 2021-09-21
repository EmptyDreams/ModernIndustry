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

import java.util.Collections;
import java.util.List;

/**
 * 鼠标按键触发器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class ListenerTrigger {

	/** 尝试触发鼠标松开事件 */
	public static void activateReleased(IComponentManager frame, IComponent component,
	                                    float mouseX, float mouseY, int code) {
		frame.activeMouseListenerNoLog(IMouseReleasedListener.class,
				component, new MouseData(mouseX, mouseY, code, 0), false, Collections.emptyList());
	}
	
	/** 尝试触发鼠标离开事件 */
	public static void activateExited(IComponentManager frame, IComponent component) {
		frame.activeMouseListenerNoLog(IMouseExitedListener.class,
				component, MouseData.EMPTY, false, Collections.emptyList());
	}
	
	/** 尝试触发鼠标左键单击事件 */
	public static List<IComponent> activateAction(IComponentManager frame,
	                                              IComponent component, float mouseX, float mouseY) {
		return frame.activeMouseListener(IMouseActionListener.class,
				component, new MouseData(mouseX, mouseY, -1, 0),
				true, Collections.emptyList());
	}
	
	/** 尝试触发鼠标点击事件 */
	public static List<IComponent> activateClick(IComponentManager frame, IComponent component,
	                                 float mouseX, float mouseY, int code) {
		return frame.activeMouseListener(IMouseClickListener.class,
				component, new MouseData(mouseX, mouseY, code, 0), true, Collections.emptyList());
	}
	
	/** 尝试触发鼠标进入的事件 */
	public static List<IComponent> activateEntered(IComponentManager frame,
	                                               IComponent component, float mouseX, float mouseY,
	                                               List<IComponent> ignore) {
		return frame.activeMouseListener(IMouseEnteredListener.class,
				component, new MouseData(mouseX, mouseY, -1, 0), true, ignore);
	}
	
	/** 尝试触发鼠标坐标事件 */
	public static void activateLocation(IComponentManager frame,
	                                                IComponent component, float mouseX, float mouseY) {
		frame.activeMouseListenerNoLog(IMouseLocationListener.class,
				component, new MouseData(mouseX, mouseY, -1, 0),
				false, Collections.emptyList());
	}
	
	/** 尝试触发鼠标滚轮事件 */
	public static void activateWheel(IComponentManager frame,
	                                             IComponent component, int wheel) {
		frame.activeMouseListenerNoLog(IMouseWheelListener.class,
				component, new MouseData(0, 0, -1, wheel),
				true, Collections.emptyList());
	}
	
	/** 尝试触发键盘按下事件 */
	public static void activateKeyPressed(IComponentManager frame,
	                                      IComponent component, int keyCode, boolean isFocus) {
		component.activateListener(frame.getFrame(),
				IKeyPressedListener.class, it -> it.pressed(keyCode, isFocus));
	}
	
	/** 尝试触发键盘释放事件 */
	public static void activateKeyRelease(IComponentManager frame,
	                                      IComponent component, int keyCode, boolean isFocus) {
		component.activateListener(frame.getFrame(),
				IKeyReleaseListener.class, it -> it.release(keyCode, isFocus));
	}
	
}