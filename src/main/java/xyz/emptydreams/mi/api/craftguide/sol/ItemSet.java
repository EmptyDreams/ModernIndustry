package xyz.emptydreams.mi.api.craftguide.sol;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.container.IntWrapper;
import xyz.emptydreams.mi.api.utils.data.math.Point2D;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.ObjIntConsumer;

/**
 * 无序物品列表
 * @author EmptyDreams
 */
public class ItemSet implements ItemSol, Iterable<ItemElement> {
	
	private final Set<ItemElement> elements;
	
	public ItemSet() {
		elements = new HashSet<>();
	}
	
	public ItemSet(ItemSet set) {
		elements = new HashSet<>(set.elements);
	}
	
	public ItemSet(ItemElement... set) {
		elements = Sets.newHashSet(set);
	}
	
	public ItemSet(Set<ItemElement> set) {
		elements = set;
	}
	
	/** 想列表中添加一个元素 */
	public void add(ItemElement element) {
		ItemElement real = element;
		for (Iterator<ItemElement> iterator = elements.iterator(); iterator.hasNext(); ) {
			ItemElement it = iterator.next();
			if (it.contain(element)) {
				real = ItemElement.instance(element.getItem(),
						element.getAmount() + it.getAmount(), it.getMeta());
				iterator.remove();
				break;
			}
		}
		elements.add(real);
	}
	
	@Override
	public boolean hasElement(ItemElement element) {
		return elements.contains(element);
	}
	
	@Override
	public boolean hasItem(ItemStack stack) {
		for (ItemElement element : elements) {
			if (element.contain(stack))return true;
		}
		return false;
	}
	
	@Override
	public boolean apply(ItemSol sol) {
		if (!(sol instanceof ItemSet)) return false;
		ItemSet that = (ItemSet) sol;
		if (elements.size() != that.elements.size()) return false;
		o : for (ItemElement element : elements) {
			for (ItemElement inner : that.elements) {
				if (inner.contain(element)) continue o;
			}
			return false;
		}
		return true;
	}
	
	@Override
	public int size() {
		return elements.size();
	}
	
	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}
	
	@Override
	public boolean fill(ItemList sol) {
		int width = sol.getWidth();
		if (width * sol.getHeight() > size()) return false;
		forEachIndex((element, index) -> {
			Point2D point = MathUtil.indexToMatrix(index, width);
			sol.set(point.getX(), point.getY(), element);
		});
		return true;
	}
	
	@Override
	public ItemSet copy() {
		return new ItemSet(this);
	}
	
	@Override
	public ItemSet offset() {
		Set<ItemElement> elements = new HashSet<>(this.elements);
		elements.removeIf(ItemElement::isEmpty);
		return new ItemSet(elements);
	}
	
	@Override
	public Iterator<ItemElement> iterator() {
		return elements.iterator();
	}
	
	/** 根据下标遍历所有元素（无序） */
	public void forEachIndex(ObjIntConsumer<ItemElement> consumer) {
		IntWrapper index = new IntWrapper();
		forEach(it -> consumer.accept(it, index.getAndIncrement()));
	}
	
	/** 解析JSON */
	public static ItemSet parse(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		String input = json.get("pattern").getAsString();
		ItemSet set = new ItemSet();
		for (int i = 0; i < input.length(); ++i) {
			set.add(keyMap.get(input.charAt(i)));
		}
		return set;
	}
	
}