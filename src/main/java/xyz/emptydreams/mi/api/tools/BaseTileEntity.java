package xyz.emptydreams.mi.api.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.utils.data.io.ObjectData;

/**
 * 基础TE，提供了一些默认的功能
 * @author EmptyDreams
 */
public class BaseTileEntity extends TileEntity {
	
	/**
	 * 方块更新时若是同种方块则不更新TE
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @param oldState 旧的state
	 * @param newSate 新的state
	 * @return 是否更新TE
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		ObjectData.write(this, compound, ".");
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		ObjectData.read(this, compound, ".");
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() +
				       "{world=" + (world == null ? "null" : world.getProviderName()) +
				       "pos=" + pos + '}';
	}
	
}