package xyz.emptydreams.mi.api.craftguide.only;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.client.resources.I18n;
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
public class OrderedShapeOnly implements IShape<ItemList, ItemElement> {
	
	private final ItemList list;
	private final ItemElement production;
	
	public OrderedShapeOnly(ItemList list, ItemElement production) {
		this.list = list.offset();
		this.production = StringUtil.checkNull(production, "production");
	}
	
	@Override
	public ItemList getInput() {
		return list.copy();
	}
	
	@Override
	public @Nonnull ItemElement getOutput() {
		return production;
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
	public Class<ItemElement> getOutputClass() {
		return ItemElement.class;
	}
	
	@Override
	public String getMainlyName() {
		return I18n.format(list.get(0, 0).getItem().getUnlocalizedName() + ".name");
	}
	
	/**
	 * 注册一个JSON
	 * @param json json内容
	 * @param keyMap KEY值
	 * @throws NullPointerException 如果json中对应的合成表不存在
	 */
	@SuppressWarnings("ConstantConditions")
	public static void pares(JsonObject json, Char2ObjectMap<ItemElement> keyMap) {
		ItemList input = ItemList.parse(json, keyMap);
		ItemElement result = JsonUtil.getElement(json.getAsJsonObject("result"));
		String group = json.get("group").getAsString();
		CraftGuide.getInstance(new ResourceLocation(group)).registry(new OrderedShapeOnly(input, result));
	}
	
}