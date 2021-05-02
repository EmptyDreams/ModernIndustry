package xyz.emptydreams.mi.content.items.fluids;

import net.minecraft.item.ItemBucket;
import xyz.emptydreams.mi.content.blocks.fluids.FluidIron;

/**
 * @author EmptyDreams
 */
public class ItemBucketIron extends ItemBucket {
	
	public ItemBucketIron() {
		super(FluidIron.blockInstance());
	}
	
}