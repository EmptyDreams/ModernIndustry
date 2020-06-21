package xyz.emptydreams.mi.blocks.machine;

import javax.annotation.Nullable;

import net.minecraft.world.World;
import xyz.emptydreams.mi.blocks.base.TransferBlock;
import xyz.emptydreams.mi.blocks.te.EleSrcCable;
import xyz.emptydreams.mi.register.AutoManager;

/**
 * 电线的管理类
 * @author EmptyDremas
 * @version V1.0
 */
@AutoManager(block = true, item = false)
public final class WireManager {
	
	/** 铜质导线 */
	public final static TransferBlock COPPER = new TransferBlock("wire_copper") {
		
		@Nullable
		@Override
		public EleSrcCable createNewTileEntity(World worldIn, int meta) {
			//防止折叠
			return new EleSrcCable(1000, 1);
		}
		
	};
	
}
