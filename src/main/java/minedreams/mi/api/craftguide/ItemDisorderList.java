package minedreams.mi.api.craftguide;

import java.util.HashSet;
import java.util.Set;

import minedreams.mi.api.net.WaitList;
import minedreams.mi.tools.MISysInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * 通过物品对象工作的无序合成表，不支持矿词，不支持元素重复
 * @author EmptyDreams
 * @version V1.0
 */
public class ItemDisorderList extends GuideList<Item, ItemStack> {
	
	private Set<Item> items;
	
	private int amount;
	private Item out;
	
	/**
	 * 创建指定大小的合成表
	 * @param size 大小
	 * @throws IllegalArgumentException 如果size <= 0 | amount <= 0
	 * @throws NullPointerException 如果out == null
	 */
	public ItemDisorderList(int size, Item out, int amount) {
		if (size <= 0) throw new IllegalArgumentException("size[" + size + "] <= 0");
		if (amount <= 0) throw new IllegalArgumentException("amount[" + amount + "] <= 0");
		WaitList.checkNull(out, "out");
		
		items = new HashSet<>(size);
		this.amount = amount;
		this.out = out;
	}
	
	/**
	 * 创建默认大小的合成表，大小为(3, 1)
	 * @throws IllegalArgumentException 如果amount <= 0
	 * @throws NullPointerException 如果out == null
	 */
	public ItemDisorderList(Item out, int amount) {
		this(3, out, amount);
	}
	
	@Override
	public boolean contains(Item item) {
		return items.contains(item);
	}
	
	@Override
	public boolean add(int x, int y, Item item) {
		return items.add(item);
	}
	
	@Deprecated
	@Override
	public Item remove(int x, int y) {
		return null;
	}
	
	@Override
	public boolean remove(Item item, int max) {
		if (max < 0) throw new IllegalArgumentException("max[" + max + "] < 0");
		if (max == 0) return false;
		if (max > 1) MISysInfo.err("max > 1 是无效的！元素列表中不可能含有重复元素！");
		return items.remove(item);
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
		if (!(o instanceof ItemDisorderList)) return false;
		ItemDisorderList list = (ItemDisorderList) o;
		return list.items.equals(items);
	}
	
}
