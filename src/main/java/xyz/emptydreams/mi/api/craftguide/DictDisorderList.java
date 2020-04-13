package xyz.emptydreams.mi.api.craftguide;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.utils.MISysInfo;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class DictDisorderList extends GuideList<ItemStack, ItemStack> {
	
	public static DictDisorderList create(Item out, int amount, ItemStack... items) {
		DictDisorderList list = new DictDisorderList(items.length, out, amount);
		for (ItemStack item : items) {
			list.add(0, 0, item);
		}
		return list;
	}
	
	private final Set<ItemStack> items;
	
	private int amount;
	private Item out;
	
	/**
	 * 创建指定大小的合成表
	 * @param size 大小
	 * @throws IllegalArgumentException 如果size <= 0 | amount < 0
	 * @throws NullPointerException 如果out == null
	 */
	public DictDisorderList(int size, Item out, int amount) {
		if (size <= 0) throw new IllegalArgumentException("size[" + size + "] <= 0");
		if (amount < 0) throw new IllegalArgumentException("amount[" + amount + "] < 0");
		
		items = new HashSet<ItemStack>(size) {
			@Override
			public boolean equals(Object o) {
				if (!(o instanceof HashSet)) return false;
				//noinspection unchecked
				HashSet<ItemStack> stacks = (HashSet<ItemStack>) o;
				if (stacks.size() != size()) return false;
				for (ItemStack stack : this) {
					int[] ids = OreDictionary.getOreIDs(stack);
					int amount = 0;
					for (ItemStack item : stacks) {
						if (item.getItem() == stack.getItem()) {
							amount += item.getCount();
						} else if (!isEquals(item, ids)) {
							return false;
						}
					}
					if (amount != stack.getCount()) return false;
				}
				return true;
			}
		};
		this.amount = amount;
		this.out = out;
	}
	
	/**
	 * 创建默认大小的合成表，大小为(3, 1)
	 * @throws IllegalArgumentException 如果amount <= 0
	 * @throws NullPointerException 如果out == null
	 */
	@SuppressWarnings("unused")
	public DictDisorderList(Item out, int amount) {
		this(3, out, amount);
	}
	
	@Override
	public boolean contains(ItemStack item) {
		int[] ids = OreDictionary.getOreIDs(item);
		for (ItemStack stack : items) {
			//if (stack.getCount() != item.getCount()) continue;
			if (stack.getItem() == item.getItem()) return true;
			if (stack.equals(item)) return true;
			if (isEquals(stack, ids)) return true;
		}
		return false;
	}
	
	@Override
	public boolean add(int x, int y, ItemStack item) {
		return add(item);
	}
	
	public boolean add(ItemStack item) {
		if (item.isEmpty() || item.getItem() == Items.AIR) return false;
		int[] ids = OreDictionary.getOreIDs(item);
		for (ItemStack stack : items) {
			if (stack == item || stack.getItem() == item.getItem() ||
					    isEquals(stack, ids)) {
				stack.setCount(stack.getCount() + item.getCount());
				return true;
			}
		}
		items.add(item);
		return true;
	}
	
	private boolean isEquals(ItemStack stack, int[] ids) {
		int[] id = OreDictionary.getOreIDs(stack);
		for (int i : ids) {
			for (int i1 : id) {
				if (i == i1) return true;
			}
		}
		return false;
	}
	
	@Deprecated
	@Override
	public ItemStack remove(int x, int y) { return null; }
	
	@Override
	public boolean remove(ItemStack item, int max) {
		if (max < 0) throw new IllegalArgumentException("max[" + max + "] < 0");
		if (max == 0) return false;
		if (max > 1) MISysInfo.err("max > 1 是无效的！元素列表中不可能含有重复元素！");
		
		int[] ids = OreDictionary.getOreIDs(item);
		Iterator<ItemStack> it = items.iterator();
		ItemStack stack;
		while (it.hasNext()) {
			stack = it.next();
			if (stack.getCount() != item.getCount()) continue;
			if (stack.equals(item)) {
				it.remove();
				return true;
			}
			if (isEquals(stack, ids)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ItemStack get() {
		return new ItemStack(out, amount);
	}
	
	@Override
	public boolean set(ItemStack itemStack) {
		WaitList.checkNull(itemStack, "itemStack");
		amount = itemStack.getCount();
		out = itemStack.getItem();
		return true;
	}
	
	@Override
	public boolean isOrderly() {
		return false;
	}
	
	public int size() { return items.size(); }
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "size=" + size();
	}
	
	@Override
	public int hashCode() {
		return items.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DictDisorderList)) return false;
		DictDisorderList list = (DictDisorderList) o;
		return list.items.equals(items);
	}
	
}
