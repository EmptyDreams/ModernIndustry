package xyz.emptydreams.mi.api.electricity.src.block;

import javax.annotation.Nonnull;

import xyz.emptydreams.mi.api.electricity.src.tileentity.EleMaker;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcUser;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcCable;
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
	
	private Boolean isUser = null;
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos,
	                            @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
		TileEntity now = world.getTileEntity(pos);
		if (now == null) return;
		TileEntity from = world.getTileEntity(fromPos);
		if (isUser == null) isUser = now instanceof EleSrcUser;
		if (isUser) {
			EleSrcUser user = (EleSrcUser) now;
			if (from == null) {
				user.removeLink(fromPos);
			} else if (from instanceof EleMaker) {
				user.link((EleMaker) from);
			} else if (from instanceof EleSrcCable) {
				EleSrcCable et = (EleSrcCable) from;
				user.link(et);
				et.link(pos);
			}
		} else {
			EleMaker maker = (EleMaker) now;
			if (from instanceof EleSrcUser) {
				((EleSrcUser) from).link(maker);
			} else if (from instanceof EleSrcCable) {
				((EleSrcCable) from).link(pos);
			}
		}
	}
	
}
