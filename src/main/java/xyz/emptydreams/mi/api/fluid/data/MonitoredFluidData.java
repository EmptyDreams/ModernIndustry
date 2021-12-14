package xyz.emptydreams.mi.api.fluid.data;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import xyz.emptydreams.mi.api.interfaces.IntIntObjConsumer;
import xyz.emptydreams.mi.api.interfaces.ThConsumer;

import javax.annotation.Nonnull;

/**
 * 被监视的FluidData，当内部数据发生变动时会触发相应的方法
 * @author EmptyDreams
 */
public class MonitoredFluidData extends FluidData {
	
	/** 流体数量发生变动时的监视器，第一个参数为原数量，第二个参数是修改的数量，第三个参数是发生变动的类 */
	private IntIntObjConsumer<FluidData> amountEdit = null;
	/** 流体种类发生变动时的监视器，第一个参数为原流体，第二个参数是变动后的流体，第三个参数是发生变动的类 */
	private ThConsumer<Fluid, Fluid, FluidData> fluidEdit = null;
	
	public MonitoredFluidData(FluidData data) {
		super(data.getFluid(), data.getAmount());
	}
	
	public MonitoredFluidData(Fluid fluid, int amount) {
		super(fluid, amount);
	}
	
	public MonitoredFluidData(FluidStack stack) {
		super(stack);
	}
	
	/** 注册流体种类变动监视器 */
	@SuppressWarnings("unused")
	public MonitoredFluidData registryFluidMonitor(ThConsumer<Fluid, Fluid, FluidData> monitor) {
		fluidEdit = monitor;
		return this;
	}
	
	/** 注册数量变动监视器 */
	@SuppressWarnings("UnusedReturnValue")
	public MonitoredFluidData registryAmountMonitor(IntIntObjConsumer<FluidData> monitor) {
		amountEdit = monitor;
		return this;
	}
	
	@Override
	public void plusAmount(int amount) {
		if (amountEdit != null) amountEdit.accept(getAmount(), amount, this);
		super.plusAmount(amount);
	}
	
	@Override
	public void minusAmount(int amount) {
		if (amountEdit != null) amountEdit.accept(getAmount(), -amount, this);
		super.minusAmount(amount);
	}
	
	@Override
	public void setEmpty() {
		if (amountEdit != null) amountEdit.accept(getAmount(), -getAmount(), this);
		if (fluidEdit != null) fluidEdit.accept(getFluid(), null, this);
		super.setEmpty();
	}
	
	@Override
	public void setAmount(int amount) {
		if (amountEdit != null) amountEdit.accept(getAmount(), amount - getAmount(), this);
		super.setAmount(amount);
	}
	
	/** 返回一个移除了监视功能的FluidData */
	@Nonnull
	public FluidData removeMonitor() {
		return new FluidData(getFluid(), getAmount());
	}
	
}