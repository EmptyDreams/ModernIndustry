package top.kmar.mi.api.utils;

import net.minecraft.item.ItemStack;
import top.kmar.mi.api.utils.data.enums.OperateResult;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static top.kmar.mi.api.utils.data.enums.OperateResult.*;

/**
 * 关于物品的一些常用操作的封装
 * @author EmptyDreams
 */
public final class ItemUtil {
	
	/**
	 * 将指定的ItemStack合并到列表中，合并时会更改传入的stack
	 * @param stack 要并入的stack
	 * @param slots 被并入的stack列表
	 * @param startIndex 列表起始下标
	 * @param endIndex 列表终止下标
	 * @param reverseDirection 是否反向查找
	 * @return 运算结果
	 */
	@Nonnull
	public static OperateResult mergeItemStack(ItemStack stack, List<ItemStack> slots,
	                                           int startIndex, int endIndex, boolean reverseDirection) {
		int stackOldSize = stack.getCount();
		int i = startIndex;
		if (reverseDirection) i = endIndex - 1;
		while (!stack.isEmpty()) {
			if (reverseDirection) {
				if (i < startIndex) break;
			} else if (i >= endIndex) {
				break;
			}
			ItemStack itemstack = slots.get(i);
			if (itemstack.isEmpty()) {
				slots.set(i, stack.copy());
				return SUCCESS;
			} else if (itemstack.isItemEqual(stack)
					&& ItemStack.areItemStackTagsEqual(stack, itemstack)) {
				int j = itemstack.getCount() + stack.getCount();
				int maxSize = Math.min(itemstack.getMaxStackSize(), stack.getMaxStackSize());
				if (j <= maxSize) {
					stack.setCount(0);
					itemstack.setCount(j);
				} else if (itemstack.getCount() < maxSize) {
					stack.shrink(maxSize - itemstack.getCount());
					itemstack.setCount(maxSize);
				}
			}
			if (reverseDirection) --i;
			else ++i;
		}
		if (stack.isEmpty()) return SUCCESS;
		if (stack.getCount() == stackOldSize) return FAIL;
		return PARTIAL;
	}
	
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
	 * @return 合并失败时返回空的List
	 * @throws NullPointerException 如果 stacks == null
	 */
	@Nonnull
	public static List<ItemStack> merge(ItemStack... stacks) {
		StringUtil.checkNull(stacks, "stacks");
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
	 * @return 合并失败时返回空的List
	 * @throws NullPointerException 如果 accept == null || inputs == null
	 */
	@SuppressWarnings("unused")
	@SafeVarargs
	public static<T> List<ItemStack> merge(Function<T, ItemStack> accept, T... inputs) {
		StringUtil.checkNull(accept, "accept");
		StringUtil.checkNull(inputs, "inputs");
		ItemStack[] stacks = new ItemStack[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			stacks[i] = accept.apply(inputs[i]);
		}
		return merge(stacks);
	}
	
}