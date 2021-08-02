package xyz.emptydreams.mi.data.agent;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.capabilities.ele.EleCapability;
import xyz.emptydreams.mi.api.capabilities.ele.IStorage;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.capabilities.ele.EleStateEnum;
import xyz.emptydreams.mi.api.electricity.info.VoltageRange;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.register.agent.AutoAgentRegister;
import xyz.emptydreams.mi.data.info.EnumVoltage;

/**
 * 提供对能力系统的输入支持
 * @author EmptyDreams
 */
@AutoAgentRegister("INSTANCE")
public class EleCapInputer implements IEleInputer {
	
	private static final ResourceLocation NAME =
			new ResourceLocation(ModernIndustry.MODID, "EleSrcInputer");
	
	//该字段通过反射赋值
	@SuppressWarnings("unused")
	private static EleCapInputer INSTANCE;
	
	public static EleCapInputer instance() { return INSTANCE; }
	
	@Override
	public int useEnergy(TileEntity now, int energy, IVoltage voltage) {
		//noinspection ConstantConditions
		return now.getCapability(EleCapability.ENERGY, null)
				.receiveEnergy(new EleEnergy(energy, voltage), false);
	}
	
	@Override
	public int getEnergy(TileEntity te) {
		//noinspection ConstantConditions
		return te.getCapability(EleCapability.ENERGY, null)
				       .receiveEnergy(new EleEnergy(Integer.MAX_VALUE, EnumVoltage.NON), true);
	}
	
	@Override
	public int getEnergy(TileEntity now, int energy) {
		//noinspection ConstantConditions
		return now.getCapability(EleCapability.ENERGY, null)
				.receiveEnergy(new EleEnergy(energy, EnumVoltage.NON), true);
	}
	
	@Override
	public IVoltage getVoltage(TileEntity now, IVoltage voltage) {
		//noinspection ConstantConditions
		return now.getCapability(EleCapability.ENERGY, null).getVoltage(EleStateEnum.IN, voltage);
	}
	
	@Override
	public VoltageRange getVoltageRange(TileEntity now) {
		//noinspection ConstantConditions
		return now.getCapability(EleCapability.ENERGY, null).getReceiveVoltageRange();
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