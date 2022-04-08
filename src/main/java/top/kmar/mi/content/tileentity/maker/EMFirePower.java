package top.kmar.mi.content.tileentity.maker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.auto.interfaces.AutoSave;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.electricity.clock.NonCounter;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.gui.component.CommonProgress;
import top.kmar.mi.api.gui.component.interfaces.IProgressBar;
import top.kmar.mi.api.register.others.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.ItemUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.CommonUtil;
import top.kmar.mi.content.blocks.CraftList;
import top.kmar.mi.content.capabilities.nonburn.NonBurnCapability;
import top.kmar.mi.data.properties.MIProperty;

/**
 * 火力发电机的TE
 * @author EmptyDreams
 */
@AutoTileEntity("fire_power")
public class EMFirePower extends FrontTileEntity implements ITickable {

	public static final int VOLTAGE = EleEnergy.COMMON;
	
	/** 工作进度条 */
	private final CommonProgress progressBar = new CommonProgress();
	/** 能量进度条 */
	private final CommonProgress energyPro = new CommonProgress();
	/** 输入/输出框 */
	@AutoSave
    private final ItemStackHandler item = new ItemStackHandler(2);
	/** 输入框 */
	private final SlotItemHandler in = CommonUtil.createInputSlot(item, 0, 52, 29,
											stack -> TileEntityFurnace.getItemBurnTime(stack) > 0 &&
													!stack.hasCapability(NonBurnCapability.NON_BURN, null));
	/** 输出框 */
	private final SlotItemHandler out = CommonUtil.createOutputSlot(item, 1, 106, 29);
	/** 已经燃烧的时长 */
	@AutoSave private int burningTime = 0;
	/** 最大燃烧时长 */
	@AutoSave private int maxTime = 0;
	/** 正在燃烧的物品 */
	@AutoSave private ItemElement burnItem;

	public EMFirePower() {
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
			state = old.withProperty(MIProperty.getWORKING(), true);
		} else {
			burnItem = null;
			state = old.withProperty(MIProperty.getWORKING(), false);
		}
		WorldUtil.setBlockState(world, pos, state);
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
	
	@Override
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
	}

}