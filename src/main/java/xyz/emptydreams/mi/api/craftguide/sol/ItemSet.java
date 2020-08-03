package xyz.emptydreams.mi.api.craftguide.sol;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.ItemElement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
			if (it.contrastWith(element)) {
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
			if (element.contrastWith(stack))return true;
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
				if (inner.contrastWith(element)) continue o;
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
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
}
