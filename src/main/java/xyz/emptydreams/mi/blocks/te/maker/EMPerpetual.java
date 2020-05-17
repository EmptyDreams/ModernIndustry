package xyz.emptydreams.mi.blocks.te.maker;

import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleTileEntity;
import xyz.emptydreams.mi.register.te.AutoTileEntity;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTileEntity("perpetual")
public class EMPerpetual extends EleTileEntity {
	
	public EMPerpetual() {
		setExtractRange(1, Integer.MAX_VALUE, EnumVoltage.NON, EnumVoltage.SUPERCONDUCTOR);
		setExtract(true);
		setMaxExtract(Integer.MAX_VALUE);
		setNowEnergy(Integer.MAX_VALUE);
	}
	
	@Override
	public boolean onExtract(EleEnergy energy) {
		setNowEnergy(Integer.MAX_VALUE);
		return true;
	}
	
	@Override
	public boolean isReAllowable(EnumFacing facing) {
		return false;
	}
	
	@Override
	public boolean isExAllowable(EnumFacing facing) {
		return true;
	}
	
}
