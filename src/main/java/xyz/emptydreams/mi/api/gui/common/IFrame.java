package xyz.emptydreams.mi.api.gui.common;

import net.minecraft.entity.player.EntityPlayer;
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
	 * 添加一个组件
	 * @param component 组件
	 * @param player 打开GUI的玩家的对象，若调用端在客户端则player允许为null
	 */
	void add(IComponent component, EntityPlayer player);
	
	/** 初始化内部数据 */
	void init(World world);

}
