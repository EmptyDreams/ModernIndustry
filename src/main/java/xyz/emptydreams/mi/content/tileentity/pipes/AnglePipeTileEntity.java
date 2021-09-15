package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.AngleFacingEnum;
import xyz.emptydreams.mi.content.tileentity.pipes.data.DataManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	@Storage protected EnumFacing facing;
	/** 后侧方向 */
	@Storage protected EnumFacing after;
	protected DataManager facingData;
	protected DataManager afterData;
	
	public AnglePipeTileEntity() {
		this(NORTH, UP);
	}
	
	public AnglePipeTileEntity(EnumFacing facing, EnumFacing after) {
		if (getMaxAmount() % 2 != 0)
			throw new IllegalArgumentException("最大容量[" + getMaxAmount() + "]应当能被2整除");
		this.facing = facing;
		this.after = after;
		facingData = DataManager.instance(getFacing(), getMaxAmount() / 2);
		afterData = DataManager.instance(getAfter(), getMaxAmount() / 2);
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
	
	@Nullable
	@Override
	protected DataManager getDataManager(EnumFacing facing) {
		if (facing == this.facing) return facingData;
		else if (facing == this.after) return afterData;
		return null;
	}
	
	@Override
	protected boolean matchFacing(EnumFacing facing) {
		return facing == this.facing || facing == this.after;
	}
	
	@Override
	public boolean isEmpty() {
		return facingData.isEmpty() && afterData.isEmpty();
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
		if (linkData == 0) return true;
		if (isLinked(this.facing)) {
			if (isLinked(after)) return false;
			return AngleFacingEnum.match(this.facing, facing);
		}
		return AngleFacingEnum.match(after, facing);
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		if (facing == DOWN || facing == UP) {
			if (linkData == 0) {
				this.facing = facing;
				if (after == DOWN || after == UP) after = NORTH;
			} else {
				after = facing;
			}
		} else {
			if (linkData == 0) {
				this.facing = facing;
				if (after == facing || after == facing.getOpposite()) {
					after = UP;
				}
			} else if (getLinkAmount() == 1) {
				if (isLinked(this.facing)) after = facing;
				else this.facing = facing;
			}
		}
		setLinkedData(facing, true);
		rotate();
		updateBlockState(false);
		return true;
	}
	
	protected void rotate() {
		facingData = facingData.rotate(facing);
		afterData = afterData.rotate(after);
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