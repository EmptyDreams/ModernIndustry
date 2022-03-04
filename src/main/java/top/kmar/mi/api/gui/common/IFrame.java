package top.kmar.mi.api.gui.common;

import net.minecraft.world.World;

/**
 * 窗口的接口
 * @author EmptyDreams
 */
public interface IFrame {

	/** 获取宽度 */
	int getWidth();
	
	/** 获取高度 */
	int getHeight();
	
	/** 初始化内部数据 */
	void init(World world);

}