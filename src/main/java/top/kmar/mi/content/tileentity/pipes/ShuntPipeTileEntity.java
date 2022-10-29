package top.kmar.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.fluid.FTTileEntity;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;

import java.util.List;

import static net.minecraft.util.EnumFacing.*;

/**
 * 十字管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("ShuntPipe")
public class ShuntPipeTileEntity extends FTTileEntity {
	
	/** 管道侧面面对的方向 */
	@AutoSave
    protected Axis side = Axis.Y;
	
	@Override
	protected NBTBase sync() {
		return new NBTTagByte((byte) side.ordinal());
	}
	
	@Override
	protected void syncClient(NBTBase reader) {
		side = Axis.values()[((NBTTagByte) reader).getInt()];
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
	
}