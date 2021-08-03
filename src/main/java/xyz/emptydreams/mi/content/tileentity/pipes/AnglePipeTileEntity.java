package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.content.blocks.base.pipes.enums.AngleFacingEnum;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

/**
 * 直角拐弯的管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("AnglePipe")
public class AnglePipeTileEntity extends FTTileEntity {
	
	@Storage protected EnumFacing facing;
	@Storage protected EnumFacing after;
	
	public AnglePipeTileEntity() {
		this(NORTH, UP);
	}
	
	public AnglePipeTileEntity(EnumFacing facing, EnumFacing after) {
		this.facing = facing;
		this.after = after;
	}
	
	@Override
	protected void sync(IDataWriter writer) {
		writer.writeByte((byte) facing.ordinal());
		writer.writeByte((byte) after.ordinal());
	}
	
	@Override
	public void receive(@Nonnull IDataReader reader) {
		super.receive(reader);
		facing = EnumFacing.values()[reader.readByte()];
		after = EnumFacing.values()[reader.readByte()];
	}
	
	@Override
	public List<EnumFacing> next() {
		if (source == null) return Lists.newArrayList(facing, after);
		if (source == facing) return Lists.newArrayList(after);
		if (source == after) return Lists.newArrayList(facing);
		throw new IllegalArgumentException("输入了没有开口的方向：" + source);
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing == this.facing || facing == after;
	}
	
	@Override
	public boolean canLink(EnumFacing facing) {
		if (hasAperture(facing)) return true;
		if (linkData == 0) return true;
		if (cap.isLinked(facing)) {
			if (cap.isLinked(after)) return false;
		}
		return AngleFacingEnum.match(this.facing, facing);
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (cap.isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		if (facing == DOWN || facing == UP) {
			if (linkData == 0 || cap.getLinkAmount() == 1 && AngleFacingEnum.match(this.facing, facing)) {
				after = facing;
			}
		} else {
			if (linkData == 0) {
				this.facing = facing;
				if (!AngleFacingEnum.match(facing, after)) {
					boolean result = true;
					if (after != UP && after != DOWN) {
						for (EnumFacing value : HORIZONTALS) {
							if (AngleFacingEnum.match(facing, value)) {
								result = false;
								after = value;
								break;
							}
						}
					}
					if (result) after = after == DOWN ? UP : DOWN;
				}
			} else if (cap.getLinkAmount() == 1 && AngleFacingEnum.match(this.facing, facing)) {
				after = facing;
			}
		}
		setLinkedData(facing, true);
		return true;
	}
	
	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}
	
	public void setAfter(EnumFacing after) {
		this.after = after;
	}
	
	public EnumFacing getFacing() {
		return facing;
	}
	
	public EnumFacing getAfter() {
		return after;
	}
}