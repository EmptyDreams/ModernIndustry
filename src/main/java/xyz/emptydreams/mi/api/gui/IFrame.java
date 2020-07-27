package xyz.emptydreams.mi.api.gui;

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
	/** 获取标题 */
	String getTitle();

	/** 添加一个组件 */
	void add(IComponent component, EntityPlayer player);
	/** 初始化内部数据 */
	void init(World world);
	/** 设置标题 */
	void setTitle(String text);
	/**
	 * 设置标题显示模式，当标题位置不为默认时该设置无效
	 * @param model 指定的模式
	 * @throws NullPointerException 如果model == null
	 */
	void setTitleModel(TitleModelEnum model);

}
