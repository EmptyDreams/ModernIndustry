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
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.event.CraftGuideRegistryEvent;
import xyz.emptydreams.mi.api.utils.data.Size2D;
import xyz.emptydreams.mi.blocks.common.OreBlock;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.COAL;
import static xyz.emptydreams.mi.api.utils.ItemUtil.newStack;
import static xyz.emptydreams.mi.blocks.common.OreBlock.getInstance;
import static xyz.emptydreams.mi.items.common.CommonItems.*;

/**
 * 存储各种合成表
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class CraftList {

	private static final Size2D ONLY = size(1, 1);
	
	/** 火力发电机 */
	public static final CraftGuide<UnorderlyShapeOnly, ItemElement> FIRE_POWER
				= CraftGuide.instance("fire_powder",
									  ONLY, ONLY,
									  UnorderlyShapeOnly.class,
									  ItemElement.class);
	/** 粉碎机 */
	public static final CraftGuide<UnorderlyShapeOnly, ItemElement> PULVERIZER
				= CraftGuide.instance("pulverizer",
									  ONLY, ONLY,
									  UnorderlyShapeOnly.class,
									  ItemElement.class);
	/** 压缩机 */
	public static final CraftGuide<UnorderlyShapeOnly, ItemElement> COMPRESSOR
				= CraftGuide.instance("compressor",
									  ONLY, ONLY,
									  UnorderlyShapeOnly.class,
									  ItemElement.class);
	/** 电子合成台 */
	public static final CraftGuide<OrderlyShape, ItemSet> SYNTHESIZER
				= CraftGuide.instance("electron_synthesizer",
									  size(5, 5),
									  size(2, 2),
									  OrderlyShape.class,
									  ItemSet.class);

	@SubscribeEvent
	public static void registryCraft(CraftGuideRegistryEvent event) {
		FIRE_POWER.registry(
				createOneCraft(new ItemStack(COAL), newStack(ITEM_COAL_BURN_POWDER)),
				createOneCraft(new ItemStack(COAL, 1, 1), newStack(ITEM_COAL_BURN_POWDER))
		);
		PULVERIZER.registry(
				createOneCraft(COAL_ORE, ITEM_COAL_CRUSH),
				createOneCraft(IRON_ORE, ITEM_IRON_CRUSH),
				createOneCraft(GOLD_ORE, ITEM_GOLD_CRUSH),
				createOneCraft(DIAMOND_ORE, ITEM_DIAMOND_CRUSH),
				createOneCraft(getInstance(OreBlock.NAME_TIN), ITEM_TIN_CRUSH),
				createOneCraft(getInstance(OreBlock.NAME_COPPER), ITEM_COPPER_CRUSH)
		);
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
	
	private static Size2D size(int width, int height) {
		return new Size2D(width, height);
	}
	
}
