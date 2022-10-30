package top.kmar.mi.api.utils;

import net.minecraft.item.ItemStack;

/**
 * 关于物品的一些常用操作的封装
 * @author EmptyDreams
 */
public final class ItemUtil {
	
	/**
	 * 将指定的Stack传入到指定的Stack中.<br>
	 * 若stack可以完全容纳下input，则正常计算，否则放弃计算，
	 * 即若{@code input.getCount() + stack.getCount() > stack.getMaxStackSize()}则该方法不会有任何作用。
	 * @param stack 接受传入的Stack
	 * @param input 需要传入的Stack
	 * @param modifyInput 是否修改需要传入的Stack
	 * @return 若方法对stack或input产生了修改返回true，否则返回false
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static boolean putItemTo(ItemStack stack, ItemStack input, boolean modifyInput) {
		if (stack.getItem() == input.getItem() && stack.getMetadata() == input.getMetadata()) {
			int value = input.getCount() + stack.getCount();
			if (value > stack.getMaxStackSize()) return false;
			stack.setCount(value);
			if (modifyInput) input.setCount(0);
			return true;
		}
		return false;
	}
	
}