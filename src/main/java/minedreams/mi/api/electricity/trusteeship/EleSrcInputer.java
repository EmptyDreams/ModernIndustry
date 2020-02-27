package minedreams.mi.api.electricity.trusteeship;

import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.EleWorker;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.ElectricityUser;
import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class EleSrcInputer implements IEleInputer {
	
	static {
		EleWorker.registerInputer(new EleSrcInputer());
	}
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcInputer");
	
	@Override
	public void input(TileEntity te, int energy, IVoltage voltage) {
		((ElectricityUser) te).useElectricity(energy, voltage);
	}
	
	@Override
	public int getEnergyMin(TileEntity te) {
		return ((ElectricityUser) te).getEnergyMin();
	}
	
	@Override
	public int getEnergy(TileEntity te) {
		return ((ElectricityUser) te).getEnergy();
	}
	
	@Override
	public IVoltage getVoltage(TileEntity te) {
		return ((ElectricityUser) te).getVoltage();
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
		return te instanceof ElectricityUser;
	}
	
}
