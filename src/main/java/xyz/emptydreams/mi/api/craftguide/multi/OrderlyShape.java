package xyz.emptydreams.mi.api.craftguide.multi;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;

import javax.annotation.Nonnull;

/**
 * 多项有序合成表
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
	public ItemList getInput() {
		return list.copy();
	}
	
	@Override
	public ItemSet getOutput() {
		return production.copy();
	}
	
	@Override
	public boolean apply(ItemList that) {
		return list.apply(that);
	}
	
	@Override
	public boolean haveElement(ItemElement element) {
		return list.hasElement(element);
	}
	
	@Override
	public boolean haveItem(ItemStack stack) {
		return list.hasItem(stack);
	}
	
	@Nonnull
	@Override
	public Class<ItemList> getInputClass() {
		return ItemList.class;
	}
	
	@Nonnull
	@Override
	public Class<ItemSet> getOutputClass() {
		return ItemSet.class;
	}
	
	/** 注册一个JSON */
	public static void pares(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		ItemSet result = ItemSet.parse(json.getAsJsonObject("result"), keyMap);
		ItemList input = ItemList.parse(json, keyMap);
		String group = json.get("group").getAsString();
		Block block = Block.getBlockFromName(group);
		if (block != null) {
			CraftGuide.getInstance(new ResourceLocation(
					block.getRegistryName().getResourceDomain(), block.getUnlocalizedName()))
					.registry(new OrderlyShape(input, result));
		} else {
			Item item = Item.getByNameOrId(group);
			if (item != null) {
				CraftGuide.getInstance(new ResourceLocation(
						item.getRegistryName().getResourceDomain(), item.getUnlocalizedName()))
						.registry(new OrderlyShape(input, result));
			}
		}
	}
	
}
