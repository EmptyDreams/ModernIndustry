package xyz.emptydreams.mi.content.tileentity.maker;

import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.electricity.EleTileEntity;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.data.info.EnumVoltage;

/**
 * 永恒发电机
 * @author EmptyDreams
 */
@AutoTileEntity("perpetual")
public class EMPerpetual extends EleTileEntity {
	
	public EMPerpetual() {
		setExtractRange(1, Integer.MAX_VALUE, EnumVoltage.NON, EnumVoltage.H);
		setExtract(true);
		setMaxEnergy(Integer.MAX_VALUE);
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