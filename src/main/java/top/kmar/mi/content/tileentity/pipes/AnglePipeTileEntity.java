package top.kmar.mi.content.tileentity.pipes;

import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.fluid.FTTileEntity;
import top.kmar.mi.api.utils.data.io.Storage;
import top.kmar.mi.content.blocks.base.pipes.enums.AngleFacingEnum;
import top.kmar.mi.api.register.others.AutoTileEntity;

import javax.annotation.Nonnull;

import static net.minecraft.util.EnumFacing.*;

/**
 * 直角拐弯的管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("AnglePipe")
public class AnglePipeTileEntity extends FTTileEntity {
	
	/** 正方向 */
	@Storage
    protected EnumFacing facing;
	/** 后侧方向 */
	@Storage protected EnumFacing after;
	
	public AnglePipeTileEntity() {
		this(NORTH, UP);
	}
	
	public AnglePipeTileEntity(EnumFacing facing, EnumFacing after) {
		if (getMaxAmount() % 2 != 0)
			throw new IllegalArgumentException("最大容量[" + getMaxAmount() + "]应当能被2整除");
		this.facing = facing;
		this.after = after;
		int max = getMaxAmount() / 2;
	}
	
	@Override
	protected void sync(IDataWriter writer) {
		writer.writeByte((byte) facing.ordinal());
		writer.writeByte((byte) after.ordinal());
	}
	
	@Override
	public void syncClient(@Nonnull IDataReader reader) {
		facing = EnumFacing.values()[reader.readByte()];
		after = EnumFacing.values()[reader.readByte()];
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing == this.facing || facing == after;
	}
	
	@Override
	public boolean canLinkFluid(EnumFacing facing) {
		if (hasAperture(facing)) return true;
		if (getLinkData().isInit()) return true;
		if (isLinked(this.facing)) {
			if (isLinked(after)) return false;
			return AngleFacingEnum.match(this.facing, facing);
		}
		return AngleFacingEnum.match(after, facing);
	}
	
	@Override
	public void unlink(EnumFacing facing) {
		if (facing == this.facing) {
			this.facing = after;
			after = facing;
		}
		super.unlink(facing);
	}
	
	@Override
	public boolean linkFluid(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!canLinkFluid(facing)) return false;
		if (facing.getAxis() == Axis.Y) {
			if (getLinkData().isInit()) {
				this.facing = facing;
				if (after == DOWN || after == UP) after = NORTH;
			} else {
				after = facing;
				if (this.facing.getAxis() == Axis.Y) this.facing = NORTH;
			}
		} else {
			if (getLinkData().isInit()) {
				this.facing = facing;
				if (after.getAxis() == facing.getAxis()) after = UP;
			} else {
				after = facing;
			}
		}
		getLinkData().set(facing, true);
		updateBlockState(false);
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