package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 只有一个输出产物的无序合成表
 * @author EmptyDreams
 */
public class ULProCraftGuide implements ICraftGuide, Iterable<ItemElement> {

	/** 原料 */
	private final HashSet<ItemElement> elements;
	/** 产物 */
	private ItemElement out;

	/**
	 * 创建一个预测大小的合成表.
	 * 这个数值不会影响运行效果，但是正确的预测大小可以减少内存浪费和提升运行速度
	 */
	public ULProCraftGuide(int size) {
		elements = new HashSet<ItemElement>(size) {
			@Override
			public boolean add(ItemElement o) {
				for (ItemElement element : this) {
					if (element.merge(o)) return true;
				}
				return super.add(o);
			}
		};
	}

	/** 以默认大小（4）创建合成表 */
	public ULProCraftGuide() { this(4); }

	/**
	 * 向列表中添加一个元素
	 * @param element 要添加的元素
	 * @throws NullPointerException 如果 element == null
	 */
	public ULProCraftGuide addElement(ItemElement element) {
		WaitList.checkNull(element, "element");
		elements.add(element);
		return this;
	}

	/**
	 * 向列表中添加一个元素
	 * @param item 要添加的元素
	 * @throws NullPointerException 如果 item == null
	 */
	public ULProCraftGuide addElement(Item item) {
		return addElement(ItemElement.instance(item, 1));
	}

	/**
	 * 设置产物
	 * @param element 产物
	 * @throws NullPointerException 如果 element == null
	 */
	public ULProCraftGuide setOut(ItemElement element) {
		WaitList.checkNull(element, "element");
		out = element;
		return this;
	}

	/**
	 * 设置产物
	 * @param item 产物
	 * @throws NullPointerException 如果 item == null
	 */
	public ULProCraftGuide setOut(Item item) {
		return setOut(ItemElement.instance(item, 1));
	}

	/**
	 * 从合成表中删除产物
	 * @return 是否删除成功
	 */
	public boolean removeOutElement() {
		if (out == null) return false;
		out = null;
		return true;
	}

	/**
	 * 从合成表中删除一个物品
	 * @param item 指定的物品
	 * @return 是否删除成功
	 */
	public boolean removeItem(Item item) {
		ItemElement element;
		Iterator<ItemElement> it = elements.iterator();
		while (it.hasNext()) {
			element = it.next();
			if (element.contrastWith(item)) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * 从合成表中删除一个元素
	 * @param element 指定的元素
	 * @return 是否删除成功
	 */
	public boolean removeElement(ItemElement element) {
		ItemElement itemElement;
		Iterator<ItemElement> it = elements.iterator();
		while (it.hasNext()) {
			itemElement = it.next();
			if (itemElement.contrastWith(element)) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断目标列表是否与合成表相符（比较时忽视产物）
	 * @param craft 目标列表
	 * @return 返回产物，若两者不相符返回null
	 */
	@Override
	public boolean apply(Object craft) {
		if (!(craft instanceof ULProCraftGuide)) return false;
		ULProCraftGuide guide = (ULProCraftGuide) craft;
		if (craft == this) return true;
		return elements.equals(guide.elements);
	}

	/**
	 * 判断目标列表是否与合成表相符
	 * @param stacks 物品列表
	 * @return 返回产物，若两者不相符返回null
	 */
	@Override
	public boolean apply(ItemStack... stacks) {
		if (stacks == null || stacks.length != elements.size()) return false;
		o : for (ItemElement element : elements) {
			for (ItemStack stack : stacks) {
				if (element.contrastWith(ItemElement.instance(stack))) {
					continue o;
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean apply(Iterable<ItemStack> stacks) {
		if (stacks == null) return false;
		o : for (ItemElement element : elements) {
			for (ItemStack stack : stacks) {
				if (element.contrastWith(ItemElement.instance(stack))) {
					continue o;
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean hasItem(Item item) {
		for (ItemElement element : this) {
			if (element.contrastWith(item)) return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public List<ItemElement> getOuts() {
		List<ItemElement> list = new ArrayList<>(1);
		list.add(out);
		return list;
	}

	@Override
	public ItemElement getFirstOut() {
		return out;
	}

	/** 将合成表转换为ItemStack[] */
	public ItemStack[] toItemStack() {
		ItemStack[] stacks = new ItemStack[elements.size()];
		int i = 0;
		for (ItemElement element : elements) {
			stacks[i] = element.getStack();
			++i;
		}
		return stacks;
	}

	@Override
	public Iterator<ItemElement> iterator() {
		return elements.iterator();
	}

	@Override
	public String toString() {
		return "ULProCraftGuide{" +
				"elements=" + elements +
				", out=" + out +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ULProCraftGuide that = (ULProCraftGuide) o;

		if (!elements.equals(that.elements)) return false;
		return out.equals(that.out);
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

}
