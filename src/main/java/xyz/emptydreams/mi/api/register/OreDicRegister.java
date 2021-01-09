package xyz.emptydreams.mi.api.register;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
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

import static xyz.emptydreams.mi.api.utils.ItemUtil.newStack;

/**
 * 用于自动注册矿物词典
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class OreDicRegister {

	private static Map<ItemStack, String[]> itemMap = new HashMap<>();
	private static Map<Block, String[]> blockMap = new HashMap<>();

	/** @see #registry(ItemStack, String...)  */
	public static void registry(Block block, String... names) {
		blockMap.put(StringUtil.checkNull(block, "block"), names);
	}

	/** @see #registry(ItemStack, String...)  */
	public static void registry(Item item, String... names) {
		registry(new ItemStack(item), names);
	}

	/**
	 * 注册一个矿物词典，该方法可以在方块/物品真正注册之前调用
	 * @param stack 要注册的方块/物品
	 * @param names 矿物词典
	 * @throws NullPointerException 如果stack == null | names == null
	 * @throws UnsupportedOperationException 如果注册矿物词典的事件已经被触发且传入的方块/物品没有注册到MC
	 */
	public static void registry(ItemStack stack, String... names) {
		if (itemMap == null) {
			registryDic(stack, names);
			return;
		}
		String[] src = null;
		ItemStack end = stack;
		Item item = stack.getItem();
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
		blockMap.forEach(OreDicRegister::registryDic);
		for (OreBlock block : OreBlock.LIST.values()) {
			GameRegistry.addSmelting(block.getBlockItem(), newStack(block.getBurnOut()), 0.5F);
		}
		//使Map得以被GC回收
		itemMap = null;
		blockMap = null;
	}

	private static void registryDic(Block block, String[] names) {
		registryDic(new ItemStack(block), names);
	}
	
	private static void registryDic(ItemStack stack, String[] names) {
		for (String name : names) {
			OreDictionary.registerOre(name, stack);
		}
	}

}
