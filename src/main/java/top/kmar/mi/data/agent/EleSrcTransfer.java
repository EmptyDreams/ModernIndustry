package top.kmar.mi.data.agent;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.PathInfo;
import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.electricity.interfaces.IEleTransfer;
import top.kmar.mi.api.register.others.AutoAgentRegister;
import top.kmar.mi.api.utils.ExpandFunctionKt;
import top.kmar.mi.content.tileentity.EleSrcCable;

/**
 * 线缆的代理
 * @author EmptyDreams
 */
@AutoAgentRegister("INSTANCE")
public class EleSrcTransfer implements IEleTransfer {
	
	//该字段通过反射赋值
	@SuppressWarnings("unused")
	private static EleSrcTransfer INSTANCE;
	
	public static EleSrcTransfer instance() { return INSTANCE; }
	
	@Override
	public PathInfo findPath(TileEntity start, TileEntity user, IEleInputer inputer) {
		EleSrcCable cable = (EleSrcCable) start;
		return cable.getCache().invoke(cable, user);
	}
	
	@Override
	public void transfer(TileEntity now, EleEnergy energy) {
		EleSrcCable cable = (EleSrcCable) now;
		cable.transfer(energy);
	}
	
	@Override
	public boolean link(TileEntity now, TileEntity target) {
		return ((EleSrcCable) now).link(target.getPos());
	}
	
	@Override
	public boolean isLink(TileEntity now, TileEntity target) {
		EleSrcCable cable = (EleSrcCable) now;
		if (target.equals(cable.getNext()) ||
				target.equals(cable.getPrev())) return true;
		EnumFacing facing = ExpandFunctionKt.whatFacing(now.getPos(), target.getPos());
		return cable.isLink(facing);
	}
	
	@Override
	public boolean canLink(TileEntity now, TileEntity target) {
		return ((EleSrcCable) now).canLink(target);
	}
	
	@Override
	public int getEnergyLoss(TileEntity now, EleEnergy energy) {
		return ((EleSrcCable) now).getLoss(energy);
	}
	
	@Override
	public ResourceLocation getName() {
		return new ResourceLocation(ModernIndustry.MODID, "srcTransfer");
	}
	
	@Override
	public boolean contains(TileEntity te) {
		return te instanceof EleSrcCable;
	}
	
}