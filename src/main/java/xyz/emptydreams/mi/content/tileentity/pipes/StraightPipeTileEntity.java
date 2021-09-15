package xyz.emptydreams.mi.content.tileentity.pipes;

import com.google.common.collect.Lists;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import xyz.emptydreams.mi.api.capabilities.fluid.FluidCapability;
import xyz.emptydreams.mi.api.capabilities.fluid.IFluid;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.fluid.TransportResult;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.content.tileentity.pipes.data.DataManager;
import xyz.emptydreams.mi.content.tileentity.pipes.data.FluidData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 直线型管道的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("StraightPipe")
public class StraightPipeTileEntity extends FTTileEntity {
	
	/** 管道朝向 */
	@Storage protected EnumFacing facing;
	/** 管道存储内容，高位为管道正方向 */
	protected DataManager manager;
	
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
	
	@Nullable
	@Override
	protected DataManager getDataManager(EnumFacing facing) {
		return null;
	}
	
	@Override
	protected boolean matchFacing(EnumFacing facing) {
		return facing.getAxis() == this.facing.getAxis();
	}
	
	@Override
	public boolean isEmpty() {
		return manager.isEmpty();
	}
	
	/** 获取管道内的流体总量 */
	public int getAllAmount() {
		return manager.getBusySpace();
	}
	
	@Override
	public TransportResult extract(int amount, EnumFacing facing, boolean simulate) {
		if (!isOpen(facing)) return new TransportResult();  //如果插入方向上不能通过流体则直接退出
		EnumFacing side = facing.getOpposite();
		BlockPos next = pos.offset(side);
		StraightPipeTileEntity pre = this;
		TileEntity te = world.getTileEntity(next);
		while (te instanceof StraightPipeTileEntity) {  //找到最后一个直线管道
			pre = (StraightPipeTileEntity) te;
			te = world.getTileEntity(next);
			next = next.offset(side);
		}
		//如果当前方块不支持输出流体则直接从最后一个直线管道开始计算（对应下方两个if）
		if (te == null)
			return pre.insertHelp(new FluidData(null, amount), side, simulate, false);
		IFluid cap = te.getCapability(FluidCapability.TRANSFER, facing);
		if (cap == null)
			return pre.insertHelp(new FluidData(null, amount), side, simulate, false);
		//如果当前方块可以输出流体则将部分计算委托给这个方块
		TransportResult out = cap.extract(amount, facing, simulate);
		DataManager outManager = out.getFinal();
		if (outManager == null) //如果这个方块没有输出任何流体则从上一个直线管道开始计算
			return pre.insertHelp(new FluidData(null, amount), side, simulate, false);
		//将这个方块输出的流体读取出来
		LinkedList<FluidData> outData = outManager.extract(outManager.getBusySpace(), facing, true);
		TransportResult result = new TransportResult();
		for (FluidData data : outData) {    //将数据合并
			result.combine(pre.insertHelp(data, side, simulate, false));   //把取出操作转换为反方向的插入操作
		}
		return result;
	}
	
	@Override
	public TransportResult insert(FluidData data, EnumFacing facing, boolean simulate) {
		return insertHelp(data, facing, simulate, true);
	}
	
	protected TransportResult insertHelp(FluidData data, EnumFacing facing, boolean simulate, boolean isInput) {
		TransportResult result = new TransportResult();
		if (!isOpen(facing)) return result;  //如果插入方向上不能通过流体则直接退出
		LinkedList<FluidData> out = manager.insert(data, facing, true); //模拟流体插入获取数据
		BlockPos pre = pos.offset(facing);
		BlockPos next = getNextPos(pre);
		TileEntity te = world.getTileEntity(next);
		EnumFacing side = facing.getOpposite();
		int pastAmount = 0;     //通过的管道数
		if (out.size() == 1) {  //如果挤出来的流体只有一种则可以进行去迭代运算
			Fluid fluid = out.getFirst().getFluid();
			while (te instanceof StraightPipeTileEntity) {  //循环运算直到下一个方块不再是直线型管道
				StraightPipeTileEntity teSP = (StraightPipeTileEntity) te;
				if (!teSP.manager.isPure(fluid)) break;
				++pastAmount;
				pre = next;
				next = teSP.getNextPos(pre);
				te = world.getTileEntity(next);
			}
			result.getNode(fluid).plus(side, pastAmount * getMaxAmount());  //更新结果
		}
		//如果当前方块不能接收流体则尝试将流体排放到世界中（对应下方两个if）
		if (te == null)
			return putFluid2World(data, side, simulate, result, out, next, isInput, manager, this);
		IFluid cap = te.getCapability(FluidCapability.TRANSFER, null);
		if (cap == null)
			return putFluid2World(data, side, simulate, result, out, next, isInput, manager, this);
		//如果当前方块可以接收流体则将后续运算委托给这个方块
		TransportResult inner = cap.insert(data, facing, simulate);
		if ((!simulate) && inner.getNow() != 0) //更新数据
			manager.insert(new FluidData(data.getFluid(), inner.getNow()), facing, false);
		return result.combine(inner);   //合并并返回结果
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
	
	/** 获取连接的下一根管道 */
	public BlockPos getNextPos(BlockPos pre) {
		return pos.offset(WorldUtil.whatFacing(pre, pos));
	}
	
	/** 设置管道正方向 */
	public void setFacing(EnumFacing facing) {
		//加一个判断是为了防止管道连接时方向倒转导致内容反转
		if (facing != this.facing.getOpposite()) this.facing = facing;
	}
	
	/** 获取管道正方向 */
	public EnumFacing getFacing() {
		return facing;
	}
	
}