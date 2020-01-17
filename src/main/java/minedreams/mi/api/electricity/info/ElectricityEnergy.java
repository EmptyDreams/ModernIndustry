package minedreams.mi.api.electricity.info;

/**
 * 能量单位
 * @author EmptyDremas
 * @version V1.0
 */
public final class ElectricityEnergy {

	/** 能量 */
	private double energy = 0;
	/**
	 * 电压<br>
	 * 未来版本可能开启电压损耗
	 */
	private int voltage = 0;
	
	private ElectricityEnergy() { }
	
	public static ElectricityEnergy craet(int energy, int voltage) {
		ElectricityEnergy ee = new ElectricityEnergy();
		ee.energy = energy;
		ee.voltage = voltage;
		return ee;
	}
	
	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
	
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	
	public int getVoltage() {
		return voltage;
	}
	
	public double getEnergy() {
		return energy;
	}
	
	/**
	 * 判断两个电压/电能是否相等，判断范围是5
	 */
	public static boolean isEquals(double arg0, double arg1) {
		if (arg0 == arg1) return true;
		return isEquals(arg0, arg1 - 5, arg1 + 5);
	}
	
	/**
	 * 判断电压/电能是否在指定范围内
	 * @param arg0 要判断的电压
	 * @param min 最小值
	 * @param max 最大值
	 */
	public static boolean isEquals(double arg0, double min, double max) {
		return arg0 >= min && arg0 <= max;
	}
	
}
