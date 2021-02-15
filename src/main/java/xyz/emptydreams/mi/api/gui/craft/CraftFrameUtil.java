package xyz.emptydreams.mi.api.gui.craft;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.utils.data.enums.OperateResult;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static xyz.emptydreams.mi.api.utils.data.enums.OperateResult.*;

/**
 * 用于放置{@link CraftFrame}的工具方法
 * @author EmptyDreams
 */
final class CraftFrameUtil {
	
	/** 将record写入到NBT中 */
	public static void writeRecord(NBTTagCompound data, Int2IntMap record) {
		NBTTagCompound tag = new NBTTagCompound();
		int index = 0;
		for (Map.Entry<Integer, Integer> entry : record.entrySet()) {
			tag.setInteger(index + "k", entry.getKey());
			tag.setInteger(index + "v", entry.getValue());
			++index;
		}
		data.setTag("_record", tag);
	}
	
	/** 读取record */
	public static Int2IntMap readRecord(NBTTagCompound data) {
		NBTTagCompound tag = data.getCompoundTag("_record");
		int size = tag.getSize();
		Int2IntMap map = new Int2IntArrayMap(size);
		for (int i = 0; i < size; ++i) {
			int key = tag.getInteger(i + "k");
			int value = tag.getInteger(i + "v");
			map.put(key, value);
		}
		return map;
	}
	
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
				case FAIL:
					if (result == null) result = FAIL;
					break;
				case PARTIAL: result = PARTIAL; break;
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
		if (stack.isEmpty()) return SUCCESS;
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
	
}