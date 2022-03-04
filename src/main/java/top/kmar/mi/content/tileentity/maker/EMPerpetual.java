package top.kmar.mi.content.tileentity.maker;

import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.electricity.EleTileEntity;
import top.kmar.mi.data.info.EnumVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.register.others.AutoTileEntity;

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