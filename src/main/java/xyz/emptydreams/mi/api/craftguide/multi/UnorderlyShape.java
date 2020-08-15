package xyz.emptydreams.mi.api.craftguide.multi;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
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
	
	/** 注册一个JSON */
	public static void pares(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		ItemSet input = ItemSet.parse(json, keyMap);
		ItemSet result = ItemSet.parse(json.getAsJsonObject("result"), keyMap);
		String group = json.get("group").getAsString();
		CraftGuide.instance(new ResourceLocation(group)).registry(new UnorderlyShape(input, result));
	}
	
}
