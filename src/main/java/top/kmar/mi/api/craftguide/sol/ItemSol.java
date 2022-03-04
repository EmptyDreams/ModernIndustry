package top.kmar.mi.api.craftguide.sol;

import net.minecraft.item.ItemStack;
import top.kmar.mi.api.craftguide.ItemElement;

/**
 * 存储物品序列（可以无序也可以有序）
 * @author EmptyDreams
 */
public interface ItemSol {
	
	/** 判断列表中是否包含指定元素 */
	boolean hasElement(ItemElement element);
	
	/** 判断列表中是否包含指定物品，比较时忽略物品数量 */
	boolean hasItem(ItemStack item);
	
	/**
	 * 判断当前序列与指定序列是否相等.<br>
	 * 方法内不会自动对当前对象以及sol进行{@link #offset()}操作，
	 * 没有进行{@link #offset()}操作时该方法的判断结果可能不准确
	 */
	boolean apply(ItemSol sol);

	/** 判断物品序列是否为空 */
	boolean isEmpty();
	
	/**
	 * 尝试将该sol中的内容按顺序填充到sol中
	 * @param sol 被填充的sol，该sol物品序列必须为空
	 * @return 是否填充成功
	 */
	boolean fill(ItemList sol);
	
	/** 获取列表大小 */
	int size();
	
	/** 拷贝内容 */
	ItemSol copy();
	
	/**
	 * 去除列表所有不必要的区域，然后返回修正后的列表
	 * @return 该返回值一定返回新的列表
	 */
	ItemSol offset();
	
}