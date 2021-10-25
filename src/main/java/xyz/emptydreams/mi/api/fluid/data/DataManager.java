package xyz.emptydreams.mi.api.fluid.data;

import net.minecraftforge.fluids.Fluid;
import xyz.emptydreams.mi.api.fluid.TransportContent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 流体数据管理器
 * @author EmptyDreams
 */
public class DataManager {
	
	/** 翻转指定数据管理器的正方向 */
	@Nonnull
	public static DataManager opposite(DataManager manager) {
		return new DataManager(manager.getMax()) {
			@Override
			public TransportContent insert(FluidData data, boolean isPositive, boolean simulate) {
				return super.insert(data, !isPositive, simulate);
			}
			
			@Nonnull
			@Override
			public FluidData extract(int amount, boolean isPositive, boolean simulate) {
				return super.extract(amount, !isPositive, simulate);
			}
			
			@Override
			public int getVoidSpace(boolean isPositive) {
				return super.getVoidSpace(!isPositive);
			}
		};
	}
	
	/** 标记流体起点 */
	protected int index = 0;
	/** 标记存储的内容 */
	protected FluidData data;
	/** 最大值 */
	protected final int max;
	
	/**
	 * @param max 最大容量
	 */
	public DataManager(int max) {
		this.max = max;
		data = new FluidData(null, 0);
	}
	
	/**
	 * 从指定方向插入数据
	 * @param data 要插入的流体数据，当{@code data.isAir()}返回true时仅移动内部数据
	 * @param isPositive 是否在正方向上进行操作
	 * @param simulate 是否为模拟，为true时不改变内部数据
	 * @return 被挤出的流体数据
	 */
	public TransportContent insert(FluidData data, boolean isPositive, boolean simulate) {
		TransportContent result = new TransportContent();
		if (this.data.isAir()) {
			result.add(cutData(data.copy(), 0, simulate));
			return result;
		}
		if (data.isAir() || data.getFluid() != this.data.getFluid()) {
			int index = this.index;
			if (isPositive) index += data.getAmount();
			else index -= data.getAmount();
			int old = this.data.getAmount();
			FluidData out = cutData(this.data, index, simulate);
			result.plusTransportAmount(out.getAmount());
			if (out.getAmount() == old) result.combine(insert(data, isPositive, simulate));
			result.add(cutData(this.data, this.index, simulate));
			return result;
		}
		this.data.plusAmount(data.getAmount());
		if (index > data.getAmount()) {
			index = (index - data.getAmount()) / 2;
			result.plusTransportAmount(index);
		}
		result.plusTransportAmount(data.getAmount());
		return result;
	}
	
	/**
	 * 从指定方向上取出数据
	 * @param amount 数据数量
	 * @param isPositive 是否在正方向上进行操作
	 * @param simulate 是否为模拟，为true时不改变内部数据
	 * @return 取出的数据
	 */
	@Nonnull
	public FluidData extract(int amount, boolean isPositive, boolean simulate) {
		if (isEmpty()) return FluidData.empty();
		index -= isPositive ? amount : -amount;
		return cutData(data, index, simulate);
	}
	
	/**
	 * 删除多余的数据
	 * @return 被删除的数据
	 */
	protected FluidData cutData(FluidData data, int index, boolean simulate) {
		FluidData old = data.copy();
		if (index < 0) {
			data.minusAmount(index);
			if (data.getAmount() <= 0) data.setEmpty();
			if (!simulate) this.index = 0;
		} else if (index >= getMax()) {
			data.setEmpty();
			if (!simulate) this.index = 0;
		} else if (data.getAmount() + index > getMax()) {
			data.setAmount(getMax() - index);
		}
		old.minusAmount(data.getAmount());
		if (!simulate) {
			this.data = data;
			this.index = index;
		}
		return old;
	}
	
	/** 获取所有的空闲空间 */
	public int getVoidSpace() {
		return getMax() - getFluidAmount();
	}
	
	/** 获取指定方向上的空闲空间 */
	public int getVoidSpace(boolean isPositive) {
		if (data.isEmpty()) return getMax();
		return isPositive ? index : (getMax() - index - data.getAmount());
	}
	
	/** 获取存储的流体量 */
	public int getFluidAmount() {
		return data.isAir() ? 0 : data.getAmount();
	}
	
	/**
	 * 获取内部存储的流体
	 * @return 为null表示没有存储流体
	 */
	@Nullable
	public Fluid getFluid() {
		return data.getFluid();
	}
	
	/** 判断容器是否填满 */
	public boolean isFull() {
		return getVoidSpace() == 0;
	}
	
	/** 判断数据是否只包含指定的流体 */
	public boolean isPure(Fluid fluid) {
		return isFull() && data.getFluid() == fluid;
	}
	
	/** 判断容器是否为空 */
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	/** 拷贝当前数据 */
	@Nonnull
	public DataManager copy() {
		DataManager result = new DataManager(getMax());
		result.index = index;
		result.data = data.copy();
		return result;
	}
	
	/** 获取最大容量 */
	public int getMax() {
		return max;
	}

}