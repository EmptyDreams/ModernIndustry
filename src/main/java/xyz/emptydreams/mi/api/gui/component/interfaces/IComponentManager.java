package xyz.emptydreams.mi.api.gui.component.interfaces;

import net.minecraft.inventory.Slot;
import xyz.emptydreams.mi.api.gui.common.MIFrame;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.function.Consumer;

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
	
	/** 遍历所有组件 */
	void forEachComponent(Consumer<? super IComponent> consumer);
	
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
	
}