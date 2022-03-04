package top.kmar.mi.api.electricity.info;

import top.kmar.mi.api.electricity.interfaces.IVoltage;
import top.kmar.mi.data.info.EnumVoltage;

/**
 * 电压范围
 * @author EmptyDreams
 */
public final class VoltageRange {
	
	/** 全范围的电压 */
	public static final VoltageRange ALL = new VoltageRange(EnumVoltage.NON, EnumVoltage.MAX);
	
	public static VoltageRange instance(IVoltage voltage) {
		return new VoltageRange(voltage, voltage);
	}
	
	public final IVoltage min;
	public final IVoltage max;
	
	public VoltageRange(IVoltage min, IVoltage max) {
		this.min = min;
		this.max = max;
	}
	
	/** 判断指定电压是否在范围内 */
	public boolean contains(IVoltage voltage) {
		return min.compareTo(voltage) <= 0 && max.compareTo(voltage) >= 0;
	}
	
	/** 获取最适电压 */
	public IVoltage getOptimalVoltage(EnergyRange range) {
		IVoltage voltage = range.getOptimalVoltage(min);
		if (contains(voltage)) return voltage;
		return range.getOptimalVoltage(max);
	}
	
}