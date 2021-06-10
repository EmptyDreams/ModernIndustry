package xyz.emptydreams.mi.content.blocks.tileentity.maker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.electricity.clock.NonCounter;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.interfaces.IProgressBar;
import xyz.emptydreams.mi.api.register.others.AutoTileEntity;
import xyz.emptydreams.mi.api.utils.ItemUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;
import xyz.emptydreams.mi.content.blocks.CommonUtil;
import xyz.emptydreams.mi.content.blocks.CraftList;
import xyz.emptydreams.mi.content.blocks.base.MIProperty;
import xyz.emptydreams.mi.content.blocks.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.content.capabilities.nonburn.NonBurnCapability;
import xyz.emptydreams.mi.data.info.EnumVoltage;

/**
 * 火力发电机的TE
 * @author EmptyDreams
 */
@AutoTileEntity("fire_power")
public class EMFirePower extends FrontTileEntity implements ITickable {

	/** 工作进度条 */
	private final CommonProgress progressBar = new CommonProgress();
	/** 能量进度条 */
	private final CommonProgress energyPro = new CommonProgress();
	/** 输入/输出框 */
	@Storage private final ItemStackHandler item = new ItemStackHandler(2);
	/** 输入框 */
	private final SlotItemHandler in = CommonUtil.createInputSlot(item, 0, 52, 29,
											stack -> TileEntityFurnace.getItemBurnTime(stack) > 0 &&
													!stack.hasCapability(NonBurnCapability.NON_BURN, null));
	/** 输出框 */
	private final SlotItemHandler out = CommonUtil.createOutputSlot(item, 1, 106, 29);
	/** 已经燃烧的时长 */
	@Storage private int burningTime = 0;
	/** 最大燃烧时长 */
	@Storage private int maxTime = 0;
	/** 正在燃烧的物品 */
	@Storage private ItemElement burnItem;

	public EMFirePower() {
		setExtractRange(1, 120, EnumVoltage.C, EnumVoltage.E);
		setExtract(true);
		setReceive(false);
		setMaxEnergy(10000);
		setCounter(NonCounter.getInstance());
		energyPro.setStyle(CommonProgress.Style.STRIPE);
		energyPro.setMax(getMaxEnergy());
		energyPro.setStringShower(CommonProgress.ProgressStyle.DOWN);
	}

	@Override
	public void update() {
		if (world.isRemote) {
			WorldUtil.removeTickable(this);
			return;
		}
		if (maxTime <= 0) burnItem();
		else updateBurningTime();
		
		energyPro.setNow(getNowEnergy());
	}

	/** 燃烧输入框中的物品 */
	private void burnItem() {
		ItemStack stack = in.getStack();
		IBlockState old = world.getBlockState(pos);
		IBlockState state;
		if (!stack.isEmpty() && getNowEnergy() < getMaxEnergy() / 10 * 5) {
			maxTime = TileEntityFurnace.getItemBurnTime(stack);
			burnItem = ItemElement.instance(stack.getItem(), 1);
			stack.shrink(1);
			state = old.withProperty(MIProperty.WORKING, true);
		} else {
			burnItem = null;
			state = old.withProperty(MIProperty.WORKING, false);
		}
		WorldUtil.setBlockState(world, pos, old, state);
		progressBar.setMax(maxTime);
		markDirty();
	}

	/** 更新输出 */
	private void updateProduction() {
		maxTime = burningTime = 0;
		ItemElement element = CraftList.FIRE_POWER.apply(new ItemSet(burnItem));
		if (element == null) return;
		ItemUtil.putItemTo(out.getStack(), element.getStack(), false);
	}

	/** 更新燃烧时间 */
	private void updateBurningTime() {
		if ((burningTime += 5) >= maxTime) updateProduction();
		progressBar.setNow(burningTime);
		setNowEnergy(getNowEnergy() + 30);
		markDirty();
	}

	//public StringComponent getStringShower() { return stringShower; }
	public IProgressBar getEnergyProBar() { return energyPro; }
	public IProgressBar getProgressBar() { return progressBar; }
	public SlotItemHandler getInSlot() { return in; }
	public SlotItemHandler getOutSlot() { return out; }
	
	@Override
	public boolean isReAllowable(EnumFacing facing) {
		return false;
	}
	
	@Override
	public boolean isExAllowable(EnumFacing facing) {
		return facing != getFront();
	}
	
	@Override
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.HORIZONTAL);
	}

}