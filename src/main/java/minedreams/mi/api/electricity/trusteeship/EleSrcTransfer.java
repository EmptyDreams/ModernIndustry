package minedreams.mi.api.electricity.trusteeship;

import javax.annotation.Nullable;

import minedreams.mi.api.electricity.cache.EleLineCache;
import minedreams.mi.api.electricity.info.PathInfo;
import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IEleTransfer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class EleSrcTransfer implements IEleTransfer {
	
	@Override
	public PathInfo findPath(TileEntity start, TileEntity user, IEleInputer inputer) {
		return null;
	}
	
	@Override
	public Object transfer(TileEntity now, int energy, IVoltage voltage, Object info) {
		return null;
	}
	
	@Override
	public boolean link(TileEntity now, TileEntity target) {
		return false;
	}
	
	@Override
	public boolean isLink(TileEntity now, TileEntity target) {
		return false;
	}
	
	@Override
	public boolean canLink(TileEntity now, TileEntity tgte) {
		return false;
	}
	
	@Nullable
	@Override
	public EleLineCache getLineCache(TileEntity now) {
		return null;
	}
	
	@Override
	public ResourceLocation getName() {
		return null;
	}
	
	@Override
	public boolean contains(TileEntity te) {
		return false;
	}
	
}
