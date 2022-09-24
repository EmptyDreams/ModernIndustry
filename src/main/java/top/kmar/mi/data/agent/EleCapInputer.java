package top.kmar.mi.data.agent;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.capabilities.ele.EleCapability;
import top.kmar.mi.api.capabilities.ele.IStorage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.register.others.AutoAgentRegister;

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
	public EleEnergy useEnergy(TileEntity now, EleEnergy energy) {
		//noinspection ConstantConditions
		return now.getCapability(EleCapability.ENERGY, null)
				.receiveEnergy(energy, false);
	}
	
	@Override
	public int getEnergyDemand(TileEntity now) {
		//noinspection ConstantConditions
		return now.getCapability(EleCapability.ENERGY, null).getEnergyDemand();
	}
	
	@Override
	public EleEnergy getEnergy(TileEntity now, EleEnergy energy) {
		//noinspection ConstantConditions
		return now.getCapability(EleCapability.ENERGY, null).receiveEnergy(energy, true);
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