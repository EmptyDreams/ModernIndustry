package xyz.emptydreams.mi.api.gui.craft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.utils.data.enums.OperateResult;
import xyz.emptydreams.mi.api.utils.data.math.Mar2D;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.*;

/**
 * 用于放置{@link CraftFrame}的工具方法
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
final class CraftFrameUtil {
	
	/**
	 * <p>从输入中去除列表中的物品.
	 * <p><b>就算没有完全成功也会修改输入和列表中的数据</b>
	 * @param input 输入
	 * @param list 列表
	 * @param record 运算记录，留空表明不保留记录
	 * @return 运算结果
	 */
	@Nonnull
	public static OperateResult removeItemStack(List<ItemStack> input, ItemList list, Record record) {
		OperateResult result = null;
		for (ItemList.Node node : list) {
			ItemStack stack = node.getElement().getStack();
			int remove = removeItemStack(input, stack);
			if (record != null) record.add(node.getX(), node.getY(), remove);
			if (remove == 0) {
				if (result == null) result = FAIL;
				else result = PARTIAL;
			} else if (remove == node.getElement().getAmount()) {
				if (result == null) result = SUCCESS;
			} else {
				result = PARTIAL;
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
	 * @param record 运算记录，留空表明不保留记录
	 * @return 移除了几个物品
	 */
	@Nonnull
	public static int removeItemStack(List<ItemStack> input, ItemStack stack) {
		if (stack.isEmpty()) return 0;
		int result = 0;
		for (ItemStack itemStack : input) {
			int count = stack.getCount();
			result += removeItemStack(itemStack, stack);
			if (result == count) return result;
		}
		return result;
	}
	
	/**
	 * <p>从输入中去除stack.
	 * <p><b>就算没有完全成功也会修改输入和stack中的数据</b>
	 * @param input 输入
	 * @param stack 要移除的物品
	 * @return 移除了几个物品
	 */
	@Nonnull
	public static int removeItemStack(ItemStack input, ItemStack stack) {
		if (input.isEmpty()) return 0;
		if (input.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(input, stack)) {
			int count = stack.getCount();
			if (input.getCount() >= count) {
				input.shrink(count);
				stack.setCount(0);
				return count;
			} else {
				int result = input.getCount();
				stack.shrink(result);
				input.setCount(0);
				return result;
			}
		}
		return 0;
	}
	
	/**
	 * 用于记录操作历史的类
	 */
	static final class Record implements Iterable<Mar2D.Node> {
	
		private final Mar2D data;
		
		public Record(int width, int height) {
			data = new Mar2D(width, height);
		}
	
		public void add(int x, int y, int k) {
			data.set(x, y, data.get(x, y) + k);
		}
		
		public int get(int x, int y) {
			return data.get(x, y);
		}
		
		@Override
		public Iterator<Mar2D.Node> iterator() {
			return data.iterator();
		}
	}
	
}