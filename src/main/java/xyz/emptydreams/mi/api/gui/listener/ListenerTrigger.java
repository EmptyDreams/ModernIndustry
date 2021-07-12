package xyz.emptydreams.mi.api.gui.listener;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.listener.key.KeyListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseClickListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseEnteredListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseExitedListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseLocationListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseReleasedListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseWheelListener;

/**
 * 鼠标按键触发器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class ListenerTrigger {
	
	/** 尝试触发鼠标松开事件 */
	public static void activateReleased(MIFrame frame, IComponent component,
	                                    float mouseX, float mouseY, int mouseButton) {
		component.activateListener(frame, MouseReleasedListener.class, it -> it.mouseReleased(mouseX, mouseY, mouseButton));
	}
	
	/** 尝试触发鼠标离开事件 */
	public static void activateExited(MIFrame frame, IComponent component, float mouseX, float mouseY) {
		component.activateListener(frame, MouseExitedListener.class, it -> it.mouseExited(mouseX, mouseY));
	}
	
	/** 尝试触发鼠标左键单击事件 */
	public static void activateAction(MIFrame frame, IComponent component, float mouseX, float mouseY) {
		component.activateListener(frame, MouseActionListener.class, it -> it.mouseAction(mouseX, mouseY));
	}
	
	/** 尝试触发鼠标点击事件 */
	public static void activateClick(MIFrame frame, IComponent component,
	                                 float mouseX, float mouseY, int mouseButton) {
		component.activateListener(frame, MouseClickListener.class, it -> it.mouseClick(mouseX, mouseY, mouseButton));
	}
	
	/** 尝试触发鼠标进入的事件 */
	public static void activateEntered(MIFrame frame, IComponent component, float mouseX, float mouseY) {
		component.activateListener(frame, MouseEnteredListener.class, it -> it.mouseEntered(mouseX, mouseY));
	}
	
	/** 尝试触发鼠标坐标事件 */
	public static void activateLocation(MIFrame frame, IComponent component, float mouseX, float mouseY) {
		component.activateListener(frame, MouseLocationListener.class, it -> it.mouseLocation(mouseX, mouseY));
	}
	
	/** 尝试触发鼠标滚轮事件 */
	public static void activateWheel(MIFrame frame, IComponent component, int wheel) {
		component.activateListener(frame, MouseWheelListener.class, it -> it.mouseWheel(wheel));
	}
	
	/** 尝试触发键盘按下事件 */
	public static void activateKeyPressed(MIFrame frame, IComponent component, int keyCode, boolean isFocus) {
		component.activateListener(frame, KeyListener.class, it -> it.pressed(keyCode, isFocus));
	}
	
	/** 尝试触发键盘释放事件 */
	public static void activateKeyRelease(MIFrame frame, IComponent component, int keyCode, boolean isFocus) {
		component.activateListener(frame, KeyListener.class, it -> it.release(keyCode, isFocus));
	}
	
}