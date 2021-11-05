package xyz.emptydreams.mi.api.fluid;

import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.fluid.data.FluidData;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于表示运输运算的结果
 * @author EmptyDreams
 */
public final class TransportResult {
	
	private final Map<EnumFacing, Manager> dataMap = new EnumMap<>(EnumFacing.class);
	
	private int realTransport = 0;
	
	/**
	 * 添加流体信息到管理器
	 * @param facing 流体运输方向
	 * @param data 流体数据
	 */
	public void add(EnumFacing facing, FluidData data) {
		dataMap.computeIfAbsent(facing, key -> new Manager()).add(data.copy());
	}
	
	/**
	 * 添加流体信息到管理器
	 * @param facing 流体运输方向
	 * @param content 流体数据
	 */
	public void add(EnumFacing facing, TransportContent content) {
		Manager manager = dataMap.computeIfAbsent(facing, key -> new Manager());
		for (FluidData fluidData : content) {
			manager.add(fluidData);
		}
	}
	
	/** 设置流体前进总量 */
	public void setRealTransport(int amount) {
		realTransport = amount;
	}
	
	/** 增加流体前进总量 */
	public void plusRealTransport(int plus) {
		realTransport += plus;
	}
	
	/** 获取流体运输总量 */
	public int getRealTransport() {
		return realTransport;
	}
	
	private static final class Manager implements Iterable<FluidData> {
	
		private final List<FluidData> fluidList = new LinkedList<>();
	
		private void add(FluidData data) {
			for (FluidData fluidData : fluidList) {
				if (fluidData.matchFluid(data)) {
					fluidData.plusAmount(data.getAmount());
					return;
				}
			}
			fluidList.add(data);
		}
		
		@Override
		public Iterator<FluidData> iterator() {
			return fluidList.iterator();
		}
		
	}
	
}