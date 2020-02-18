package minedreams.mi.api.electricity.block;

import minedreams.mi.api.electricity.ElectricityMaker;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.ElectricityUser;
import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.blocks.register.BlockBaseT;
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
public abstract class MachineBlock extends BlockBaseT implements IEleInfo {
	
	public MachineBlock(Material materialIn) {
		super(materialIn);
	}
	
	private Boolean isUser = null;
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntity now = world.getTileEntity(pos);
		if (now == null) return;
		TileEntity from = world.getTileEntity(fromPos);
		if (isUser == null) isUser = now instanceof ElectricityUser;
		if (isUser) {
			ElectricityUser user = (ElectricityUser) now;
			if (from == null) {
				user.removeLink(fromPos);
			} else if (from instanceof ElectricityMaker) {
				user.link((ElectricityMaker) from);
			} else if (from instanceof ElectricityTransfer) {
				ElectricityTransfer et = (ElectricityTransfer) from;
				user.link(et);
				et.link(now);
			}
		} else {
			ElectricityMaker maker = (ElectricityMaker) now;
			if (from instanceof ElectricityUser) {
				((ElectricityUser) from).link(maker);
			}
		}
	}
	
}
