package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 所有合成表的接口，其中定义了一些规范
 * @author EmptyDreams
 */
public interface ICraftGuide {
	
	/**
	 * 判断目标列表是否与合成表相符（比较时忽视产物）
	 * @param craft 目标列表，若类型不符则返回null
	 */
	boolean apply(Object craft);
	
	/**
	 * 判断目标列表是否与合成表相符
	 * @param stacks 物品列表
	 * @throws NullPointerException 如果 stacks == null
	 */
	boolean apply(ItemStack... stacks);
	
	/**
	 * 判断目标列表是否与合成表相符
	 * @param stacks 物品列表
	 * @throws NullPointerException 如果 stacks == null
	 */
	boolean apply(Iterable<ItemStack> stacks);
	
	/**
	 * 判断当前合成表中是否包含指定物品
	 * @param item 指定物品
	 */
	boolean hasItem(Item item);
	
	/**
	 * 获取产物
	 */
	@Nonnull
	List<ItemElement> getOuts();

	/**
	 * 获取列表中第一个产物
	 */
	ItemElement getFirstOut();
	
}
