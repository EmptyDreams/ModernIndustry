package top.kmar.mi.api.electricity.info;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import top.kmar.mi.api.electricity.interfaces.IVoltage;
import top.kmar.mi.data.info.EnumVoltage;

/**
 * 存储一个能量的具体值
 * @author EmptyDreams
 */
public final class EleEnergy implements INBTSerializable<NBTTagCompound> {
	
	private IVoltage voltage;
	private int energy;
	
	public EleEnergy() { this(0, EnumVoltage.D); }
	
	public EleEnergy(int energy, IVoltage voltage) {
		this.energy = energy;
		this.voltage = voltage;
	}
	
	public IVoltage getVoltage() {
		return voltage;
	}
	
	public void setVoltage(IVoltage voltage) {
		this.voltage = voltage;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	/** 拷贝当前对象 */
	public EleEnergy copy() {
		return new EleEnergy(energy, voltage.copy());
	}

	/**
	 * 计算损耗的电能
	 * @return 计算结果
	 */
	public double calculateLoss() {
		return voltage.getLossIndex() * energy;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("voltage", voltage.getVoltage());
		data.setDouble("loss", voltage.getLossIndex());
		data.setInteger("energy", energy);
		return data;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int voltage = nbt.getInteger("voltage");
		int loss = nbt.getInteger("loss");
		energy = nbt.getInteger("energy");
		this.voltage = IVoltage.getInstance(voltage, loss);
	}
}