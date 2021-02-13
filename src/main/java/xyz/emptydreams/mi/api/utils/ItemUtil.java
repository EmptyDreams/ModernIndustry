package xyz.emptydreams.mi.api.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.utils.data.enums.OperateResult;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static xyz.emptydreams.mi.api.utils.StringUtil.checkNull;
import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.*;
import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.FAIL;

/**
 * 关于物品的一些常用操作的封装
 * @author EmptyDreams
 */
public final class ItemUtil {
	
	/**
	 * <p>从输入中去除列表中的物品.
	 * <p><b>就算没有完全成功也会修改输入和列表中的数据</b>
	 * @param input 输入
	 * @param list 列表
	 * @return 运算结果
	 */
	@Nonnull
	public static OperateResult removeItemStack(List<ItemStack> input, ItemList list) {
		OperateResult result = null;
		for (ItemList.Node node : list) {
			OperateResult operate = removeItemStack(input, node.getElement().getStack());
			switch (operate) {
				case FAIL: result = FAIL; break;
				case PARTIAL:
					if (result != FAIL) result = PARTIAL;
					break;
				case SUCCESS:
					if (result == null) result = SUCCESS;
					break;
				default: throw new AssertionError("出现了意料之外的值");
			}
		}
		assert result != null : new AssertionError("返回值为空");
		return result;
	}
	
	/**
	 * <p>从输入中去除stack.
	 * <p><b>就算没有完全成功也会修改输入和stack中的数据</b>
	 * @param input 输入
	 * @param stack 要移除的物品
	 * @return 运算结果
	 */
	@Nonnull
	public static OperateResult removeItemStack(List<ItemStack> input, ItemStack stack) {
		boolean isFail = true;
		for (ItemStack itemStack : input) {
			OperateResult result = removeItemStack(itemStack, stack);
			if (result == SUCCESS) return SUCCESS;
			if (result == PARTIAL) isFail = false;
		}
		return isFail ? FAIL : PARTIAL;
	}
	
	/**
	 * <p>从输入中去除stack.
	 * <p><b>就算没有完全成功也会修改输入和stack中的数据</b>
	 * @param input 输入
	 * @param stack 要移除的物品
	 * @return 运算结果
	 */
	@Nonnull
	public static OperateResult removeItemStack(ItemStack input, ItemStack stack) {
		if (input.isEmpty()) return FAIL;
		if (input.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(input, stack)) {
			if (input.getCount() >= stack.getCount()) {
				input.shrink(stack.getCount());
				stack.setCount(0);
				return SUCCESS;
			} else {
				stack.shrink(input.getCount());
				input.setCount(0);
				return PARTIAL;
			}
		}
		return FAIL;
	}
	
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
			if (itemstack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
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
	 * 创建一个新的{@link ItemStack}对象，用于在服务端替代{@link Item#getDefaultInstance()}
	 * @param item 物品
	 */
	public static ItemStack newStack(Item item) {
		return new ItemStack(item);
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
		checkNull(stacks, "stacks");
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
		checkNull(accept, "accept");
		checkNull(inputs, "inputs");
		ItemStack[] stacks = new ItemStack[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			stacks[i] = accept.apply(inputs[i]);
		}
		return merge(stacks);
	}
	
}