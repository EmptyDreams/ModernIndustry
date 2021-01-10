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
 * 单项无序合成表
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
	public ItemSet getInput() {
		return set.copy();
	}
	
	@Override
	public @Nonnull ItemElement getOutput() {
		return production;
	}
	
	@Override
	public boolean apply(ItemSet that) {
		return set.apply(that);
	}
	
	@Override
	public boolean haveElement(ItemElement element) {
		return set.hasElement(element);
	}
	
	@Override
	public boolean haveItem(ItemStack stack) {
		return set.hasItem(stack);
	}
	
	@Nonnull
	@Override
	public Class<ItemSet> getInputClass() {
		return ItemSet.class;
	}
	
	@Nonnull
	@Override
	public Class<ItemElement> getOutputClass() {
		return ItemElement.class;
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
		ItemElement result = JsonUtil.getElement(json.getAsJsonObject("result"));
		String group = json.get("group").getAsString();
		CraftGuide.getInstance(new ResourceLocation(group)).registry(new UnorderlyShapeOnly(input, result));
	}
	
}
