package top.kmar.mi.content.tileentity.pipes;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.fluid.FTTileEntity;
import top.kmar.mi.api.register.block.annotations.AutoTileEntity;
import top.kmar.mi.data.properties.AngleFacingEnum;

import javax.annotation.Nonnull;

import static net.minecraft.util.EnumFacing.*;

/**
 * 直角拐弯的管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("AnglePipe")
public class AnglePipeTileEntity extends FTTileEntity {
	
	/** 正方向 */
	@AutoSave protected EnumFacing facing;
	/** 后侧方向 */
	@AutoSave protected EnumFacing after;
	
	public AnglePipeTileEntity() {
		this(NORTH, UP);
	}
	
	public AnglePipeTileEntity(EnumFacing facing, EnumFacing after) {
		if (getMaxAmount() % 2 != 0)
			throw new IllegalArgumentException("最大容量[" + getMaxAmount() + "]应当能被2整除");
		this.facing = facing;
		this.after = after;
	}
	
	@Override
	protected NBTBase sync() {
		NBTTagCompound result = new NBTTagCompound();
		result.setByte("fac", (byte) facing.ordinal());
		result.setByte("aft", (byte) facing.ordinal());
		return result;
	}
	
	@Override
	public void syncClient(@Nonnull NBTBase reader) {
		NBTTagCompound nbt = (NBTTagCompound) reader;
		facing = EnumFacing.values()[nbt.getByte("fac")];
		after = EnumFacing.values()[nbt.getByte("aft")];
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing == this.facing || facing == after;
	}
	
	@Override
	public boolean canLinkFluid(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!super.canLinkFluid(facing)) return false;
		if (hasAperture(facing)) return true;
		if (getLinkData().isInit()) return true;
		if (isLinked(this.facing)) {
			if (isLinked(after)) return false;
			return AngleFacingEnum.match(this.facing, facing);
		}
		return AngleFacingEnum.match(after, facing);
	}
	
	@Override
	public boolean linkFluid(EnumFacing facing) {
		if (!super.linkFluid(facing)) return false;
		if (!hasAperture(facing)) {
			if (isLinked(this.facing)) {
				after = facing;
			} else if (facing.getAxis() == Axis.Y) {
				assert isLinked(after);
				after = facing;
			} else this.facing = facing;
			updateBlockState(false);
		}
		return true;
	}
	
	@Override
	public int getLinkedAmount() {
		if (isLinked(getFacing())) return isLinked(getAfter()) ? 2 : 1;
		else return isLinked(getAfter()) ? 1 : 0;
	}
	
	public EnumFacing getFacing() {
		if (facing == UP || facing == DOWN) return after;
		return facing;
	}
	
	public EnumFacing getAfter() {
		if (facing == UP || facing == DOWN) return facing;
		return after;
	}
}