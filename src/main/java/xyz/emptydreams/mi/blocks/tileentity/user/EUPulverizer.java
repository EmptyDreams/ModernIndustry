package xyz.emptydreams.mi.blocks.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.electricity.clock.OrdinaryCounter;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.group.AbstractSlotGroup;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.gui.component.interfaces.IProgressBar;
import xyz.emptydreams.mi.api.register.tileentity.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.blocks.CommonUtil;
import xyz.emptydreams.mi.blocks.CraftList;
import xyz.emptydreams.mi.blocks.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.data.info.BiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumVoltage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xyz.emptydreams.mi.blocks.base.MIProperty.HORIZONTAL;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 粉碎机的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("pulverizer")
public class EUPulverizer extends FrontTileEntity implements ITickable {

	/** 物品栏 */
	@Storage private final ItemStackHandler item = new ItemStackHandler(2);
	private final SlotItemHandler in = CommonUtil.createInputSlot(item, 0, 52, 32,
																CraftList.PULVERIZER::haveInput);
	private final SlotItemHandler out = CommonUtil.createOutputSlot(item, 1, 106, 32);
	private final SlotGroup slotGroup = new AbstractSlotGroup(in);
	/** 工作时间 */
	private int workingTime = 0;
	/** 工作进度条 */
	private final CommonProgress progressBar = new CommonProgress();

	public EUPulverizer() {
		setReceiveRange(1, 20, EnumVoltage.C, EnumVoltage.D);
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setReceive(true);
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
		IBlockState old = world.getBlockState(pos);
		WorldUtil.setBlockState(world, pos, old, old.withProperty(WORKING, workingTime > 0));
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
	public EnumFacing getFront() { return world.getBlockState(pos).getValue(HORIZONTAL); }
	@Override
	public boolean isReAllowable(EnumFacing facing) { return true; }
	@Override
	public boolean isExAllowable(EnumFacing facing) { return false; }

}