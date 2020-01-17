package minedreams.mi.api.craftguide;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public final class CraftGuideItems {

	private final ArrayList<Item> items = new ArrayList<>();
	private final ArrayList<Integer> ints = new ArrayList<>();
	
	public ItemStack get(int index) {
		return new ItemStack(items.get(index), ints.get(index));
	}
	
	/**
	 * 查找列表中是否含有某个物品
	 */
	public boolean hasItem(Item item) {
		return items.contains(item);
	}
	
	/**
	 * 添加一个物品到列表
	 */
	public CraftGuideItems add(Item item, int number) {
		int index = items.indexOf(item);
		if (index == -1) {
			ints.add(number);
			items.add(item);
		} else {
			ints.set(index, ints.get(index) + number);
		}
		return this;
	}
	
	public void add(CraftGuideItems cgi) {
		for (int i = 0; i < cgi.ints.size(); ++i) {
			items.add(cgi.items.get(i));
			ints.add(cgi.ints.get(i));
		}
	}
	
	public void forEach(Consumer<? super Item> action) {
		items.forEach(action);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(items.toArray());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CraftGuideItems) {
			CraftGuideItems cgi = (CraftGuideItems) obj;
			if (items.size() != cgi.items.size()) return false;
			for (Item item : cgi.items) {
				if (!items.contains(item)) return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return items.toString();
	}
	
}
