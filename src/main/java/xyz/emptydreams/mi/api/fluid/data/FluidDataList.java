package xyz.emptydreams.mi.api.fluid.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 使用链表的流体集合
 * @author EmptyDreams
 */
public class FluidDataList implements Iterable<FluidData> {
	
	private final List<FluidData> list = new LinkedList<>();
	private int amount = 0;
	
	/**
	 * 想列表中添加一个流体数据，若列表中已经存在流体类型相同的数据则优先合并两个数据
	 * @param data 要添加的数据（内部拷贝）
	 */
	public void add(FluidData data) {
		for (FluidData fluidData : list) {
			if (fluidData.matchFluid(data)) {
				fluidData.plusAmount(data.getAmount());
				return;
			}
		}
		amount += data.getAmount();
		MonitoredFluidData element = new MonitoredFluidData(data);
		element.registryAmountMonitor((amount, edit, it) -> editAmount(edit));
		list.add(element);
	}
	
	/** 获取流体总量 */
	public int getAmount() {
		return amount;
	}
	
	/** 判断是否为空 */
	public boolean isEmpty() {
		return getAmount() == 0;
	}
	
	private void editAmount(int amount) {
		this.amount += amount;
	}
	
	@Override
	public Iterator<FluidData> iterator() {
		return list.iterator();
	}
	
}