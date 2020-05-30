package xyz.emptydreams.mi.api.electricity.src.trusteeship;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.capabilities.EleCapability;
import xyz.emptydreams.mi.api.electricity.capabilities.EnumEleState;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.capabilities.IStorage;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

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
		now.getCapability(EleCapability.ENERGY, null)
				.receiveEnergy(new EleEnergy(energy, voltage), false);
	}
	
	@Override
	public int getEnergy(TileEntity te) {
		return te.getCapability(EleCapability.ENERGY, null)
				       .receiveEnergy(new EleEnergy(Integer.MAX_VALUE, EnumVoltage.NON), true);
	}
	
	@Override
	public int getEnergy(TileEntity now, int energy) {
		return now.getCapability(EleCapability.ENERGY, null)
				.receiveEnergy(new EleEnergy(energy, EnumVoltage.NON), true);
	}
	
	@Override
	public IVoltage getVoltage(TileEntity now, IVoltage voltage) {
		return now.getCapability(EleCapability.ENERGY, null).getVoltage(EnumEleState.IN, voltage);
	}
	
	@Override
	public boolean isAllowable(TileEntity now, EnumFacing facing) {
		IStorage pro = now.getCapability(EleCapability.ENERGY, facing);
		if (pro == null) return false;
		return pro.isReAllowable(facing);
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}
	
	@Override
	public boolean contains(TileEntity te) {
		IStorage cap = te.getCapability(EleCapability.ENERGY, null);
		return cap != null && cap.canReceive();
	}
	
}
