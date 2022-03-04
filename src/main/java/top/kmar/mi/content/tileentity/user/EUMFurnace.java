package top.kmar.mi.content.tileentity.user;

import top.kmar.mi.data.info.EnumVoltage;
import top.kmar.mi.api.register.others.AutoTileEntity;

/**
 * 高温火炉的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("ele_mfurnace")
public class EUMFurnace extends EUFurnace {

	public EUMFurnace() {
		setReceiveRange(10, 20, EnumVoltage.C, EnumVoltage.D);
		setMaxEnergy(20);
	}

	@Override
	public int getNeedEnergy() {
		return 10;
	}

	@Override
	public int getNeedTime() {
		return 100;
	}

}