package xyz.emptydreams.mi.api.gui.common;

import net.minecraft.world.World;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;

/**
 * 窗口的接口
 * @author EmptyDreams
 */
public interface IFrame {

	/** 获取宽度 */
	int getWidth();
	
	/** 获取高度 */
	int getHeight();
	
	/**
	 * 添加一个控件
	 * @param component 要添加的控件
	 * @throws NullPointerException 如果component == null
	 */
	void add(IComponent component);
	
	/** 初始化内部数据 */
	void init(World world);

}