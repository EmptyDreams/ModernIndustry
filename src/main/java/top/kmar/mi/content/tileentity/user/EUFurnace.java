package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.gui.component.interfaces.IProgressBar;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.utils.data.io.Storage;
import top.kmar.mi.data.info.BiggerVoltage;
import top.kmar.mi.data.info.EnumBiggerVoltage;
import top.kmar.mi.data.info.EnumVoltage;
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.gui.component.CommonProgress;
import top.kmar.mi.api.register.others.AutoTileEntity;
import top.kmar.mi.api.utils.properties.MIProperty;

import javax.annotation.Nullable;

/**
 * 电炉
 * @author EmptyDreams
 */
@AutoTileEntity("ele_furnace")
public class EUFurnace extends FrontTileEntity implements ITickable {

	/** 工作进度条 */
	private final CommonProgress progressBar = new CommonProgress();
	/** 输入/输出框 */
	@Storage
    private final ItemStackHandler item = new ItemStackHandler(2);
	/** 输入框 */
	private final SlotItemHandler in = new SlotItemHandler(item, 0, 52, 32) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return super.isItemValid(stack) && !MuffleFurnace.getResult(stack).isEmpty();
		}
	};
	/** 输出框 */
	private final SlotItemHandler out = new SlotItemHandler(item, 1, 106, 32) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	@Storage private int workingTime = 0;

	public EUFurnace() {
		setReceiveRange(5, 10, EnumVoltage.C, EnumVoltage.D);
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setReceive(true);
		setMaxEnergy(10);
		progressBar.setMax(getNeedTime());
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		updateWorkingTime();
		progressBar.setNow(workingTime);
	}

	private void updateWorkingTime() {
		ItemStack outStack = out.getStack();
		if (outStack.getCount() >= outStack.getMaxStackSize()) return;
		if (!in.getHasStack()) {
			workingTime = 0;
			return;
		}
		if (!shrinkEnergy(getNeedEnergy())) return;

		if (++workingTime >= getNeedTime()) {
			workingTime = 0;
			if (out.getHasStack()) {
				outStack.grow(1);
			} else {
				out.putStack(MuffleFurnace.getResult(in.getStack()));
			}
			in.getStack().shrink(1);
		}

		IBlockState old = world.getBlockState(pos);
		IBlockState state = old.withProperty(MIProperty.WORKING, workingTime > 0);
		WorldUtil.setBlockState(world, pos, state);
	}

	public IProgressBar getProgressBar() { return progressBar; }
	public SlotItemHandler getInSlot() { return in; }
	public SlotItemHandler getOutSlot() { return out; }
	public int getNeedEnergy() { return 5; }
	public int getNeedTime() { return 120; }

	@Nullable
	@Override
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.HORIZONTAL);
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