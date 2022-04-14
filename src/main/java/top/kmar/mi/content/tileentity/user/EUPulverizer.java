package top.kmar.mi.content.tileentity.user;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.electricity.info.BiggerVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage;
import top.kmar.mi.api.gui.component.CommonProgress;
import top.kmar.mi.api.gui.component.group.AbstractSlotGroup;
import top.kmar.mi.api.gui.component.group.SlotGroup;
import top.kmar.mi.api.gui.component.interfaces.IProgressBar;
import top.kmar.mi.api.register.others.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.blocks.CraftList;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 粉碎机的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("pulverizer")
public class EUPulverizer extends FrontTileEntity implements ITickable {

	public static final int VOLTAGE = EleEnergy.COMMON;
	
	/** 物品栏 */
	@AutoSave
    private final ItemStackHandler item = new ItemStackHandler(2);
	private final SlotItemHandler in = CommonUtil.createInputSlot(item, 0, 52, 32,
																CraftList.PULVERIZER::haveInput);
	private final SlotItemHandler out = CommonUtil.createOutputSlot(item, 1, 106, 32);
	private final SlotGroup slotGroup = new AbstractSlotGroup(in);
	/** 工作时间 */
	private int workingTime = 0;
	/** 工作进度条 */
	private final CommonProgress progressBar = new CommonProgress();

	public EUPulverizer() {
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setMaxEnergy(20);
		progressBar.setCraftButton(CraftList.PULVERIZER, te -> ((EUPulverizer) te).slotGroup);
	}

	@Override
	public void update() {
		if (world.isRemote) {
			WorldUtil.removeTickable(this);
			return;
		}
		if (checkInputAndOutput() || !shrinkEnergy(getNeedEnergy())) {
			updateShow();
			return;
		}
		updateData();
		updateShow();
	}

	private void updateShow() {
		progressBar.setMax(getNeedTime());
		progressBar.setNow(workingTime);
		WorldUtil.setBlockState(world, pos,
				world.getBlockState(pos)
						.withProperty(MIProperty.getWORKING(), workingTime > 0));
	}

	/** 更新内部数据 */
	@SuppressWarnings("ConstantConditions")
	private void updateData() {
		if (++workingTime >= getNeedTime()) {
			if (out.getHasStack()) {
				out.getStack().grow(CraftList.PULVERIZER.apply(
						new ItemSet(ItemElement.instance(in.getStack()))).getAmount());
			} else {
				out.putStack(CraftList.PULVERIZER.apply(
						new ItemSet(ItemElement.instance(in.getStack()))).getStack());
			}
			in.getStack().shrink(1);
			workingTime = 0;
		}
		markDirty();
	}

	/**
	 * 检查输入/输出是否正确
	 * @return 是否中断运行
	 */
	private boolean checkInputAndOutput() {
		//如果输入框为空则不可能正常工作
		if (!in.getHasStack()) {
			if (workingTime != 0) {
				workingTime = 0;
				markDirty();
			}
			return true;
		}
		ItemStack out = this.out.getStack();
		if (!out.isEmpty()) {
			//noinspection ConstantConditions
			if (out.getCount() >= out.getMaxStackSize() ||
					!CraftList.PULVERIZER.apply(new ItemSet(
							ItemElement.instance(in.getStack()))).contain(out)) {
				//如果输出框不为空但物品数量达到上限则不能正常运行
				//               或产品与输出框不相符则不能正常运行
				if (workingTime != 0) {
					workingTime = 0;
					markDirty();
				}
				return true;
			}
		}
		return false;
	}

	/** 获取输入槽 */
	@Nonnull
	public SlotItemHandler getInSlot() { return in; }
	/** 获取输出槽 */
	@Nonnull
	public SlotItemHandler getOutSlot() { return out; }
	/** 获取1tick需要的能量 */
	public int getNeedEnergy() { return 10; }
	/** 获取需要工作的时长 */
	public int getNeedTime() { return 120; }
	/** 获取进度条 */
	public IProgressBar getProgress() { return progressBar; }
	@Nullable
	@Override
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
	}
	
	@Override
	public boolean onReceive(EleEnergy energy) {
		if (energy.getVoltage() > VOLTAGE) getCounter().plus();
		return true;
	}
	
	@Override
	public boolean isReceiveAllowable(EnumFacing facing) { return true; }
	@Override
	public boolean isExtractAllowable(EnumFacing facing) { return false; }
	
	@Override
	public int getExVoltage() {
		return EleEnergy.COMMON;
	}
	
}