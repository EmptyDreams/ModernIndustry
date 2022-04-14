package top.kmar.mi.content.tileentity.maker;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.electricity.clock.NonCounter;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.gui.component.CommonProgress;
import top.kmar.mi.api.register.others.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nullable;

/**
 * 红石能转换器
 * @author EmptyDreams
 */
@AutoTileEntity("red_stone_converter")
public class EMRedStoneConverter extends FrontTileEntity implements ITickable {

	public static final int VOLTAGE = EleEnergy.COMMON;
	
	@AutoSave
    private final ItemStackHandler item = new ItemStackHandler(1);
	private final SlotItemHandler INPUT = CommonUtil.createInputSlot(item, 0, 79, 20,
			stack -> stack.getItem() == Items.REDSTONE ||
					stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
	private final CommonProgress energyPro = new CommonProgress(
									CommonProgress.Style.STRIPE, CommonProgress.Front.RIGHT);
	private final CommonProgress burnPro = new CommonProgress(
									CommonProgress.Style.ARROW_DOWN, CommonProgress.Front.DOWN);
	@AutoSave private int burnTime = 0;
	@AutoSave private int maxTime = 0;

	public static int getBurnTime(ItemStack stack) {
		if (stack.getItem() == Items.REDSTONE) return 500;
		else if (stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) return 5000;
		return 0;
	}

	public EMRedStoneConverter() {
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
		return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
	}
	
	@Override
	public boolean onReceive(EleEnergy energy) {
		if (energy.getVoltage() > VOLTAGE) getCounter().plus();
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
	}
	
}