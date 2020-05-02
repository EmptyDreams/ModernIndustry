package xyz.emptydreams.mi.api.electricity.src.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.utils.TEHelper;
import xyz.emptydreams.mi.register.te.AutoTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 所有于电有关的设备的父级TE
 *
 * @author EmptyDremas
 * @version V2.0
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY")
public abstract class Electricity extends TileEntity implements TEHelper {
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	/** 过载最长时间 */
	protected int biggerMaxTime = 50;
	
	/** 设置过载最长时间(单位：tick，默认值：50tick)，当设置时间小于0时保持原设置不变 */
	@SuppressWarnings("UnusedReturnValue")
	protected void setBiggerMaxTime(int bvt) {
		biggerMaxTime = (bvt >= 0) ? bvt : biggerMaxTime;
	}
	/** 获取最长过载时间 */
	public int getBiggerMaxTime() {
		return biggerMaxTime;
	}
	
	/** 设置方块类型 */
	public final void setBlockType(Block block) {
		blockType = block;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return TEHelper.super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		TEHelper.super.readFromNBT(compound);
	}
	
	@Override
	public String toString() {
		return "Electricity{ pos=" + pos + '}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Electricity e = (Electricity) o;
		if (world == null) {
			return e.getWorld() == null;
		}
		return world == e.getWorld() && pos.equals(e.getPos());
	}
	
	@Override
	public int hashCode() {
		return pos.hashCode();
	}
}