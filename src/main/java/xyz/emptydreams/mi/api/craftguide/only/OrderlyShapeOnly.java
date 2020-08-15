package xyz.emptydreams.mi.api.craftguide.only;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.utils.JsonUtil;

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
	
	/** 注册一个JSON */
	public static void pares(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		ItemList input = ItemList.parse(json, keyMap);
		ItemElement result = JsonUtil.getElement(json.getAsJsonObject("result"));
		String group = json.get("group").getAsString();
		CraftGuide.instance(new ResourceLocation(group)).registry(new OrderlyShapeOnly(input, result));
	}
	
}
