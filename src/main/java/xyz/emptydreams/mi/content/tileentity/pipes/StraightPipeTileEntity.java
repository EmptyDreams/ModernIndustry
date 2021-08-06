package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 直线型管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("StraightPipe")
public class StraightPipeTileEntity extends FTTileEntity {
	
	@Storage protected EnumFacing facing;
	
	public StraightPipeTileEntity() {
		this(EnumFacing.NORTH);
	}
	
	public StraightPipeTileEntity(EnumFacing facing) {
		this.facing = facing;
	}
	
	@Override
	protected void sync(IDataWriter writer) {
		writer.writeByte((byte) facing.ordinal());
	}
	
	@Override
	public void syncClient(@Nonnull IDataReader reader) {
		facing = EnumFacing.values()[reader.readByte()];
	}
	
	@Override
	public List<EnumFacing> next() {
		EnumFacing side = facing.getOpposite();
		if (source == null) return Lists.newArrayList(facing, side);
		if (source == facing) return Lists.newArrayList(side);
		if (source == side) return Lists.newArrayList(facing);
		throw new IllegalArgumentException("输入了没有开口的方向：" + source);
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing == this.facing || facing == this.facing.getOpposite();
	}
	
	@Override
	public boolean canLink(EnumFacing facing) {
		if (hasAperture(facing)) return true;
		return linkData == 0;
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (cap.isLinked(facing)) return true;
		if (!cap.canLink(facing)) return false;
		if (linkData == 0) setFacing(facing);
		setLinkedData(facing, true);
		return true;
	}
	
	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}
	
	public EnumFacing getFacing() {
		return facing;
	}
	
}