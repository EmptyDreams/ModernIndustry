package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 直线型管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("StraightPipe")
public class StraightPipeTileEntity extends FTTileEntity {
	
	/** 管道朝向 */
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
	public List<EnumFacing> next(EnumFacing source) {
		EnumFacing side = facing.getOpposite();
		if (source == facing) return hasPlug(facing) ?
				Collections.emptyList() : Lists.newArrayList(side);
		if (source == side) return hasPlug(side) ?
				Collections.emptyList() : Lists.newArrayList(facing);
		if (source == null) {
			List<EnumFacing> result = new ArrayList<>(2);
			if (!hasPlug(facing)) result.add(facing);
			if (!hasPlug(side)) result.add(side);
			return result;
		}
		throw new IllegalArgumentException("输入了没有开口的方向：" + source);
	}
	
	@Override
	public boolean hasAperture(EnumFacing facing) {
		return facing.getAxis() == this.facing.getAxis();
	}
	
	@Override
	public boolean canLink(EnumFacing facing) {
		return hasAperture(facing) || linkData.isInit();
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		if (linkData.isInit()) setFacing(facing);
		linkData.set(facing, true);
		updateBlockState(false);
		return true;
	}
	
	@Override
	public int getLinkedAmount() {
		if (isLinked(getFacing().getOpposite())) return isLinked(getFacing()) ? 2 : 1;
		else return isLinked(getFacing()) ? 1 : 0;
	}
	
	/** 设置管道正方向 */
	public void setFacing(EnumFacing facing) {
		//加一个判断是为了防止管道连接时方向倒转导致内容反转
		if (facing.getAxis() != this.facing.getAxis()) this.facing = facing;
	}
	
	/** 获取管道正方向 */
	public EnumFacing getFacing() {
		return facing;
	}
	
}