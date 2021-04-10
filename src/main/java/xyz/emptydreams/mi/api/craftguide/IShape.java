package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;

import javax.annotation.Nonnull;

/**
 * 合成表接口
 * @author EmptyDreams
 */
public interface IShape<T extends ItemSol, R> {
	
	/**
	 * 获取原料列表
	 * @return 返回值经过保护性拷贝
	 */
	T getInput();
	
	/**
	 * 获取产物列表
	 * @return 返回值是经过保护性拷贝的
	 */
	R getOutput();
	
	/**
	 * 判断指定输入与原料列表是否相符
	 * @param that 指定输入
	 */
	boolean apply(T that);
	
	/**
	 * 判断原料中是否包含指定元素
	 * @param element 元素
	 */
	boolean haveElement(ItemElement element);
	
	/**
	 * 判断原料中是否包含指定物品，比较时忽略物品数量
	 * @param stack 物品
	 */
	boolean haveItem(ItemStack stack);
	
	/** 获取原料列表的Class */
	@Nonnull
	Class<T> getInputClass();
	
	/** 获取的Class */
	@SuppressWarnings("unused")
	@Nonnull
	Class<R> getOutputClass();
	
	/** 获取合成表产物的主要名称 */
	@SideOnly(Side.CLIENT)
	String getMainlyName();
	
}