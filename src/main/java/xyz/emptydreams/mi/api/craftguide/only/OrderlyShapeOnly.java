package xyz.emptydreams.mi.api.craftguide.only;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.utils.JsonUtil;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;

/**
 * 单项有序合成表
 * @author EmptyDreams
 */
public class OrderlyShapeOnly implements IShape<ItemList, ItemElement> {
	
	private final ItemList list;
	private final ItemElement production;
	
	public OrderlyShapeOnly(ItemList list, ItemElement production) {
		this.list = list.offset();
		this.production = StringUtil.checkNull(production, "production");
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
	
	@Nonnull
	@Override
	public Class<ItemList> getItemSolClass() {
		return ItemList.class;
	}
	
	@Nonnull
	@Override
	public Class<ItemElement> getProtectedClass() {
		return ItemElement.class;
	}
	
	/** 注册一个JSON */
	public static void pares(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		ItemList input = ItemList.parse(json, keyMap);
		ItemElement result = JsonUtil.getElement(json.getAsJsonObject("result"));
		String group = json.get("group").getAsString();
		CraftGuide.getInstance(new ResourceLocation(group)).registry(new OrderlyShapeOnly(input, result));
	}
	
}
