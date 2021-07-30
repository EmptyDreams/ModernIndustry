package xyz.emptydreams.mi.api.capabilities.fluid;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

/**
 * 流体管道信息
 * @author EmptyDreams
 */
public interface IFluid {
	
	/** 一格管道可以容纳的最大流体量 */
	int FLUID_TRANSFER_MAX_AMOUNT = 1000;
	
	/** 获取流体容量（单位：mB） */
	int fluidAmount();
	
	/** 获取所包含的流体类型 */
	@Nullable
	Fluid fluid();
	
	/** 设置流体 */
	void setFluid(@Nullable FluidStack stack);
	
	/**
	 * 取出指定数额的流体
	 * @param stack 输出的流体
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实取出的流体量
	 */
	int extract(FluidStack stack, boolean simulate);
	
	/**
	 * 放入指定数额的流体
	 * @param stack 输入的流体
	 * @param facing 流体输入的方向在方块的方向(如果方向不确定或simulate为true则可以为null)
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实取出的流体量
	 */
	int insert(FluidStack stack, EnumFacing facing, boolean simulate);
	
	/** 设置管道朝向 */
	void setFacing(EnumFacing facing);
	
	/** 设置流体来源方向 */
	void setSource(EnumFacing facing);
	
	/** 获取管道朝向 */
	EnumFacing getFacing();
	
	/** 获取流体管道最大流量 */
	int getMaxAmount();
	
	/**
	 * 获取指定方向上连接的方块
	 * @param facing 方向
	 * @return 如果没有连接则返回null，若输入为null则返回本身
	 */
	@Nullable
	IFluid getLinkedTransfer(EnumFacing facing);
	
	/**
	 * 获取下一个可用的流体去向
	 * @throws IllegalArgumentException 如果来源没有与管道连接
	 * @return 返回结果无序
	 */
	@Nonnull
	List<EnumFacing> next();
	
	/** 判断指定方向是否含有开口 */
	boolean hasAperture(EnumFacing facing);
	
	/** 判断是否可以连接指定方向 */
	boolean canLink(EnumFacing facing);
	
	/** 判断指定方向上是否可以设置管塞 */
	default boolean canSetPlug(EnumFacing facing) {
		return hasPlug(facing) && !isLinked(facing);
	}
	
	/** 获取管道已经连接的数量 */
	default int getLinkAmount() {
		int result = 0;
		for (EnumFacing value : values()) {
			if (isLinked(value)) ++result;
		}
		return result;
	}
	
	/**
	 * 连接指定方向上的设备
	 * @param facing 方向
	 * @return 连接是否成功，若该方向原本已经有连接则返回true
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
			default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
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
	
	/**
	 * 在指定方向上设置管塞
	 * @param plug 管塞物品对象
	 * @param facing 方向
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	default boolean setPlug(EnumFacing facing, Item plug) {
		switch (facing) {
			case DOWN: return setPlugDown(plug);
			case UP: return setPlugUp(plug);
			case NORTH: return setPlugNorth(plug);
			case SOUTH: return setPlugSouth(plug);
			case WEST: return setPlugWest(plug);
			case EAST: return setPlugEast(plug);
			default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
		}
	}
	/**
	 * 在管道上方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugUp(Item plug);
	/**
	 * 在管道下方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugDown(Item plug);
	/**
	 * 在管道北方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugNorth(Item plug);
	/**
	 *在管道南方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugSouth(Item plug);
	/**
	 * 在管道西方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugWest(Item plug);
	/**
	 * 在管道东方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugEast(Item plug);
	
	default boolean hasPlug(EnumFacing facing) {
		switch (facing) {
			case DOWN: return hasPlugDown();
			case UP: return hasPlugUp();
			case NORTH: return hasPlugNorth();
			case SOUTH: return hasPlugSouth();
			case WEST: return hasPlugWest();
			case EAST: return hasPlugEast();
			default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
		}
	}
	/** 判断管道上方是否含有管塞 */
	boolean hasPlugUp();
	/** 判断管道下方是否含有管塞 */
	boolean hasPlugDown();
	/** 判断管道北方是否含有管塞 */
	boolean hasPlugNorth();
	/** 判断管道南方是否含有管塞 */
	boolean hasPlugSouth();
	/** 判断管道西方是否含有管塞 */
	boolean hasPlugWest();
	/** 判断管道东方是否含有管塞 */
	boolean hasPlugEast();
	
	/** 向下运输一格 */
	@Nullable
	default FluidStack transportDown(int amount, boolean simulate) {
		return transport(DOWN, amount, simulate);
	}
	/** 向上运输一格 */
	@Nullable
	default FluidStack transportUp(int amount, boolean simulate) {
		return transport(UP, amount, simulate);
	}
	/** 向东运输一格 */
	@Nullable
	default FluidStack transportEast(int amount, boolean simulate) {
		return transport(EAST, amount, simulate);
	}
	/** 向西运输一格 */
	@Nullable
	default FluidStack transportWest(int amount, boolean simulate) {
		return transport(WEST, amount, simulate);
	}
	/** 向北运输一格 */
	@Nullable
	default FluidStack transportNorth(int amount, boolean simulate) {
		return transport(NORTH, amount, simulate);
	}
	/** 向南运输一格 */
	@Nullable
	default FluidStack transportSouth(int amount, boolean simulate) {
		return transport(SOUTH, amount, simulate);
	}
	/**
	 * 向指定方向运输一格
	 * @param facing 方向
	 * @param amount 运输量(mB)
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实运输的流体
	 */
	@Nullable
	default FluidStack transport(EnumFacing facing, int amount, boolean simulate) {
		Fluid fluid = fluid();
		if (fluid == null) return null;
		FluidStack out = new FluidStack(fluid, Math.min(amount, FLUID_TRANSFER_MAX_AMOUNT));
		out.amount = extract(out, true);
		if (out.amount == 0) return null;
		IFluid that = getLinkedTransfer(facing);
		if (that == null) return null;
		int realIn = that.insert(out, null, true);
		out.amount = Math.min(out.amount, realIn);
		if (out.amount == 0) return null;
		if (!simulate) {
			extract(out, false);
			that.insert(out, facing.getOpposite(), false);
		}
		return out;
	}
	
}