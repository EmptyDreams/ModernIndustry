package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.inventory.Slot;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.listener.MouseData;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseListener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
	 * <p>触发指定控件的鼠标。
	 * <p>与{@link #activeMouseListener(Class, IComponent, MouseData, boolean, List)}
	 *      不同的是该方法不会生成记录调用结果的列表。
	 * @param listenerClass 触发的事件的class
	 * @param component 要触发事件的控件，留空则尝试触发所有符合条件的控件
	 * @param data 鼠标参数
	 * @param optimize 是否进行优化，为true时若鼠标不在控件上就不会触发事件
	 * @param ignore 忽略列表，该列表中的控件不会触发事件
	 */
	default void activeMouseListenerNoLog(Class<? extends IMouseListener> listenerClass,
	                                      IComponent component, MouseData data,
	                                      boolean optimize, List<IComponent> ignore) {
		MIFrame frame = getFrame();
		float mouseX = data.mouseX, mouseY = data.mouseY;
		if (component == null) {
			forEachComponent(it -> {
				if (optimize && !(it.getX() <= mouseX && it.getY() <= mouseY
						&& it.getX() + it.getWidth() >= mouseX && it.getY() + it.getHeight() >= mouseY)
						&& !ignore.contains(it)) {
					return true;
				}
				float x = mouseX - it.getX();
				float y = mouseY - it.getY();
				it.activateListener(frame, listenerClass,
						listener -> listener.active(data.create(x, y)));
				if (it instanceof IComponentManager) {
					((IComponentManager) it).activeMouseListenerNoLog(
							listenerClass, null, data.create(x, y), optimize, ignore);
				}
				return true;
			});
		} else {
			forEachComponent(it -> {
				if (optimize && !(it.getX() <= mouseX && it.getY() <= mouseY
						&& it.getX() + it.getWidth() >= mouseX && it.getY() + it.getHeight() >= mouseY)
						&& ignore.contains(it)) {
					return true;
				}
				float x = mouseX - it.getX();
				float y = mouseY - it.getY();
				if (it instanceof IComponentManager) {
					((IComponentManager) it).activeMouseListenerNoLog(listenerClass,
							component == it ? null : component, data.create(x, y), optimize, ignore);
				}
				if (it == component) {
					it.activateListener(frame, listenerClass, listener -> listener.active(data.create(x, y)));
				}
				return true;
			});
		}
	}
	
	/**
	 * 触发指定控件的鼠标
	 * @param listenerClass 触发的事件的class
	 * @param component 要触发事件的控件，留空则尝试触发所有符合条件的控件
	 * @param data 鼠标参数
	 * @param optimize 是否进行优化，为true时若鼠标不在控件上就不会触发事件
	 * @param ignore 忽略列表，该列表中的控件不会触发事件但是也会记录到触发事件的列表中
	 * @return 成功触发事件的控件列表
	 */
	default List<IComponent> activeMouseListener(Class<? extends IMouseListener> listenerClass,
	                                             IComponent component, MouseData data,
	                                             boolean optimize, List<IComponent> ignore) {
		MIFrame frame = getFrame();
		List<IComponent> result = new LinkedList<>();
		float mouseX = data.mouseX, mouseY = data.mouseY;
		if (component == null) {
			forEachComponent(it -> {
				if (optimize && !(it.getX() <= mouseX && it.getY() <= mouseY
						&& it.getX() + it.getWidth() >= mouseX && it.getY() + it.getHeight() >= mouseY)) {
					return true;
				}
				result.add(it);
				float x = mouseX - it.getX();
				float y = mouseY - it.getY();
				if (it instanceof IComponentManager) {
					result.addAll(((IComponentManager) it).activeMouseListener(
							listenerClass, null, data.create(x, y), optimize, ignore));
				}
				if (ignore.contains(it)) return true;
				it.activateListener(frame, listenerClass,
						listener -> listener.active(data.create(x, y)));
				return true;
			});
		} else {
			IComponent real = component instanceof IComponentManager ? null : component;
			forEachComponent(it -> {
				if (optimize && !(it.getX() <= mouseX && it.getY() <= mouseY
						&& it.getX() + it.getWidth() >= mouseX && it.getY() + it.getHeight() >= mouseY)) {
					return true;
				}
				float x = mouseX - it.getX();
				float y = mouseY - it.getY();
				if (it instanceof IComponentManager) {
					result.addAll(((IComponentManager) it).activeMouseListener(
							listenerClass, real, data.create(x, y), optimize, ignore));
				}
				if (it == real) {
					result.add(it);
					if (!ignore.contains(it))
						it.activateListener(frame, listenerClass,
								listener -> listener.active(data.create(x, y)));
				}
				return true;
			});
		}
		return result;
	}
	
}