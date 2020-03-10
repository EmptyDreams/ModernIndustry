package minedreams.mi.api.electricity.src.info;

/**
 * 机器过载操作
 *
 * @author EmptyDreams
 * @version V1.0
 */
public enum EnumBiggerVoltage {
	
	/** 什么都不做 */
	NON(0),
	/** 爆炸 */
	BOOM(1),
	/** 引起火灾 */
	FIRE(2);
	
	private final int INDEX;
	
	EnumBiggerVoltage(int i) {
		INDEX = i;
	}
	
	public final int getIndex() {
		return INDEX;
	}
	
	public static EnumBiggerVoltage getFront(int index) {
		return values()[index];
	}
	
}
