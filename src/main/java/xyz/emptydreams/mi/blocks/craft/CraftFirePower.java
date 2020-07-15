package xyz.emptydreams.mi.blocks.craft;

import xyz.emptydreams.mi.api.craftguide.CraftRegistry;
import xyz.emptydreams.mi.api.craftguide.ICraftGuide;
import xyz.emptydreams.mi.register.AutoLoader;

import static xyz.emptydreams.mi.items.common.CommonItems.*;
import static net.minecraft.init.Items.*;
import static xyz.emptydreams.mi.api.utils.CraftUtil.createOneCraft;

/**
 * 火力发电机的合成表
 * @author EmptyDreams
 */
@AutoLoader
public class CraftFirePower {

	public static final CraftRegistry CRAFT = CraftRegistry.instance("firepower");

	static {
		ICraftGuide[] crafts = { createOneCraft(COAL, ITEM_COAL_BURN_POWDER) };
		CRAFT.register(crafts);
	}

}
