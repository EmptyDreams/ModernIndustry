package xyz.emptydreams.mi.content.blocks.machine;

import net.minecraft.world.World;
import xyz.emptydreams.mi.api.register.others.AutoManager;
import xyz.emptydreams.mi.content.blocks.base.EleTransferBlock;
import xyz.emptydreams.mi.content.tileentity.EleSrcCable;

/**
 * 电线的管理类
 * @author EmptyDremas
 */
@AutoManager(block = true)
public final class WireManager {
	
	/** 铜质导线 */
	public final static EleTransferBlock COPPER = new EleTransferBlock(
						"wire_copper", "itemCopperCable", "mi_cable") {
		@Override
		public EleSrcCable createNewTileEntity(World worldIn, int meta) {
			//防止折叠
			return new EleSrcCable(500, 1);
		}
	};
	
}