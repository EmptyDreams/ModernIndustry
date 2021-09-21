package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.inventory.Slot;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseListener;
import xyz.emptydreams.mi.api.utils.container.IntWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 控件组
 * @author EmptyDreams
 */
public interface IComponentManager {
	
	/**
	 * 添加一个控件
	 * @param component 要添加的控件
	 * @throws NullPointerException 如果component == null
	 */
	void add(IComponent component);
	
	/**
	 * 遍历所有组件
	 * @param predicate 对元素的操作，返回值用于判断是否继续遍历
	 */
	void forEachComponent(Predicate<? super IComponent> predicate);
	
	/** 遍历所有控件 */
	default void forEachAllComponent(Consumer<? super IComponent> consumer) {
		forEachComponent(it -> {
			consumer.accept(it);
			return true;
		});
	}
	
	/** 克隆控件列表 */
	ArrayList<IComponent> cloneComponent();
	
	/** 获取控件数量 */
	int componentSize();
	
	/** 获取控件组的X坐标 */
	int getX();
	
	/** 获取控件组的Y坐标 */
	int getY();
	
	/**
	 * 添加一个Slot到窗体
	 * @return 传入的Slot本身
	 */
	default Slot addSlotToContainer(Slot slot) {
		return getFrame().addSlotToContainer(slot);
	}
	
	/** 为指定控件分配网路ID */
	default void allocID(IComponent it) {
		getFrame().allocID(it);
	}
	
	/**
	 * 获取父级管理类
	 * @return 如果当前管理类已经为最高级，则返回自身
	 */
	@Nonnull
	IComponentManager getSuperManager();
	
	/** 判断当前管理类是否为最高级（窗体） */
	boolean isFrame();
	
	/** 获取窗体对象 */
	default MIFrame getFrame() {
		IComponentManager result = this;
		while (!result.isFrame()) {
			result = result.getSuperManager();
		}
		return (MIFrame) result;
	}
	
	/**
	 * 触发指定控件的鼠标
	 * @param listenerClass 触发的事件的class
	 * @param component 要触发事件的控件，留空则尝试触发所有符合条件的控件
	 * @param mouseX 鼠标X轴坐标（相对于控件组）
	 * @param mouseY 鼠标Y轴坐标（相对于控件组）
	 * @param code 鼠标按钮代码
	 * @param wheel 鼠标滚轮滚动距离
	 */
	default int activeMouseListener(Class<? extends IMouseListener> listenerClass, IComponent component,
	                                 float mouseX, float mouseY, int code, int wheel) {
		MIFrame frame = getFrame();
		IntWrapper result = new IntWrapper();
		if (component == null) {
			forEachComponent(it -> {
				if (it.getX() <= mouseX && it.getY() <= mouseY
						&& it.getX() + it.getWidth() >= mouseX && it.getY() + it.getHeight() >= mouseY) {
					result.increment();
					float x = mouseX - it.getX();
					float y = mouseY - it.getY();
					it.activateListener(frame, listenerClass,
							listener -> listener.active(x, y, code, wheel));
					if (it instanceof IComponentManager) {
						result.add(((IComponentManager) it).activeMouseListener(
								listenerClass, null, x, y, code, wheel));
					}
				}
				return true;
			});
		} else {
			forEachComponent(it -> {
				float x = mouseX - it.getX();
				float y = mouseY - it.getY();
				if (it == component) {
					result.increment();
					it.activateListener(frame, listenerClass, listener -> listener.active(x, y, code, wheel));
					return false;
				} else if (it instanceof IComponentManager) {
					result.add(((IComponentManager) it).activeMouseListener(
							listenerClass, component, x, y, code, wheel));
					return result.get() == 0;
				}
				return true;
			});
		}
		return result.get();
	}
	
}