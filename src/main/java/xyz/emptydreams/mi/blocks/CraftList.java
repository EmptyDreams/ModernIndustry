package xyz.emptydreams.mi.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.multi.OrderlyShape;
import xyz.emptydreams.mi.api.craftguide.only.UnorderlyShapeOnly;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.event.CraftGuideRegistryEvent;
import xyz.emptydreams.mi.blocks.common.OreBlock;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.COAL;
import static xyz.emptydreams.mi.blocks.common.OreBlock.getInstance;
import static xyz.emptydreams.mi.items.common.CommonItems.*;

/**
 * 存储各种合成表
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class CraftList {

	/** 火力发电机 */
	public static final CraftGuide<UnorderlyShapeOnly, ItemElement> FIRE_POWER =
															CraftGuide.instance("fire_powder");
	/** 粉碎机 */
	public static final CraftGuide<UnorderlyShapeOnly, ItemElement> PULVERIZER =
															CraftGuide.instance("pulverizer");
	/** 压缩机 */
	public static final CraftGuide<UnorderlyShapeOnly, ItemElement> COMPRESSOR =
															CraftGuide.instance("compressor");
	/** 电子合成台 */
	public static final CraftGuide<OrderlyShape, ItemSet> SYNTHESIZER =
															CraftGuide.instance("electron_synthesizer");

	@SubscribeEvent
	public static void registryCraft(CraftGuideRegistryEvent event) {
		FIRE_POWER.registry(
				createOneCraft(new ItemStack(COAL), ITEM_COAL_BURN_POWDER.getDefaultInstance()),
				createOneCraft(new ItemStack(COAL, 1, 1), ITEM_COAL_BURN_POWDER.getDefaultInstance())
		);
		PULVERIZER.registry(
				createOneCraft(COAL_ORE, ITEM_COAL_CRUSH),
				createOneCraft(IRON_ORE, ITEM_IRON_CRUSH),
				createOneCraft(GOLD_ORE, ITEM_GOLD_CRUSH),
				createOneCraft(DIAMOND_ORE, ITEM_DIAMOND_CRUSH),
				createOneCraft(getInstance(OreBlock.NAME_TIN), ITEM_TIN_CRUSH),
				createOneCraft(getInstance(OreBlock.NAME_COPPER), ITEM_COPPER_CRUSH)
		);
		registrySynthesizer();
	}

	private static void registrySynthesizer() {
		SYNTHESIZER.registry(CraftList::toOrderlyShape,
				 "electron_synthesizer/fire_power.json",
						"electron_synthesizer/pulverizer.json",
						"electron_synthesizer/red_stone_converter.json",
						"electron_synthesizer/ele_mfurnace.json",
						"electron_synthesizer/ele_furnace.json",
						"electron_synthesizer/compressor.json"
		);
	}
	
	private static OrderlyShape toOrderlyShape(ItemSol sol, ItemSet output) {
		return new OrderlyShape((ItemList) sol, output);
	}
	
	private static UnorderlyShapeOnly createOneCraft(Block input, Item output) {
		ItemSet set = new ItemSet();
		set.add(ItemElement.instance(input, 1));
		return new UnorderlyShapeOnly(set, ItemElement.instance(output, 2));
	}
	
	private static UnorderlyShapeOnly createOneCraft(ItemStack input, ItemStack output) {
		ItemSet set = new ItemSet();
		set.add(ItemElement.instance(input));
		return new UnorderlyShapeOnly(set, ItemElement.instance(output));
	}
	
}
