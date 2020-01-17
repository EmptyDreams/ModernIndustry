package minedreams.mi.api.electricity.info;

/**
 * 存储电压过大时操作
 * @author EmptyDremas
 * @version V1.0
 */
public final class BiggerVoltage {
	
	/** 效果影响半径，单位：方块 */
	public final int radius;
	public final EnumBiggerVoltage EBV;
	
	/**
	 * 影响半径默认为1，操作默认为爆炸
	 */
	public BiggerVoltage() {
		this(1, EnumBiggerVoltage.BOOM);
	}
	
	/**
	 * 操作默认为爆炸
	 * @param radius 影响半径
	 */
	public BiggerVoltage(int radius) {
		this(radius, EnumBiggerVoltage.BOOM);
	}
	
	/**
	 * 影响半径默认为1
	 * @param ebv 操作
	 */
	public BiggerVoltage(EnumBiggerVoltage ebv) {
		this(1, ebv);
	}
	
	/**
	 * @param radius 影响半径
	 * @param ebv 操作
	 */
	public BiggerVoltage(int radius, EnumBiggerVoltage ebv) {
		this.radius = (radius > 0) ? radius : 1;
		this.EBV = ebv;
	}
	
	/**
	 * 机器过载操作
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
	
}
