package xyz.emptydreams.mi.api.craftguide.only;

import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public class UnorderlyShapeOnly implements IShape<ItemSet, ItemElement> {
	
	private final ItemSet set;
	private final ItemElement production;
	
	public UnorderlyShapeOnly(ItemSet set, ItemElement production) {
		this.set = set;
		this.production = production;
	}
	
	@Override
	public ItemSet getRawSol() {
		return set.copy();
	}
	
	@Override
	public @Nonnull ItemElement getProduction() {
		return production;
	}
	
	@Override
	public boolean apply(ItemSet that) {
		return set.apply(that);
	}
	
	@Override
	public boolean hasElement(ItemElement element) {
		return set.hasElement(element);
	}
	
	@Override
	public boolean hasItem(ItemStack stack) {
		return set.hasItem(stack);
	}
	
}
