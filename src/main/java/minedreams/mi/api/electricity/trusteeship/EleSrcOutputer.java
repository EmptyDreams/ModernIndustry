package minedreams.mi.api.electricity.trusteeship;

import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.EleWorker;
import minedreams.mi.api.electricity.ElectricityMaker;
import minedreams.mi.api.electricity.info.EnumVoltage;
import minedreams.mi.api.electricity.info.UseOfInfo;
import minedreams.mi.api.electricity.interfaces.IEleOutputer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import minedreams.mi.register.trusteeship.AutoTrusteeshipRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTrusteeshipRegister(EleSrcOutputer.class)
public class EleSrcOutputer implements IEleOutputer {
	
	static {
		EleWorker.registerOutputer(new EleSrcOutputer());
	}
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcOutputer");
	
	@Override
	public UseOfInfo output(TileEntity te, int energy, IVoltage voltage, boolean simulation) {
		UseOfInfo info = new UseOfInfo();
		switch (((ElectricityMaker) te).output(energy, voltage, !simulation)) {
			case YES:
				info.setEnergy(energy);
				info.setVoltage(voltage);
				break;
			case FAILURE:
				info.setEnergy(0);
				info.setVoltage(EnumVoltage.NON);
				break;
			case NOT_ENOUGH:
				info.setEnergy(((ElectricityMaker) te).getOutputMax());
				info.setVoltage(voltage);
				break;
		}
		return info;
	}
	
	@Override
	public boolean isAllowable(TileEntity te, EnumFacing facing) {
		return true;
	}
	
	@Override
	public int getOutput(TileEntity te) {
		return ((ElectricityMaker) te).getOutputMax();
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}
	
	@Override
	public boolean contains(TileEntity te) {
		return te instanceof ElectricityMaker;
	}
}
