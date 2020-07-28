package xyz.emptydreams.mi.blocks.tileentity.user;

import xyz.emptydreams.mi.data.info.EnumVoltage;
import xyz.emptydreams.mi.register.tileentity.AutoTileEntity;

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
