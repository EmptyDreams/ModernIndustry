package xyz.emptydreams.mi.api.craftguide.only;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.utils.JsonUtil;

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
	
	/** 注册一个JSON */
	public static void pares(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		ItemSet input = ItemSet.parse(json, keyMap);
		ItemElement result = JsonUtil.getElement(json.getAsJsonObject("result"));
		String group = json.get("group").getAsString();
		CraftGuide.getInstance(new ResourceLocation(group)).registry(new UnorderlyShapeOnly(input, result));
	}
	
}
