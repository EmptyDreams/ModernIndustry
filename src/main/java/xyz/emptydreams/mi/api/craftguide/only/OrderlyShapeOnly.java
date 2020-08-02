package xyz.emptydreams.mi.api.craftguide.only;

import net.minecraft.item.ItemStack;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;

/**
 * 有序合成表
 * @author EmptyDreams
 */
public class OrderlyShapeOnly implements IShape<ItemList, ItemElement> {
	
	private final ItemList list;
	private final ItemElement production;
	
	public OrderlyShapeOnly(ItemList list, ItemElement production) {
		WaitList.checkNull(production, "production");
		this.list = list.offset();
		this.production = production;
	}
	
	@Override
	public ItemList getRawSol() {
		return list.copy();
	}
	
	@Override
	public @Nonnull ItemElement getProduction() {
		return production;
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
