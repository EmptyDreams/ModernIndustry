package xyz.emptydreams.mi.content.blocks.machine;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoManager;
import xyz.emptydreams.mi.content.blocks.base.FluidTransferBlock;

/**
 * 包含大部分流体管道
 * @author EmptyDreams
 */
@AutoManager(block = true)
public final class FluidTransferManager {
	
	public static final FluidTransferBlock IRON_FT = new FluidTransferBlock("iron_ft") {
		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			return new FTTileEntity();
		}
	};
	
}