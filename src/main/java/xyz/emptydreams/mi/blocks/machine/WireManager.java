package xyz.emptydreams.mi.blocks.machine;

import net.minecraft.world.World;
import xyz.emptydreams.mi.api.register.AutoManager;
import xyz.emptydreams.mi.blocks.base.TransferBlock;
import xyz.emptydreams.mi.blocks.tileentity.EleSrcCable;

import javax.annotation.Nullable;

/**
 * 电线的管理类
 * @author EmptyDremas
 */
@AutoManager(block = true)
public final class WireManager {
	
	/** 铜质导线 */
	public final static TransferBlock COPPER = new TransferBlock(
						"wire_copper", "itemCopperCable", "mi_cable") {
		@Nullable
		@Override
		public EleSrcCable createNewTileEntity(World worldIn, int meta) {
			//防止折叠
			return new EleSrcCable(500, 1);
		}
	};
	
}
