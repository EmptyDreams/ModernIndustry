package xyz.emptydreams.mi.api.electricity.src.trusteeship;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcUser;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTrusteeshipRegister
public class EleSrcInputer implements IEleInputer {
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcInputer");
	
	@Override
	public int getEnergy(TileEntity te) {
		return ((EleSrcUser) te).getEnergy();
	}
	
	@Override
	public int getEnergy(TileEntity now, int energy) {
		int max = getEnergy(now);
		return energy >= max ? max : ((EleSrcUser) now).getEnergyMin();
	}
	
	@Override
	public void useEnergy(TileEntity now, int energy, IVoltage voltage) { }
	
	@Override
	public IVoltage getVoltage(TileEntity te) {
		return ((EleSrcUser) te).getVoltage();
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
		return te instanceof EleSrcUser;
	}
	
}
