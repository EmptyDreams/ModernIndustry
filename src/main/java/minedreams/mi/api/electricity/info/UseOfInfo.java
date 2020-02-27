package minedreams.mi.api.electricity.info;

import minedreams.mi.api.electricity.interfaces.IVoltage;

/**
 * 计算结果信息储存
 * @author EmptyDreams
 * @version V1.0
 */
public final class UseOfInfo {
	
	private IVoltage voltage;
	private int energy;
	
	public UseOfInfo() { }
	
	public UseOfInfo(int energy, IVoltage voltage) {
		this.voltage = voltage;
		this.energy = energy;
	}
	
	public IVoltage getVoltage() {
		return voltage;
	}
	
	public UseOfInfo setVoltage(IVoltage voltage) {
		this.voltage = voltage;
		return this;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public UseOfInfo setEnergy(int energy) {
		this.energy = energy;
		return this;
	}
}
