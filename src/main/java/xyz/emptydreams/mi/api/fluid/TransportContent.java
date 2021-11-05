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
	
	/**
	 * 尝试移除指定流体数据
	 * @param data 要移除的流体数据
	 * @return 成功移除的量
	 */
	public int remove(FluidData data) {
		int amount = data.getAmount();
		Iterator<FluidData> iterator = content.iterator();
		while (amount != 0 && iterator.hasNext()) {
			FluidData it = iterator.next();
			if (it.getFluid() != data.getFluid()) continue;
			if (amount >= it.getAmount()) {
				amount -= it.getAmount();
				iterator.remove();
			} else {
				it.minusAmount(amount);
				amount = 0;
			}
		}
		return data.getAmount() - amount;
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
	
	/** 判断是否为空 */
	public boolean isEmpty() {
		return content.isEmpty();
	}
	
	/** 深度复制对象 */
	public TransportContent copy() {
		TransportContent result = new TransportContent();
		this.content.forEach(it -> result.content.add(it.copy()));
		result.amount = this.amount;
		return result;
	}
	
	@Override
	public Iterator<FluidData> iterator() {
		return content.iterator();
	}
	
}