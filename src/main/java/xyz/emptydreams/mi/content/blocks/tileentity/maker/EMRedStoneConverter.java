package xyz.emptydreams.mi.content.blocks.tileentity.maker;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.electricity.clock.NonCounter;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.content.blocks.CommonUtil;
import xyz.emptydreams.mi.content.blocks.base.MIProperty;
import xyz.emptydreams.mi.content.blocks.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.data.info.EnumVoltage;

import javax.annotation.Nullable;

/**
 * 红石能转换器
 * @author EmptyDreams
 */
@AutoTileEntity("red_stone_converter")
public class EMRedStoneConverter extends FrontTileEntity implements ITickable {

	@Storage private final ItemStackHandler item = new ItemStackHandler(1);
	private final SlotItemHandler INPUT = CommonUtil.createInputSlot(item, 0, 79, 20,
			stack -> stack.getItem() == Items.REDSTONE ||
					stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
	private final CommonProgress energyPro = new CommonProgress(
									CommonProgress.Style.STRIPE, CommonProgress.Front.RIGHT);
	private final CommonProgress burnPro = new CommonProgress(
									CommonProgress.Style.ARROW_DOWN, CommonProgress.Front.DOWN);
	@Storage private int burnTime = 0;
	@Storage private int maxTime = 0;

	public static int getBurnTime(ItemStack stack) {
		if (stack.getItem() == Items.REDSTONE) return 500;
		else if (stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) return 5000;
		return 0;
	}

	public EMRedStoneConverter() {
		setExtractRange(1, 120, EnumVoltage.A, EnumVoltage.C);
		setExtract(true);
		setReceive(false);
		setMaxEnergy(5000);
		setCounter(NonCounter.getInstance());

		energyPro.setMax(5000);
		energyPro.setLocation(44, 68);
		energyPro.setStringShower(CommonProgress.ProgressStyle.DOWN);
		burnPro.setLocation(80, 38);
	}

	@Override
	public void update() {
		if (world.isRemote) {
			WorldUtil.removeTickable(this);
			return;
		}

		if (maxTime == 0) {
			if (!burnRedStone()) return;
		}
		updateData();
		updateShow();
	}

	/** 更新显示 */
	private void updateShow() {
		energyPro.setNow(getNowEnergy());
		burnPro.setNow(burnTime);
	}

	/** 更新数据 */
	private void updateData() {
		growEnergy(1);
		if (++burnTime >= maxTime) {
			maxTime = burnTime = 0;
		}
		markDirty();
	}

	/** 消耗红石 */
	private boolean burnRedStone() {
		if (!INPUT.getHasStack()) {
			markDirty();
			return false;
		}
		ItemStack stack = INPUT.getStack();
		stack.shrink(1);
		maxTime = getBurnTime(stack);
		burnPro.setMax(maxTime);
		return true;
	}

	/** 获取输入框 */
	public SlotItemHandler getInput() { return INPUT; }
	/** 获取能量进度条 */
	public CommonProgress getEnergyPro() { return energyPro; }
	/** 获取燃烧进度条 */
	public CommonProgress getBurnPro() { return burnPro; }

	@Nullable
	@Override
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.HORIZONTAL);
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