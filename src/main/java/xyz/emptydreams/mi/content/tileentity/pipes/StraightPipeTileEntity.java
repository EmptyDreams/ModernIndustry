package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.fluid.TransportResult;
import xyz.emptydreams.mi.api.fluid.data.FluidData;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public TransportResult extract(int amount, EnumFacing facing, boolean simulate) {
		TransportResult result = new TransportResult();
		if (!isOpen(facing)) return result;     //如果插入方向上不能通过流体则直接退出
		return result;
	}
	
	@Override
	public TransportResult insert(FluidData data, EnumFacing facing, boolean simulate) {
		TransportResult result = new TransportResult();
		if (!isOpen(facing)) return result;     //如果插入方向上不能通过流体则直接退出
		
		return result;
	}
	
	@Override
	public List<EnumFacing> next(EnumFacing source) {
		EnumFacing side = facing.getOpposite();
		if (source == facing) return hasPlug(facing) ? Collections.emptyList() : Lists.newArrayList(side);
		if (source == side) return hasPlug(side) ? Collections.emptyList() : Lists.newArrayList(facing);
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
		return hasAperture(facing) || linkData == 0;
	}
	
	@Override
	public boolean link(EnumFacing facing) {
		if (isLinked(facing)) return true;
		if (!canLink(facing)) return false;
		if (linkData == 0) setFacing(facing);
		setLinkedData(facing, true);
		return true;
	}
	
	/**
	 * 获取指定方向上连接的方块
	 * @param facing 指定方向
	 * @return 没有连接则返回null
	 */
	@Nullable
	public TileEntity getNext(EnumFacing facing) {
		if (isLinked(facing)) {
			return world.getTileEntity(pos.offset(facing));
		} else {
			return null;
		}
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