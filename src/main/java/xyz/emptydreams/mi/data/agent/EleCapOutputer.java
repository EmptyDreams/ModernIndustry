package xyz.emptydreams.mi.data.agent;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.capabilities.ele.EleCapability;
import xyz.emptydreams.mi.api.capabilities.ele.IStorage;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.info.VoltageRange;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.register.agent.AutoAgentRegister;

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
	public EleEnergy output(TileEntity te, int energy, VoltageRange voltage, boolean simulation) {
		//noinspection ConstantConditions
		return te.getCapability(EleCapability.ENERGY, null)
				           .extractEnergy(energy, voltage, simulation);
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