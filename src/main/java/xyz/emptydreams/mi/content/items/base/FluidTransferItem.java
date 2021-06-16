package xyz.emptydreams.mi.content.items.base;

import net.minecraft.item.ItemBlock;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.content.blocks.base.FluidTransferBlock;

/**
 * @author EmptyDreams
 */
public class FluidTransferItem extends ItemBlock {
	
	public FluidTransferItem(FluidTransferBlock block, String name) {
		super(block);
		setRegistryName(ModernIndustry.MODID, name);
		setUnlocalizedName(name);
		setCreativeTab(block.getCreativeTabToDisplayOn());
	}
	
}