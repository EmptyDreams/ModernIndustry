package xyz.emptydreams.mi.api.gui.component.interfaces;

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
	
	/** 获取控件组在GUI中的X坐标 */
	int getX();
	
	/** 获取控件组在GUI中的Y坐标 */
	int getY();
	
}