package xyz.emptydreams.mi.api.utils;

import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

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

	/**
	 * 检查列表中是否含有空的{@link ItemStack}
	 * @param stacks 指定列表
	 * @see ItemStack#isEmpty()
	 */
	public static boolean hasEmpty(ItemStack... stacks) {
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) return true;
		}
		return false;
	}
	
	/**
	 * 合并物品列表，该方法不会修改列表中ItemStack的内容
	 * @return 合并失败时返回Optional.empty()
	 * @throws NullPointerException 如果 stacks == null
	 */
	@Nonnull
	public static List<ItemStack> merge(ItemStack... stacks) {
		WaitList.checkNull(stacks, "stacks");
		List<ItemStack> list = new LinkedList<>();
		o : for (ItemStack stack : stacks) {
			if (stack == null) continue;
			for (ItemStack it : list) {
				if (it == null) continue;
				if (it.getMetadata() == stack.getMetadata() &&
					it.getItem() == stack.getItem() &&
						    it.getItemDamage() == stack.getItemDamage()) {
					it.grow(stack.getCount());
					continue o;
				}
			}
			list.add(stack.copy());
		}
		return list;
	}
	
	/**
	 * 合并物品列表，该方法不会修改列表中ItemStack的内容
	 * <p><pre>例如：{@code
	 * ItemStackHandler i0 = ...;
	 * ItemStackHandler i1 = ...;
	 * ......
	 * ItemUtil.merge(it -> it.getStack(), i0, i1, ...);
	 * }</pre></p>
	 * @param accept 用于将输入的参数转换为{@link ItemStack}对象
	 * @param inputs 输入列表
	 * @param <T> 输入的参数类型
	 * @return 合并失败时返回Optional.empty()
	 * @throws NullPointerException 如果 accept == null || inputs == null
	 */
	@SuppressWarnings("unused")
	@SafeVarargs
	public static<T> List<ItemStack> merge(Function<T, ItemStack> accept, T... inputs) {
		WaitList.checkNull(accept, "accept");
		WaitList.checkNull(inputs, "inputs");
		ItemStack[] stacks = new ItemStack[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			stacks[i] = accept.apply(inputs[i]);
		}
		return merge(stacks);
	}
	
}
