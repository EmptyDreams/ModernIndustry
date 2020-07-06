package xyz.emptydreams.mi.blocks.te.maker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.electricity.clock.NonCounter;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.IProgressBar;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.blocks.CommonUtil;
import xyz.emptydreams.mi.blocks.base.MIProperty;
import xyz.emptydreams.mi.blocks.te.FrontTileEntity;
import xyz.emptydreams.mi.data.info.EnumVoltage;
import xyz.emptydreams.mi.register.te.AutoTileEntity;

import static xyz.emptydreams.mi.api.utils.data.DataType.INT;
import static xyz.emptydreams.mi.api.utils.data.DataType.OTHER;

/**
 * 火力发电机的TE
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTileEntity("fire_power")
public class EMFirePower extends FrontTileEntity implements ITickable {

	/** 工作进度条 */
	private final CommonProgress progressBar = new CommonProgress();
	/** 能量进度条 */
	private final CommonProgress energyPro = new CommonProgress();
	/** 能量数据显示 */
	private final StringComponent stringShower = new StringComponent() {
		@Override
		public void send(Container con, IContainerListener listener) {
			listener.sendWindowProperty(con, getCodeID(0), getNowEnergy());
		}
		
		@Override
		public boolean update(int codeID, int data) {
			if (codeID == getCodeID(0)) {
				((EMFirePower) world.getTileEntity(pos)).setNowEnergy(data);
				return true;
			}
			return false;
		}
	};
	/** 输入/输出框 */
	@Storage(OTHER) private final ItemStackHandler item = new ItemStackHandler(2);
	/** 输入框 */
	private final SlotItemHandler in = CommonUtil.createInputSlot(item, 0, 52, 29,
											stack -> TileEntityFurnace.getItemBurnTime(stack) > 0);
	/** 输出框 */
	private final SlotItemHandler out = CommonUtil.createOutputSlot(item, 1, 106, 29);
	/** 已经燃烧的时长 */
	@Storage(INT) private int burningTime = 0;
	/** 最大燃烧时长 */
	@Storage(INT) private int maxTime = 0;

	public EMFirePower() {
		setExtractRange(1, 120, EnumVoltage.C, EnumVoltage.E);
		setExtract(true);
		setReceive(false);
		setMaxEnergy(10000);
		setCounter(NonCounter.getInstance());
		progressBar.setLocation(76, 30);
		energyPro.setLocation(44, 51);
		energyPro.setStyle(CommonProgress.Style.STRIPE);
		energyPro.setMax(getMaxEnergy());
	}

	@Override
	public void update() {
		if (world.isRemote) {
			updateStringShower();
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
			stack.shrink(1);
			state = old.withProperty(MIProperty.WORKING, true);
		} else {
			state = old.withProperty(MIProperty.WORKING, false);
		}
		WorldUtil.setBlockState(world, pos, old, state);
		progressBar.setMax(maxTime);
	}

	/** 更新燃烧时间 */
	private void updateBurningTime() {
		if ((burningTime += 5) >= maxTime) {
			maxTime = burningTime = 0;
		}
		progressBar.setNow(burningTime);
		setNowEnergy(getNowEnergy() + 30);
		markDirty();
	}

	@SideOnly(Side.CLIENT) private int cache = -1;
	/** 更新客户端进度条下方的文字显示 */
	@SideOnly(Side.CLIENT)
	private void updateStringShower() {
		if (cache != getNowEnergy()) {
			cache = getNowEnergy();
			stringShower.setString(cache + " / " + getMaxEnergy());
			stringShower.setLocation(
					(176 - Minecraft.getMinecraft().fontRenderer
							.getStringWidth(stringShower.getString())) / 2, 56);
		}
	}

	public StringComponent getStringShower() { return stringShower; }
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
		return world.getBlockState(pos).getValue(MIProperty.FACING);
	}

}
