package xyz.emptydreams.mi.content.tileentity.pipes.data;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

/**
 * 存储管道内的流体数据
 * @author EmptyDreams
 */
public class FluidData {
	
	/** 流体种类。null表示为空气 */
	private final Fluid fluid;
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
		return amount == 0;
	}
	
	/** 增加流体量 */
	public void plusAmount(int amount) {
		this.amount += amount;
	}
	
	/** 设置流体量 */
	public void setAmount(int amount) { this.amount = amount; }
	
	/** 获取流体数量 */
	public int getAmount() {
		return amount;
	}
	
	/** 获取流体对象 */
	public Fluid getFluid() {
		return fluid;
	}
	
	/** 复制当前对象 */
	public FluidData copy() {
		return new FluidData(fluid, amount);
	}
	
	public FluidStack toStack() {
		return new FluidStack(fluid, amount);
	}
	
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
		int result = fluid != null ? fluid.hashCode() : 0;
		result = 31 * result + amount;
		return result;
	}
	
}