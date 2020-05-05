package xyz.emptydreams.mi.api.electricity.info;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

/**
 * 存储一个能量的具体值
 * @author EmptyDreams
 * @version V1.0
 */
public final class EleEnergy implements INBTSerializable<NBTTagCompound> {
	
	private IVoltage voltage;
	private int energy;
	
	public EleEnergy() { }
	
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
	
	public EleEnergy copy() {
		return new EleEnergy(energy, voltage.copy());
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("voltage", voltage.getVoltage());
		data.setInteger("loss", voltage.getLossIndex());
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
