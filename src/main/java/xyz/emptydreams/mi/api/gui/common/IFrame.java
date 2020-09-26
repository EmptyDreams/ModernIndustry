package xyz.emptydreams.mi.api.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.gui.component.IComponent;

/**
 * 窗口的接口
 * @author EmptyDreams
 */
public interface IFrame {

	/** 获取宽度 */
	int getWidth();
	/** 获取高度 */
	int getHeight();

	/** 添加一个组件 */
	void add(IComponent component, EntityPlayer player);
	/** 初始化内部数据 */
	void init(World world);

}
