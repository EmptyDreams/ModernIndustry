package xyz.emptydreams.mi.blocks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.craftguide.CraftRegistry;
import xyz.emptydreams.mi.api.event.CraftGuideRegistryEvent;
import xyz.emptydreams.mi.blocks.common.OreBlock;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.COAL;
import static xyz.emptydreams.mi.api.utils.CraftUtil.createOneCraft;
import static xyz.emptydreams.mi.blocks.common.OreBlock.getInstance;
import static xyz.emptydreams.mi.items.common.CommonItems.*;

/**
 * 存储各种合成表
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class CraftList {

	/** 火力发电机 */
	public static final CraftRegistry FIRE_POWER = CraftRegistry.instance("fire_powder");
	/** 粉碎机 */
	public static final CraftRegistry PULVERIZER = CraftRegistry.instance("pulverizer");
	/** 压缩机 */
	public static final CraftRegistry COMPRESSOR = CraftRegistry.instance("compressor");

	@SubscribeEvent
	public static void registryCraft(CraftGuideRegistryEvent event) {
		FIRE_POWER.register(
				createOneCraft(COAL, ITEM_COAL_BURN_POWDER),
				createOneCraft(new ItemStack(COAL, 1, 1), ITEM_COAL_BURN_POWDER.getDefaultInstance())
		);
		PULVERIZER.register(
				createOneCraft(COAL_ORE, ITEM_COAL_CRUSH),
				createOneCraft(IRON_ORE, ITEM_IRON_CRUSH),
				createOneCraft(GOLD_ORE, ITEM_GOLD_CRUSH),
				createOneCraft(DIAMOND_ORE, ITEM_DIAMOND_CRUSH),
				createOneCraft(getInstance(OreBlock.NAME_TIN), ITEM_TIN_CRUSH),
				createOneCraft(getInstance(OreBlock.NAME_COPPER), ITEM_COPPER_CRUSH)
		);
	}

}
