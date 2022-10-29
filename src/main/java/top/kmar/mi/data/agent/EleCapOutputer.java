package top.kmar.mi.data.agent;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.capabilities.ele.EleCapability;
import top.kmar.mi.api.capabilities.ele.IStorage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.interfaces.IEleOutputer;
import top.kmar.mi.api.regedits.others.AutoAgentRegister;

/**
 * 提供对能力系统的输出支持
 * @author EmptyDreams
 */
@AutoAgentRegister("INSTANCE")
public class EleCapOutputer implements IEleOutputer {
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcOutputer");
	
	//该字段通过反射赋值
	@SuppressWarnings("unused")
	private static EleCapOutputer INSTANCE;
	
	public static EleCapOutputer instance() { return INSTANCE; }
	
	@Override
	public EleEnergy output(TileEntity te, int energy, boolean simulation) {
		//noinspection ConstantConditions
		return te.getCapability(EleCapability.ENERGY, null)
				.extractEnergy(energy, simulation);
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