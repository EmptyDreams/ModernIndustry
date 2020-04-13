package xyz.emptydreams.mi.api.electricity.src.trusteeship;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * 提供对能力系统的输入支持
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
@AutoTrusteeshipRegister
public class EleCapInputer implements IEleInputer {
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcInputer");
	
	@Override
	public void useEnergy(TileEntity now, int energy, IVoltage voltage) {
		now.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(energy, false);
	}
	
	/**
	 * @throws NullPointerException 传入的TE不符合要求
	 */
	@Override
	public int getEnergy(TileEntity te) {
		return te.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(Integer.MAX_VALUE, true);
	}
	
	@Override
	public int getEnergy(TileEntity now, int energy) {
		return now.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(energy, true);
	}
	
	@Override
	public IVoltage getVoltage(TileEntity te) {
		return EnumVoltage.ORDINARY;
	}
	
	@Override
	public boolean isAllowable(TileEntity now, EnumFacing facing) {
		return true;
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}
	
	@Override
	public boolean contains(TileEntity te) {
		IEnergyStorage cap = te.getCapability(CapabilityEnergy.ENERGY, null);
		if (cap == null) return false;
		return cap.receiveEnergy(1, true) >= 1;
	}
	
}
