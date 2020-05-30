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
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.api.electricity.src.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.api.gui.component.MProgressBar;
import xyz.emptydreams.mi.api.gui.component.StringComponent;
import xyz.emptydreams.mi.api.utils.data.DataType;
import xyz.emptydreams.mi.blocks.base.MIProperty;
import xyz.emptydreams.mi.register.te.AutoTileEntity;

/**
 * 火力发电机的TE
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTileEntity("front_tileentity")
public class EMFirePower extends FrontTileEntity implements ITickable {
	
	private final MProgressBar progressBar = new MProgressBar();
	private final MProgressBar energyPro = new MProgressBar();
	private final StringComponent stringShower = new StringComponent() {
		@Override
		public void send(Container con, IContainerListener listener) {
			listener.sendWindowProperty(con, getCode(), getNowEnergy());
		}
		
		@Override
		public boolean update(int codeID, int data) {
			if (codeID == getCode()) {
				((EMFirePower) world.getTileEntity(pos)).setNowEnergy(data);
				return true;
			}
			return false;
		}
	};
	@Storage(type = DataType.OTHER)
	private final ItemStackHandler item = new ItemStackHandler(3);
	private final SlotItemHandler in = new SlotItemHandler(item, 0, 52, 29) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return super.isItemValid(stack) &&
					       TileEntityFurnace.getItemBurnTime(stack) > 0;
		}
		@Override
		public int getItemStackLimit(ItemStack stack) {
			return 64;
		}
	};
	private final SlotItemHandler out = new SlotItemHandler(item, 1, 106, 29) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	/** 已经燃烧的时长 */
	private int burningTime = 0;
	/** 最大燃烧时长 */
	private int maxTime = 0;
	
	public EMFirePower() {
		setExtractRange(1, 120, EnumVoltage.LOWER, EnumVoltage.HIGH);
		setExtract(true);
		setReceive(false);
		setMaxEnergy(10000);
		setCounter(NonCounter.getInstance());
		progressBar.setLocation(76, 30);
		progressBar.set(0);
		energyPro.setLocation(44, 51);
		energyPro.setStyle(MProgressBar.EnumStyle.STRIPE);
		energyPro.setMax(getMaxEnergy());
		energyPro.set(0);
	}
	
	@SideOnly(Side.CLIENT) private int cache = -1;
	@Override
	public void update() {
		if (world.isRemote) {
			if (cache != getNowEnergy()) {
				cache = getNowEnergy();
				stringShower.setString(cache + " / " + getMaxEnergy());
				stringShower.setLocation(
						(176 - Minecraft.getMinecraft().fontRenderer
								       .getStringWidth(stringShower.getString())) / 2, 56);
			}
			return;
		}
		if (maxTime <= 0) {
			ItemStack stack = in.getStack();
			IBlockState old = world.getBlockState(pos);
			IBlockState state;
			if (!stack.isEmpty() && getNowEnergy() < getMaxEnergy() / 10 * 9) {
				maxTime = TileEntityFurnace.getItemBurnTime(stack);
				stack.shrink(1);
				state = old.withProperty(MIProperty.WORKING, true);
			} else {
				maxTime = 0;
				state = old.withProperty(MIProperty.WORKING, false);
			}
			if (!old.equals(state)) {
				world.setBlockState(pos, state);
				world.markBlockRangeForRenderUpdate(pos, pos);
			}
			progressBar.setMax(maxTime);
		} else {
			if ((burningTime += 15) >= maxTime) {
				maxTime = burningTime = 0;
			}
			progressBar.set(burningTime);
			setNowEnergy(getNowEnergy() + 120);
		}
		
		energyPro.set(getNowEnergy());
		stringShower.setString(getNowEnergy() + " / " + getMaxEnergy());
	}
	
	public StringComponent getStringShower() { return stringShower; }
	public MProgressBar getEnergyProBar() { return energyPro; }
	public MProgressBar getProgressBar() { return progressBar; }
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
