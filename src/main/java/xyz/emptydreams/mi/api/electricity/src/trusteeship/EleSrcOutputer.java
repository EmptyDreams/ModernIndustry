package xyz.emptydreams.mi.api.electricity.src.trusteeship;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleMaker;
import xyz.emptydreams.mi.api.electricity.info.UseInfo;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

/**
 * MI提供的缺省输出托管. 使用该托管的方块的TE必须实现{@link EleMaker}
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTrusteeshipRegister
public class EleSrcOutputer implements IEleOutputer {
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcOutputer");
	
	@Override
	public UseInfo output(TileEntity te, int energy, IVoltage voltage, boolean simulation) {
		UseInfo info = new UseInfo();
		switch (((EleMaker) te).output(energy, voltage, !simulation)) {
			case YES:
				info.setEnergy(energy);
				info.setVoltage(voltage);
				break;
			case FAILURE:
				info.setEnergy(0);
				info.setVoltage(EnumVoltage.NON);
				break;
			case NOT_ENOUGH:
				info.setEnergy(((EleMaker) te).getOutputMax());
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
	public boolean isAllowable(TileEntity now, IVoltage voltage) {
		return ((EleMaker) now).checkOutput(voltage);
	}
	
	@Override
	public int getOutput(TileEntity te) {
		return ((EleMaker) te).getOutputMax();
	}
	
	@Override
	public ResourceLocation getName() {
		return NAME;
	}
	
	@Override
	public boolean contains(TileEntity te) {
		return te instanceof EleMaker;
	}
}
