package top.kmar.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.fluid.FTTileEntity;
import top.kmar.mi.api.utils.data.io.Storage;
import top.kmar.mi.api.register.others.AutoTileEntity;

import java.util.List;

import static net.minecraft.util.EnumFacing.*;

/**
 * 十字管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("ShuntPipe")
public class ShuntPipeTileEntity extends FTTileEntity {
	
	/** 管道侧面面对的方向 */
	@Storage
    protected Axis side = Axis.Y;
	
	@Override
	protected void sync(IDataWriter writer) {
		writer.writeByte((byte) side.ordinal());
	}
	
	@Override
	protected void syncClient(IDataReader reader) {
		side = Axis.values()[reader.readByte()];
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing.getAxis() != side;
	}
	
	@Override
	public boolean canLinkFluid(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!super.canLinkFluid(facing)) return false;
		if (hasAperture(facing)) return true;
		if (getLinkData().isInit()) return true;
		List<Axis> all = Lists.newArrayList(Axis.values());
		all.remove(facing.getAxis());
		for (EnumFacing value : values()) {
			if (isLinked(value)) all.remove(value.getAxis());
		}
		return !all.isEmpty();
	}
	
	@Override
	public boolean linkFluid(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!super.linkFluid(facing)) return false;
		side = calculateSide();
		updateBlockState(false);
		return true;
	}
	
	/** 计算side应该在哪个方向 */
	protected Axis calculateSide() {
		List<Axis> all = Lists.newArrayList(Axis.values());
		for (EnumFacing value : values()) {
			if (isLinked(value)) all.remove(value.getAxis());
		}
		if (all.contains(side)) return side;
		return all.get(0);
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