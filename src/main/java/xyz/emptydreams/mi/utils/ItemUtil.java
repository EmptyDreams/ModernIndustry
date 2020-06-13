package xyz.emptydreams.mi.utils;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.net.WaitList;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public final class ItemUtil {
	
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
			for (ItemStack it : list) {
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
