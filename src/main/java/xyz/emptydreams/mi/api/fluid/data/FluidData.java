package xyz.emptydreams.mi.api.fluid.data;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 存储管道内的流体数据
 * @author EmptyDreams
 */
public class FluidData {
	
	/** 获取一个空的数据 */
	public static FluidData empty() {
		return new FluidData(null, 0);
	}
	
	/** 流体种类。null表示为空气 */
	private Fluid fluid;
	/** 流体数量 */
	private int amount;
	
	public FluidData(FluidStack stack) {
		this(stack.getFluid(), stack.amount);
	}
	
	public FluidData(Fluid fluid, int amount) {
		this.fluid = fluid;
		this.amount = amount;
	}
	
	/** 判断是否为空气 */
	public boolean isAir() {
		return fluid == null;
	}
	
	/** 判断是否为空 */
	public boolean isEmpty() {
		return amount == 0 || isAir();
	}
	
	/** 增加流体量 */
	public void plusAmount(int amount) {
		this.amount += amount;
	}
	
	/** 减少流体的量 */
	public void minusAmount(int amount) {
		this.amount -= amount;
	}
	
	/** 设置当前数据为空值 */
	public void setEmpty() {
		amount = 0;
		fluid = null;
	}
	
	/** 设置流体量 */
	public void setAmount(int amount) { this.amount = amount; }
	
	/** 获取流体数量 */
	public int getAmount() {
		return amount;
	}
	
	/** 获取流体对象 */
	@Nullable
	public Fluid getFluid() {
		return fluid;
	}
	
	/** 复制当前对象 */
	@Nonnull
	public FluidData copy() {
		return new FluidData(fluid, amount);
	}
	
	/**
	 * 复制当前对象并修改复制后对象的流体量
	 * @param amount 流体量
	 */
	@Nonnull
	public FluidData copy(int amount) {
		return new FluidData(fluid, amount);
	}
	
	@Nonnull
	public FluidStack toStack() {
		return new FluidStack(fluid, amount);
	}
	
	@Override
	@Nonnull
	public String toString() {
		return "fluid=" + (isAir() ? "air" : fluid.getBlock().getRegistryName()) + ", amount=" + amount;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		FluidData node = (FluidData) o;
		if (amount != node.amount) return false;
		return Objects.equals(fluid, node.fluid);
	}
	
	@Override
	public int hashCode() {
		int result = fluid != null ? fluid.hashCode() : 31;
		return (31 * result + amount);
	}
	
}