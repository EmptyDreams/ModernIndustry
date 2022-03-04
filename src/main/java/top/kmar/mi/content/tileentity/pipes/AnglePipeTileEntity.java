package top.kmar.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.fluid.FTTileEntity;
import top.kmar.mi.api.utils.data.io.Storage;
import top.kmar.mi.content.blocks.base.pipes.enums.AngleFacingEnum;
import top.kmar.mi.api.register.others.AutoTileEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	@Nonnull
	@Override
	public List<EnumFacing> next(EnumFacing facing) {
		if (facing == this.facing)
			return hasPlug(this.facing) ? Collections.emptyList() : Lists.newArrayList(after);
		if (facing == after)
			return hasPlug(after) ? Collections.emptyList() : Lists.newArrayList(this.facing);
		if (facing == null) {
			List<EnumFacing> result = new ArrayList<>(2);
			if (!hasPlug(this.facing)) result.add(this.facing);
			if (!hasPlug(after)) result.add(after);
			return result;
		}
		throw new IllegalArgumentException("输入方向上没有开口：" + facing);
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing == this.facing || facing == after;
	}
	
	@Override
	public boolean canLink(EnumFacing facing) {
		if (hasAperture(facing)) return true;
		if (linkData.isInit()) return true;
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
	public boolean link(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		if (facing.getAxis() == Axis.Y) {
			if (linkData.isInit()) {
				this.facing = facing;
				if (after == DOWN || after == UP) after = NORTH;
			} else {
				after = facing;
				if (this.facing.getAxis() == Axis.Y) this.facing = NORTH;
			}
		} else {
			if (linkData.isInit()) {
				this.facing = facing;
				if (after.getAxis() == facing.getAxis()) after = UP;
			} else {
				after = facing;
			}
		}
		linkData.set(facing, true);
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