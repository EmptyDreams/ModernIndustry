package xyz.emptydreams.mi.data.trusteeship;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.info.PathInfo;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleTransfer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.blocks.tileentity.EleSrcCable;
import xyz.emptydreams.mi.data.info.CableCache;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;

/**
 * 线缆的代理
 * @author EmptyDreams
 */
@AutoTrusteeshipRegister("INSTANCE")
public class EleSrcTransfer implements IEleTransfer {
	
	//该字段通过反射赋值
	@SuppressWarnings("unused")
	private static EleSrcTransfer INSTANCE;
	
	public static EleSrcTransfer instance() { return INSTANCE; }
	
	@Override
	public PathInfo findPath(TileEntity start, TileEntity user, IEleInputer inputer) {
		EleSrcCable cable = (EleSrcCable) start;
		CableCache cache = cable.getCache();
		if (cache == null) return null;
		return cache.calculate(cable, user, inputer);
	}
	
	@Override
	public Object transfer(TileEntity now, int energy, IVoltage voltage, Object info) {
		EleSrcCable cable = (EleSrcCable) now;
		cable.transfer(energy);
		if (cable.getTransfer() > cable.getMeMax()) {
			cable.clearTransfer();
			cable.getCounter().plus();
			if (cable.getCounter().getTime() > cable.getBiggerMaxTime()) {
				CableCache cache = cable.getCache();
				if (info == cache && info != null) cable.getCounter().clean();
				else cable.getCounter().overload();
				return cache;
			}
		} else {
			cable.getCounter().clean();
		}
		cable.clearTransfer();
		return null;
	}
	
	@Override
	public void cleanTransfer(TileEntity now) { ((EleSrcCable) now).clearTransfer(); }
	
	@Override
	public boolean link(TileEntity now, TileEntity target) {
		return ((EleSrcCable) now).link(target.getPos());
	}
	
	@Override
	public boolean isLink(TileEntity now, TileEntity target) {
		EleSrcCable cable = (EleSrcCable) now;
		return target.equals(cable.getNext()) ||
				       target.equals(cable.getPrev()) ||
				       cable.getLinkedBlocks().contains(target);
	}
	
	@Override
	public boolean canLink(TileEntity now, TileEntity target) {
		return ((EleSrcCable) now).canLink(target);
	}
	
	@Override
	public double getEnergyLoss(TileEntity now, int energy, IVoltage voltage) {
		return ((EleSrcCable) now).getLoss(new EleEnergy(energy, voltage));
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
