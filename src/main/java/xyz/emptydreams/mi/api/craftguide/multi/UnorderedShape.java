package xyz.emptydreams.mi.api.craftguide.multi;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;

import javax.annotation.Nonnull;

/**
 * 多项无序合成表
 * @author EmptyDreams
 */
public class UnorderedShape implements IShape<ItemSet, ItemSet> {
	
	private final ItemSet raw;
	private final ItemSet production;
	
	public UnorderedShape(ItemSet input, ItemSet output) {
		raw = input.offset();
		production = output.offset();
	}
	
	@Nonnull
	@Override
	public ItemSet getInput() {
		return raw.copy();
	}
	
	@Override
	public ItemSet getOutput() {
		return production.copy();
	}
	
	@Override
	public boolean apply(ItemSet that) {
		return raw.apply(that);
	}
	
	@Override
	public boolean haveElement(ItemElement element) {
		return raw.hasElement(element);
	}
	
	@Override
	public boolean haveItem(ItemStack stack) {
		return raw.hasItem(stack);
	}
	
	@Nonnull
	@Override
	public Class<ItemSet> getInputClass() {
		return ItemSet.class;
	}
	
	@Nonnull
	@Override
	public Class<ItemSet> getOutputClass() {
		return ItemSet.class;
	}
	
	@Override
	public String getMainlyName() {
		return I18n.format(production.iterator().next().getItem().getUnlocalizedName() + ".name");
	}
	
	/**
	 * 注册一个JSON
	 * @param json json内容
	 * @param keyMap KEY值
	 * @throws NullPointerException 如果json中对应的合成表不存在
	 */
	@SuppressWarnings("ConstantConditions")
	public static void pares(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		ItemSet input = ItemSet.parse(json, keyMap);
		ItemSet result = ItemSet.parse(json.getAsJsonObject("result"), keyMap);
		String group = json.get("group").getAsString();
		CraftGuide.getInstance(new ResourceLocation(group)).registry(new UnorderedShape(input, result));
	}
	
}