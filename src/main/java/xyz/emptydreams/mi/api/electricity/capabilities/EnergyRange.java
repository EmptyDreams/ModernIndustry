package xyz.emptydreams.mi.api.electricity.capabilities;

import javax.annotation.Nonnull;

import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

/**
 * 用于表示能量范围
 * @author EmptyDreams
 * @version V1.0
 */
public final class EnergyRange {
	
	private IVoltage minVoltage;
	private IVoltage maxVoltage;
	private int minEnergy;
	private int maxEnergy;
	
	public IVoltage getMinVoltage() {
		return minVoltage;
	}
	
	public void setMinVoltage(IVoltage minVoltage) {
		this.minVoltage = minVoltage;
	}
	
	public IVoltage getMaxVoltage() {
		return maxVoltage;
	}
	
	public void setMaxVoltage(IVoltage maxVoltage) {
		this.maxVoltage = maxVoltage;
	}
	
	public int getMinEnergy() {
		return minEnergy;
	}
	
	public void setMinEnergy(int minEnergy) {
		this.minEnergy = minEnergy;
	}
	
	public int getMaxEnergy() {
		return maxEnergy;
	}
	
	public void setMaxEnergy(int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}
	
	/** 获取最适电压 */
	public IVoltage getOptimalVoltage(IVoltage voltage) {
		return voltage.getVoltage() >= minVoltage.getVoltage() ?
				       voltage.getVoltage() <= maxVoltage.getVoltage() ? voltage : maxVoltage : minVoltage;
	}
	
	/** 获取最适电能 */
	public int getOptimalEnergy(int energy) {
		return energy >= minEnergy ? Math.min(energy, maxEnergy) : minEnergy;
	}
	
	@Nonnull
	public EnergyRange copy() {
		EnergyRange range = new EnergyRange();
		range.minEnergy = minEnergy;
		range.maxEnergy = maxEnergy;
		if (minVoltage != null) range.minVoltage = minVoltage.copy();
		if (maxVoltage != null) range.maxVoltage = maxVoltage.copy();
		return range;
	}
}
