package minedreams.mi.api.electricity.info;

import java.util.Comparator;

/**
 * 存储支持的电压等级
 *
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
public enum EnumVoltage {

	NON(0, Integer.MAX_VALUE, 0),
	LOW(10, 50, 1),
	LOWER(50, 45, 2),
	ORDINARY(100, 34, 3),
	HIGHER(200, 27, 4),
	HIGH(500, 20, 5),
	VERY_HIGH(2000, 10, 6),
	EXTREMELY_HIGHER(5000, 5, 7),
	SUPERCONDUCTOR(50000, 0, 8);
	
	private final int VOLTAGE;
	private final int LOSS;
	private final int INDEX;

	EnumVoltage(int voltage, int loss, int index) {
		VOLTAGE = voltage;
		LOSS = loss;
		INDEX = index;
	}
	
	public int getVoltage() {
		return VOLTAGE;
	}
	
	public int getLossIndex() {
		return LOSS;
	}
	
	public int getIndex() { return INDEX; }
	
	/**
	 * 根据下标获取值
	 * @param index 下标
	 *
	 * @throws IndexOutOfBoundsException 如果 index 超出范围
	 */
	public static EnumVoltage valueOf(int index) {
		return values()[index];
	}
	
}
