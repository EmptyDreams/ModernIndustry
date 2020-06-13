package xyz.emptydreams.mi.api.electricity.src.trusteeship;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.capabilities.EleCapability;
import xyz.emptydreams.mi.api.electricity.capabilities.IStorage;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;

/**
 * 提供对能力系统的输出支持
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTrusteeshipRegister
public class EleCapOutputer implements IEleOutputer {
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcOutputer");
	
	@Override
	public EleEnergy output(TileEntity te, int energy, IVoltage voltage, boolean simulation) {
		return te.getCapability(EleCapability.ENERGY, null)
				           .extractEnergy(new EleEnergy(energy, voltage), simulation);
	}
	
	@Override
	public void fallback(TileEntity te, int energy) {
		te.getCapability(EleCapability.ENERGY, null).fallback(energy);
	}
	
	@Override
	public boolean isAllowable(TileEntity te, EnumFacing facing) {
		IStorage energy = te.getCapability(EleCapability.ENERGY, facing);
		if (energy == null) return false;
		return energy.isExAllowable(facing);
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}
	
	@Override
	public boolean contains(TileEntity te) {
		IStorage cap = te.getCapability(EleCapability.ENERGY, null);
		return cap != null && cap.canExtract();
	}
	
}
