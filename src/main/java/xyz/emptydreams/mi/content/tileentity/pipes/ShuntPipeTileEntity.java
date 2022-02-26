package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.EnumFacing.*;

/**
 * 十字管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("ShuntPipe")
public class ShuntPipeTileEntity extends FTTileEntity {
	
	/** 管道侧面面对的方向 */
	@Storage protected Axis side = Axis.Y;
	protected List<EnumFacing> linked = new ArrayList<>(4);
	
	private static final Map<EnumFacing.Axis, EnumFacing[]> sideMap =
			MathUtil.createArrayMap(new Axis[]{ Axis.X, Axis.Y, Axis.Z },
					new EnumFacing[][]{
						new EnumFacing[] { UP, DOWN, SOUTH, NORTH },
						new EnumFacing[] { UP, DOWN, SOUTH, NORTH },
						new EnumFacing[] { NORTH, WEST, SOUTH, EAST }
					});
	
	public ShuntPipeTileEntity() {
		assert getMaxAmount() % 4 != 0;
		int nodeMax = getMaxAmount() / 4;
		
	}
	
	@Override
	protected void sync(IDataWriter writer) {
		writer.writeByte((byte) side.ordinal());
	}
	
	@Override
	protected void syncClient(IDataReader reader) {
		side = Axis.values()[reader.readByte()];
		if (linkData == 0 || !linked.isEmpty()) return;
		for (EnumFacing value : values()) {
			if (isLinked(value)) linked.add(value);
		}
	}
	
	@Override
	public List<EnumFacing> next(EnumFacing facing) {
		List<EnumFacing> result = Lists.newArrayList(getFacing());
		result.remove(facing);
		result.removeIf(this::hasPlug);
		return result;
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing.getAxis() != side;
	}
	
	@Override
	public boolean canLink(EnumFacing facing) {
		if (hasAperture(facing)) return true;
		if (linkData == 0) return true;
		List<EnumFacing> list = new LinkedList<>(linked);
		list.add(facing);
		Axis axis = calculatePossibleSide(list);
		return axis != null;
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		setLinkedData(facing, true);
		side = calculateSide();
		updateBlockState(false);
		return true;
	}
	
	@Override
	protected void setLinkedData(EnumFacing facing, boolean isLinked) {
		super.setLinkedData(facing, isLinked);
		if (isLinked) linked.add(facing);
		else linked.remove(facing);
		updateBlockState(false);
	}
	
	/** 计算side应该在哪个方向 */
	protected Axis calculateSide() {
		if (linked.size() == 1) {
			EnumFacing facing = linked.get(0);
			if (facing.getAxis() == Axis.Y) {
				if (side == Axis.Y) return side;
				return Axis.Z;
			}
			if (calculatePossibleSide(facing.getAxis()).contains(side)) return side;
			return Axis.Y;
		}
		Axis axis = calculatePossibleSide(linked);
		return axis == null ? Axis.Y : axis;
	}
	
	protected static Axis calculatePossibleSide(Collection<EnumFacing> facings) {
		List<Axis> all = Lists.newArrayList(Axis.values());
		for (EnumFacing facing : facings) all.remove(facing.getAxis());
		if (all.isEmpty()) return null;
		return all.get(0);
	}
	
	protected static List<Axis> calculatePossibleSide(Axis... axises) {
		List<Axis> all = Lists.newArrayList(Axis.values());
		for (Axis value : axises) all.retainAll(getPossibleSides(value));
		return all;
	}
	
	private static final List<Axis> SIDE_UP =
								Collections.unmodifiableList(Lists.newArrayList(Axis.X, Axis.Z));
	private static final List<Axis> SIDE_WEST =
								Collections.unmodifiableList(Lists.newArrayList(Axis.Y, Axis.Z));
	private static final List<Axis> SIDE_NORTH =
								Collections.unmodifiableList(Lists.newArrayList(Axis.Y, Axis.X));
	
	private static List<Axis> getPossibleSides(Axis axis) {
		switch (axis) {
			case Y: return SIDE_UP;
			case Z: return SIDE_NORTH;
			case X: return SIDE_WEST;
			default: throw new IllegalArgumentException("未知参数：axis=" + axis);
		}
	}
	
	public Axis getSide() {
		return side;
	}
	
	protected EnumFacing[] getFacing() {
		switch (side) {
			case Y: return HORIZONTALS;
			case Z: return new EnumFacing[] { UP, DOWN, WEST, EAST };
			case X: return new EnumFacing[] { UP, DOWN, NORTH, SOUTH };
			default: throw new IllegalArgumentException("未知的方向：" + side);
		}
	}
	
}