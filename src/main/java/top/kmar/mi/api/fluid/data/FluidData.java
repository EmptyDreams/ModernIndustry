package top.kmar.mi.api.fluid.data;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 存储管道内的流体数据
 * @author EmptyDreams
 */
public class FluidData {
	
	/** 获取一个空的对象 */
	@Nonnull
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
		return getFluid() == null;
	}
	
	/** 判断是否为空 */
	public boolean isEmpty() {
		return getAmount() == 0 || isAir();
	}
	
	/** 增加流体量 */
	public void plusAmount(int amount) {
		this.amount += amount;
	}
	
	/**
	 * <p>增加流体量
	 * <p>与{@link #plusAmount(int)}不同的是在当前流体为空时该方法会更改流体类型
	 * <p>调用该方法的地方必须保证插入的流体类型与当前流体类型相匹配（{@link #matchFluid(FluidData)}返回true）
	 * <p>例如：
	 * <pre>{@code
	 *     FluidData in0 = new FluidData(WATER, 1000);
	 *     FluidData in1 = new FluidData(null, 1000);
	 *     FluidData in2 = new FluidData(FluidIron.blockInstance(), 1000);
	 *     FluidData data = FluidData.empty();
	 *     data.plus(in0); //合法
	 *     data.plus(in1); //合法
	 *     data.plus(in2); //非法
	 * }</pre>
	 */
	public void plus(FluidData data) {
		if (getAmount() == 0) fluid = data.fluid;
		amount += data.getAmount();
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
	
	/** 判断当前数据的流体类型是否为指定流体 */
	public boolean matchFluid(Fluid fluid) {
		return getFluid() == fluid;
	}
	
	/**
	 * <p>判断当前数据与指定数据的流体类型是否相同
	 * <p>特别的，如果{@code this.isEmpty() || data.isEmpty()}为true的话会返回true
	 * 例如：
	 * <pre>{@code
	 *     FluidData arg0 = new FluidData(null, 100);
	 *     FluidData arg1 = new FluidData(WATER, 100);
	 *     FluidData arg2 = new FluidData(WATER, 50);
	 *     FluidData arg3 = new FluidData(FluidIron.blockInstance(), 100);
	 *     arg0.matchFluid(arg1);   //true
	 *     arg0.matchFluid(arg2);   //true
	 *     arg1.matchFluid(arg2);   //true
	 *     arg1.matchFluid(arg3);   //false
	 * }</pre>
	 */
	public boolean matchFluid(FluidData data) {
		if (isEmpty() || data.isEmpty()) return true;
		return getFluid() == data.getFluid();
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
	
	/**
	 * 构建一个FluidStack
	 * @return 如果isAir()返回true则FluidStack.getFluid()将返回WATER
	 */
	@Nonnull
	public FluidStack toStack() {
		return new FluidStack(isAir() ? FluidRegistry.WATER : fluid, amount);
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