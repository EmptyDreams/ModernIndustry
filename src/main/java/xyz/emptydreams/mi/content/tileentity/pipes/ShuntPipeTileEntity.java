package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
		List<EnumFacing> all = calculatePossibleSide(linked);
		all.retainAll(getPossibleSides(facing));
		return !all.isEmpty();
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (world.isRemote) return false;
		if (cap.isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		setLinkedData(facing, true);
		side = calculateSide();
		return true;
	}
	
	@Override
	protected void setLinkedData(EnumFacing facing, boolean isLinked) {
		super.setLinkedData(facing, isLinked);
		if (isLinked) linked.add(facing);
		else linked.remove(facing);
	}
	
	protected EnumFacing calculateSide() {
		if (linked.size() == 1) {
			EnumFacing facing = linked.get(0);
			if (facing == UP || facing == DOWN) {
				if (side != UP && side != DOWN) return side;
				return NORTH;
			}
			if (calculatePossibleSide(facing).contains(side)) return side;
			return UP;
		}
		return calculatePossibleSide(linked).get(0);
	}
	
	protected static List<EnumFacing> calculatePossibleSide(Collection<EnumFacing> facings) {
		List<EnumFacing> all = Lists.newArrayList(EnumFacing.values());
		facings.stream().map(ShuntPipeTileEntity::getPossibleSides).forEach(all::retainAll);
		return all;
	}
	
	protected static List<EnumFacing> calculatePossibleSide(EnumFacing... facings) {
		List<EnumFacing> all = Lists.newArrayList(EnumFacing.values());
		for (EnumFacing value : facings) {
			all.retainAll(getPossibleSides(value));
		}
		return all;
	}
	
	private static final List<EnumFacing> SIDE_UP =
								Collections.unmodifiableList(Lists.newArrayList(HORIZONTALS));
	private static final List<EnumFacing> SIDE_WEST =
								Collections.unmodifiableList(Lists.newArrayList(UP, DOWN, SOUTH, NORTH));
	private static final List<EnumFacing> SIDE_NORTH =
								Collections.unmodifiableList(Lists.newArrayList(UP, DOWN, WEST, EAST));
	
	private static List<EnumFacing> getPossibleSides(EnumFacing facing) {
		switch (facing) {
			case DOWN: case UP: return SIDE_UP;
			case NORTH: case SOUTH: return SIDE_NORTH;
			default: return SIDE_WEST;
		}
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