package xyz.emptydreams.mi.api.craftguide;

import java.util.ArrayList;
import java.util.List;

import xyz.emptydreams.mi.api.net.WaitList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
public class DictOrderlyList extends GuideList<String, ItemStack> {
	
	private List<String>[] items;
	
	private int amount;
	private Item out;
	
	/**
	 * 创建指定大小的合成表
	 * @param width 宽度
	 * @param height 高度
	 * @throws IllegalArgumentException 如果width<=0 | height<=0 | amount <= 0
	 * @throws NullPointerException 如果out == null
	 */
	@SuppressWarnings("unchecked")
	public DictOrderlyList(int width, int height, Item out, int amount) {
		if (height <= 0) throw new IllegalArgumentException("height[" + height + "] <= 0");
		if (width <= 0) throw new IllegalArgumentException("width[" + width + "] <= 0");
		if (amount <= 0) throw new IllegalArgumentException("amount[" + amount + "] <= 0");
		WaitList.checkNull(out, "out");
		
		items = new List[height];
		for (int i = 0; i < height; ++i) {
			items[i] = new ArrayList<>(width);
		}
		this.amount = amount;
		this.out = out;
	}
	
	/**
	 * 创建默认大小的合成表，大小为(3, 1)
	 * @throws IllegalArgumentException 如果amount <= 0
	 * @throws NullPointerException 如果out == null
	 */
	public DictOrderlyList(Item out, int amount) {
		this(3, 1, out, amount);
	}
	
	@Override
	public boolean contains(String item) {
		for (List<String> list : items) {
			if (list.contains(item)) return true;
		}
		return false;
	}
	
	@Override
	public boolean add(int x, int y, String item) {
		if (y >= items.length) {
			//noinspection unchecked
			List<String>[] items = new List[y + 1];
			System.arraycopy(this.items, 0, items, 0, this.items.length);
			for (int i = this.items.length; i < items.length; ++i)
				items[i] = new ArrayList<>();
			this.items = items;
		}
		items[y].add(x, item);
		return true;
	}
	
	@Override
	public String remove(int x, int y) {
		return items[y].remove(x);
	}
	
	@Override
	public boolean remove(String item, int max) {
		if (max < 0) throw new IllegalArgumentException("max[" + max + "] < 0");
		if (max == 0) return false;
		int now = 0;
		o : for (List<String> list : items) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).equals(item)) {
					list.remove(i);
					if (++now == max) break o;
					--i;
				}
			}
		}
		return now != 0;
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
		return true;
	}
	
	/**
	 * 获取合成表宽度.<br>
	 * 注意该方法返回的宽度是真实宽度，不是用户指定的宽度
	 */
	public int getWidth() {
		int max = 0;
		for (List<String> item : items) {
			max = Math.max(max, item.size());
		}
		return max;
	}
	
	/**
	 * 获取合成表高度<br>
	 * 注意该方法返回的高度是真实高度，不是用户指定的高度
	 */
	public int getHeight() {
		return items.length;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(width, height)=(" + getWidth() + ", " + getHeight() + ")";
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < items.length; i++) {
			hash += 1 + items[i].size() << i;
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DictOrderlyList)) return false;
		DictOrderlyList list = (DictOrderlyList) o;
		if (getHeight() != list.getHeight()) return false;
		if (getWidth() != list.getWidth()) return false;
		for (int i = 0; i < items.length; i++) {
			if (!items[i].equals(list.items[i])) return false;
		}
		return true;
	}
	
}
