package xyz.emptydreams.mi.blocks.tileentity.user;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import xyz.emptydreams.mi.api.electricity.clock.OrdinaryCounter;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.IProgressBar;
import xyz.emptydreams.mi.api.gui.group.SlotGroup;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.blocks.CraftList;
import xyz.emptydreams.mi.blocks.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.data.info.BiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumVoltage;
import xyz.emptydreams.mi.register.tileentity.AutoTileEntity;

import javax.annotation.Nullable;

import static xyz.emptydreams.mi.api.utils.data.DataType.INT;
import static xyz.emptydreams.mi.api.utils.data.DataType.SERIALIZABLE;

/**
 * @author EmptyDreams
 */
@AutoTileEntity("electron_synthesizer")
public class EUElectronSynthesizer extends FrontTileEntity implements ITickable {
	
	@Storage(SERIALIZABLE) private final ItemStackHandler HANDLER = new ItemStackHandler(5 * 5 + 4);
	private final SlotGroup SLOTS = new SlotGroup(5, 5, 18, 0);
	private final SlotGroup OUTS = new SlotGroup(2, 2, 18, 0);
	private final CommonProgress PROGRESS = new CommonProgress(
										CommonProgress.Style.ARROW, CommonProgress.Front.RIGHT);
	
	@Storage(INT) private int workingTime = -1;
	
	public EUElectronSynthesizer() {
		setReceiveRange(1, 20, EnumVoltage.C, EnumVoltage.D);
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setReceive(true);
		setMaxEnergy(20);
		SLOTS.writeFrom(HANDLER, 0, CraftList.SYNTHESIZER::rawHas);
		OUTS.writeFrom(HANDLER, 25, it -> false);
		PROGRESS.setMax(100);
	}
	
	@Override
	public void update() {
		if (world.isRemote) {
			WorldUtil.removeTickable(this);
			return;
		}
		
	}
	
	/** 获取原料入口 */
	public SlotGroup getInput() {
		return SLOTS;
	}
	
	/** 获取产品 */
	public SlotGroup getOutput() {
		return OUTS;
	}
	
	/** 获取进度条 */
	public IProgressBar getProgress() {
		return PROGRESS;
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
