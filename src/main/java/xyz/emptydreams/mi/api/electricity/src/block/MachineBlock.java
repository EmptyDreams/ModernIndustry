package xyz.emptydreams.mi.api.electricity.src.block;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import xyz.emptydreams.mi.api.electricity.capabilities.ILink;
import xyz.emptydreams.mi.api.electricity.capabilities.LinkCapability;
import xyz.emptydreams.mi.blocks.register.BlockBaseT;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public abstract class MachineBlock extends BlockBaseT {
	
	public MachineBlock(Material materialIn) {
		super(materialIn);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos,
	                            @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
		TileEntity now = world.getTileEntity(pos);
		ILink link = now.getCapability(LinkCapability.LINK, null);
		blockIn = world.getBlockState(fromPos).getBlock();
		if (link != null) {
			if (blockIn == Blocks.AIR) {
				link.unLink(fromPos);
			} else {
				link.link(fromPos);
			}
		}
	}
	
}
