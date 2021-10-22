package xyz.emptydreams.mi.api.fluid;

import net.minecraftforge.fluids.Fluid;
import xyz.emptydreams.mi.api.fluid.data.FluidData;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 存储运输内容
 * @author EmptyDreams
 */
public final class TransportContent implements Iterable<FluidData> {
	
	private final List<FluidData> content = new LinkedList<>();
	
	/** 存储运输的量 */
	private int amount = 0;
	
	/**
	 * 添加新的运输内容
	 * @param data 要添加的内容，内部进行保护性拷贝
	 */
	public void add(FluidData data) {
		if (data == null || data.isEmpty()) return;
		for (FluidData fluidData : content) {
			if (fluidData.getFluid() == data.getFluid()) {
				fluidData.plusAmount(data.getAmount());
				return;
			}
		}
		content.add(data.copy());
	}
	
	/** 增加运输量 */
	public void plusTransportAmount(int amount) {
		this.amount += amount;
	}
	
	/** 合并两个类的数据 */
	public TransportContent combine(TransportContent other) {
		if (other == null) return this;
		other.forEach(this::add);
		plusTransportAmount(other.getTransportAmount());
		return this;
	}
	
	/** 获取总量 */
	public int getAmount() {
		return content.stream().mapToInt(FluidData::getAmount).sum();
	}
	
	/** 获取指定流体的总量 */
	public int getAmount(Fluid fluid) {
		return content.stream().filter(
				data -> data.getFluid() == fluid).mapToInt(FluidData::getAmount).sum();
	}
	
	/** 获取运输量 */
	public int getTransportAmount() {
		return amount;
	}
	
	@Override
	public Iterator<FluidData> iterator() {
		return content.iterator();
	}
	
}