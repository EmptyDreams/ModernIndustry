package xyz.emptydreams.mi.api.fluid.capabilities;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.*;

/**
 * 流体管道信息
 * @author EmptyDreams
 */
public interface IFluidTransfer {
	
	/** 获取流体容量（单位：mB） */
	int fluidAmount();
	
	/** 获取所包含的流体类型 */
	@Nullable
	Fluid fluid();
	
	/** 设置流体 */
	void setFluid(@Nullable FluidStack stack);
	
	/**
	 * 取出指定数额的流体
	 * @param amount 流体量(mB)
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实取出的流体量
	 */
	int extract(int amount, boolean simulate);
	
	/**
	 * 放入指定数额的流体
	 * @param amount 流体量(mB)
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实取出的流体量
	 */
	int insert(int amount, boolean simulate);
	
	/** 获取流体管道最大流量 */
	int getMaxAmount();
	
	/**
	 * 获取指定方向上连接的流体管道
	 * @param facing 方向
	 * @return 如果没有连接则返回null，若输入为null则返回本身
	 */
	@Nullable
	IFluidTransfer getLinkedTransfer(EnumFacing facing);
	
	/**
	 * 连接指定方向上的设备
	 * @param facing 方向
	 * @return 连接是否成功，若该方向原本已经有连接则返回false
	 */
	boolean link(EnumFacing facing);
	
	/**
	 * 断开与指定方向上的设备的连接
	 * @param facing 方向
	 */
	void unlink(EnumFacing facing);
	
	/** 是否连接指定方向 */
	default boolean isLinked(EnumFacing facing) {
		switch (facing) {
			case DOWN: return isLinkedDown();
			case UP: return isLinkedUp();
			case NORTH: return isLinkedNorth();
			case SOUTH: return isLinkedSouth();
			case WEST: return isLinkedWest();
			case EAST: return isLinkedEast();
			default: throw new IllegalArgumentException("输入了未知的方向：" + facing.getName());
		}
	}
	/** 是否连接上方 */
	boolean isLinkedUp();
	/** 是否连接下方 */
	boolean isLinkedDown();
	/** 是否连接东方 */
	boolean isLinkedEast();
	/** 是否连接西方 */
	boolean isLinkedWest();
	/** 是否连接南方 */
	boolean isLinkedSouth();
	/** 是否连接北方 */
	boolean isLinkedNorth();
	
	/** 向下运输一格 */
	default int transportDown(int amount, boolean simulate) {
		return transport(DOWN, amount, simulate);
	}
	/** 向上运输一格 */
	default int transportUp(int amount, boolean simulate) {
		return transport(UP, amount, simulate);
	}
	/** 向东运输一格 */
	default int transportEast(int amount, boolean simulate) {
		return transport(EAST, amount, simulate);
	}
	/** 向西运输一格 */
	default int transportWest(int amount, boolean simulate) {
		return transport(WEST, amount, simulate);
	}
	/** 向北运输一格 */
	default int transportNorth(int amount, boolean simulate) {
		return transport(NORTH, amount, simulate);
	}
	/** 向南运输一格 */
	default int transportSouth(int amount, boolean simulate) {
		return transport(SOUTH, amount, simulate);
	}
	/**
	 * 向指定方向运输一格
	 * @param facing 方向
	 * @param amount 运输量(mB)
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实运输量(mB)
	 */
	default int transport(EnumFacing facing, int amount, boolean simulate) {
		int realOut = extract(amount, true);
		if (realOut == 0) return 0;
		IFluidTransfer that = getLinkedTransfer(facing);
		if (that == null) return 0;
		int realIn = that.insert(realOut, true);
		int real = Math.min(realOut, realIn);
		if (real == 0) return 0;
		if (!simulate) {
			extract(real, false);
			that.insert(real, false);
		}
		return real;
	}
	
}