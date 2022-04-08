package top.kmar.mi.content.tileentity.maker;

import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.electricity.EleTileEntity;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.register.others.AutoTileEntity;

/**
 * 永恒发电机
 * @author EmptyDreams
 */
@AutoTileEntity("perpetual")
public class EMPerpetual extends EleTileEntity {
	
	public static final int VOLTAGE = EleEnergy.COMMON;
	
	public EMPerpetual() {
		setMaxEnergy(Integer.MAX_VALUE);
		setNowEnergy(Integer.MAX_VALUE);
	}
	
	@Override
	public boolean onReceive(EleEnergy energy) {
		if (energy.getVoltage() > VOLTAGE) getCounter().plus();
		return true;
	}
	
	@Override
	public boolean onExtract(EleEnergy energy) {
		setNowEnergy(Integer.MAX_VALUE);
		return true;
	}
	
	@Override
	public boolean isReceiveAllowable(EnumFacing facing) {
		return false;
	}
	
	@Override
	public boolean isExtractAllowable(EnumFacing facing) {
		return true;
	}
	
	@Override
	public int getExVoltage() {
		return EleEnergy.COMMON;
		//TODO
	}
	
}