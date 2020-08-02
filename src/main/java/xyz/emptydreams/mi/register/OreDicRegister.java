package xyz.emptydreams.mi.register;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import xyz.emptydreams.mi.api.event.CraftGuideRegistryEvent;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.blocks.common.OreBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于自动注册矿物词典
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class OreDicRegister {

	private static Map<ItemStack, String[]> itemMap = new HashMap<>();

	/** @see #registry(ItemStack, String...)  */
	public static void registry(Block block, String... names) {
		registry(new ItemBlock(block), names);
	}

	/** @see #registry(ItemStack, String...)  */
	public static void registry(Item item, String... names) {
		registry(item.getDefaultInstance(), names);
	}

	/**
	 * 注册一个矿物词典，该方法可以在方块/物品真正注册之前调用
	 * @param stack 要注册的方块/物品
	 * @param names 矿物词典
	 * @throws NullPointerException 如果stack == null | names == null
	 * @throws UnsupportedOperationException 如果注册矿物词典的事件已经被触发且传入的方块/物品没有注册到MC
	 */
	public static void registry(ItemStack stack, String... names) {
		String[] src = null;
		ItemStack end = stack;
		Item item = stack.getItem();
		//如果物品已经注册到MC中则直接注册否则写入到注册列表中
		if (item.delegate.name() != null) {
			registryDic(stack, names);
			return;
		}
		if (itemMap == null) throw new UnsupportedOperationException(
				"列表注册的事件已经被触发，对于未注册到MC中的方块/物品，该方法已经不支持为其注册矿物词典");
		for (Map.Entry<ItemStack, String[]> entry : itemMap.entrySet()) {
			if (entry.getKey().getItem() == item) {
				src = entry.getValue();
				end = entry.getKey();
				break;
			}
		}
		itemMap.put(end, StringUtil.merge(src, names));
	}

	/** 用于注册矿物词典，因为没有专用的事件，所以这里使用注册注册表的事件取代 */
	@SubscribeEvent
	public static void registryDic(CraftGuideRegistryEvent event) {
		itemMap.forEach(OreDicRegister::registryDic);
		for (OreBlock block : OreBlock.LIST.values()) {
			GameRegistry.addSmelting(block.getBlockItem(), block.getBurnOut().getDefaultInstance(), 0.5F);
		}
		//使itemMap得以被GC回收
		itemMap = null;
	}

	private static void registryDic(ItemStack stack, String[] names) {
		for (String name : names) {
			OreDictionary.registerOre(name, stack);
		}
	}

}
