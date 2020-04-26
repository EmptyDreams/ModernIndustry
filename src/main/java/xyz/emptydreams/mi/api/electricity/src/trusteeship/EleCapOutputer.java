package xyz.emptydreams.mi.api.electricity.src.trusteeship;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.info.UseInfo;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * 提供对能力系统的输出支持
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
@AutoTrusteeshipRegister
public class EleCapOutputer implements IEleOutputer {
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcOutputer");
	
	@Override
	public UseInfo output(TileEntity te, int energy, IVoltage voltage, boolean simulation) {
		UseInfo info = new UseInfo();
		int real = te.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energy, simulation);
		return info.setVoltage(EnumVoltage.ORDINARY).setEnergy(real);
	}
	
	@Override
	public boolean isAllowable(TileEntity te, EnumFacing facing) {
		IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, facing);
		if (energy == null) return false;
		return energy.extractEnergy(1, true) > 0;
	}
	
	@Override
	public boolean isAllowable(TileEntity now, IVoltage voltage) {
		return now.hasCapability(CapabilityEnergy.ENERGY, null);
	}
	
	@Override
	public int getOutput(TileEntity te) {
		return te.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(Integer.MAX_VALUE, true);
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}
	
	@Override
	public boolean contains(TileEntity te) {
		IEnergyStorage cap = te.getCapability(CapabilityEnergy.ENERGY, null);
		return cap != null;
	}
	
}