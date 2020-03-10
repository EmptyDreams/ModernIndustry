package minedreams.mi.api.electricity.src.trusteeship;

import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.src.tileentity.EleSrcUser;
import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import minedreams.mi.register.trusteeship.AutoTrusteeshipRegister;
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
	public int getEnergyMin(TileEntity te) {
		return ((EleSrcUser) te).getEnergyMin();
	}
	
	@Override
	public int getEnergy(TileEntity te) {
		return ((EleSrcUser) te).getEnergy();
	}
	
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
