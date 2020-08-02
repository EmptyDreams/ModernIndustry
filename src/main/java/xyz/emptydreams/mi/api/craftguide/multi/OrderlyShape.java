package xyz.emptydreams.mi.api.craftguide.multi;

import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public class OrderlyShape implements IShape<ItemList, ItemSet> {
	
	private final ItemList list;
	private final ItemSet production;
	
	public OrderlyShape(ItemList input, ItemSet output) {
		list = input.offset();
		production = output.offset();
	}
	
	@Nonnull
	@Override
	public ItemList getRawSol() {
		return list.copy();
	}
	
	@Override
	public ItemSet getProduction() {
		return production.copy();
	}
	
	@Override
	public boolean apply(ItemList that) {
		return list.apply(that);
	}
	
	@Override
	public boolean hasElement(ItemElement element) {
		return list.hasElement(element);
	}
	
	@Override
	public boolean hasItem(ItemStack stack) {
		return list.hasItem(stack);
	}
	
}
