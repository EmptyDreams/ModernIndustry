package xyz.emptydreams.mi.api.craftguide.multi;

import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public class UnorderlyShape implements IShape<ItemSet, ItemSet> {
	
	private final ItemSet raw;
	private final ItemSet production;
	
	public UnorderlyShape(ItemSet input, ItemSet output) {
		raw = input.offset();
		production = input.offset();
	}
	
	@Nonnull
	@Override
	public ItemSet getRawSol() {
		return raw.copy();
	}
	
	@Override
	public ItemSet getProduction() {
		return production.copy();
	}
	
	@Override
	public boolean apply(ItemSet that) {
		return raw.apply(that);
	}
	
	@Override
	public boolean hasElement(ItemElement element) {
		return raw.hasElement(element);
	}
	
	@Override
	public boolean hasItem(ItemStack stack) {
		return raw.hasItem(stack);
	}
	
}
