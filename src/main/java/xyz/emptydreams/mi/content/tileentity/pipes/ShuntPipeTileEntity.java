package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

/**
 * 十字管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("ShuntPipe")
public class ShuntPipeTileEntity extends FTTileEntity {
	
	/** 管道侧面面对的方向 */
	@Storage protected EnumFacing side = UP;
	protected List<EnumFacing> linked = new ArrayList<>(4);
	
	@Override
	protected void sync(IDataWriter writer) {
		writer.writeByte((byte) side.ordinal());
	}
	
	@Override
	protected void syncClient(IDataReader reader) {
		side = EnumFacing.values()[reader.readByte()];
		if (linkData == 0) return;
		for (EnumFacing value : values()) {
			if (cap.isLinked(value)) linked.add(value);
		}
	}
	
	@Override
	public List<EnumFacing> next() {
		List<EnumFacing> result = Lists.newArrayList(getFacing());
		result.remove(source);
		return result;
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing != side && facing != side.getOpposite();
	}
	
	@Override
	public boolean canLink(EnumFacing facing) {
		if (hasAperture(facing)) return true;
		if (linkData == 0) return true;
		return cap.getLinkAmount() == 1;
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (cap.isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		setLinkedData(facing, true);
		side = linkData == 0 ? getSide(facing) : getSide(facing, linked.get(0));
		return true;
	}
	
	@Override
	protected void setLinkedData(EnumFacing facing, boolean isLinked) {
		super.setLinkedData(facing, isLinked);
		if (isLinked) linked.add(facing);
		else linked.remove(facing);
	}
	
	protected static EnumFacing getSide(EnumFacing facing, EnumFacing other) {
		switch (facing) {
			case DOWN: case UP:
				switch (other) {
					case NORTH: case SOUTH: return WEST;
					default: return NORTH;
				}
			case NORTH: case SOUTH:
				switch (other) {
					case DOWN: case UP: return WEST;
					default: return UP;
				}
			default:
				switch (other) {
					case DOWN: case UP: return NORTH;
					default: return UP;
				}
		}
	}
	
	protected static EnumFacing getSide(EnumFacing facing) {
		if (facing == UP || facing == DOWN) return NORTH;
		return UP;
	}
	
	public EnumFacing getSide() {
		return side;
	}
	
	protected EnumFacing[] getFacing() {
		switch (side) {
			case DOWN: case UP: return HORIZONTALS;
			case NORTH: case SOUTH: return new EnumFacing[] { UP, DOWN, WEST, EAST };
			case WEST: case EAST: return new EnumFacing[] { UP, DOWN, NORTH, SOUTH };
			default: throw new IllegalArgumentException("未知的方向：" + side);
		}
	}
	
}