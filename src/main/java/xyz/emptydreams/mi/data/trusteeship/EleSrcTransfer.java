package xyz.emptydreams.mi.data.trusteeship;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.info.EleLineCache;
import xyz.emptydreams.mi.api.electricity.info.PathInfo;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleTransfer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.blocks.te.EleSrcCable;
import xyz.emptydreams.mi.data.info.WireLinkInfo;
import xyz.emptydreams.mi.register.trusteeship.AutoTrusteeshipRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTrusteeshipRegister
public class EleSrcTransfer implements IEleTransfer {
	
	@Override
	public PathInfo findPath(TileEntity start, TileEntity user, IEleInputer inputer) {
		EleLineCache cache = getLineCache(start);
		if (cache == null) return null;
		PathInfo info = cache.read(start, user, inputer);
		if (info != null) return info;
		info = WireLinkInfo.calculate((EleSrcCable) start, user, inputer);
		if (info == null) return null;
		cache.writeInfo(info);
		return info;
	}
	
	@Override
	public Object transfer(TileEntity now, int energy, IVoltage voltage, Object info) {
		EleSrcCable cable = (EleSrcCable) now;
		cable.transfer(energy);
		if (cable.getTransfer() > cable.getMeMax()) {
			cable.clearTransfer();
			cable.getCounter().plus();
			if (cable.getCounter().getTime() > cable.getBiggerMaxTime()) {
				EleLineCache cache = getLineCache(now);
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
	
	@Nullable
	@Override
	public EleLineCache getLineCache(TileEntity now) {
		return ((EleSrcCable) now).getCache();
	}
	
	@Override
	public void setLineCache(TileEntity now, EleLineCache cache) {
		((EleSrcCable) now).setCache((WireLinkInfo) cache);
	}
	
	@Nonnull
	@Override
	public EleLineCache createLineCache(TileEntity now) {
		return new WireLinkInfo();
	}
	
	@Override
	public int getLinkAmount(TileEntity now) {
		return getLineCache(now).getOutputerAmount();
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
