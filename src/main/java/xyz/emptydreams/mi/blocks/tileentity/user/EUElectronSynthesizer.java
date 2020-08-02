package xyz.emptydreams.mi.blocks.tileentity.user;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;
import xyz.emptydreams.mi.api.electricity.clock.OrdinaryCounter;
import xyz.emptydreams.mi.blocks.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.data.info.BiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumVoltage;
import xyz.emptydreams.mi.register.tileentity.AutoTileEntity;

import javax.annotation.Nullable;

/**
 * @author EmptyDreams
 */
@AutoTileEntity("electron_synthesizer")
public class EUElectronSynthesizer extends FrontTileEntity {
	
	private final ItemStackHandler HANDLER = new ItemStackHandler(5 * 5 + 4);
	
	public EUElectronSynthesizer() {
		setReceiveRange(1, 20, EnumVoltage.C, EnumVoltage.D);
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setReceive(true);
		setMaxEnergy(20);
	}
	
	@Nullable
	@Override
	public EnumFacing getFront() {
		return EnumFacing.UP;
	}
	
	@Override
	public boolean isReAllowable(EnumFacing facing) {
		return true;
	}
	
	@Override
	public boolean isExAllowable(EnumFacing facing) {
		return false;
	}
}
