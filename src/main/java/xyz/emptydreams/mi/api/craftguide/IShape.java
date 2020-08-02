package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;

/**
 * @author EmptyDreams
 */
public interface IShape<T extends ItemSol, R> {
	
	/**
	 * 获取产物列表
	 * @return 返回值经过保护性拷贝
	 */
	T getRawSol();
	
	/**
	 * 获取产物列表
	 * @return 返回值是经过保护性拷贝的
	 */
	R getProduction();
	
	/**
	 * 判断指定输入与原料列表是否相符
	 * @param that 指定输入
	 */
	boolean apply(T that);
	
	/**
	 * 判断原料中是否包含指定元素
	 * @param element 元素
	 */
	boolean hasElement(ItemElement element);
	
	/**
	 * 判断原料中是否包含指定物品，比较时忽略物品数量
	 * @param stack 物品
	 */
	boolean hasItem(ItemStack stack);
	
}
