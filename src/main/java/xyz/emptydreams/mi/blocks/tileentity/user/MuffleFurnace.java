package xyz.emptydreams.mi.blocks.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.CommonProgress.Front;
import xyz.emptydreams.mi.api.gui.component.CommonProgress.Style;
import xyz.emptydreams.mi.api.gui.component.IProgressBar;
import xyz.emptydreams.mi.api.tools.BaseTileEntity;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.register.tileentity.AutoTileEntity;

import static net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime;
import static xyz.emptydreams.mi.api.utils.data.DataType.INT;
import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

/**
 * 高温火炉
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTileEntity("MuffleFurnace")
public class MuffleFurnace extends BaseTileEntity implements ITickable {
	
	@Storage private final ItemStackHandler item = new ItemStackHandler(3);
	/** 上方放置原料的输入框 */
	private final SlotItemHandler up = new SlotItemHandler(item, 0, 55, 21) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
			if (getHasStack()) return super.isItemValid(stack) &&
					                          result.getItem() == getResult(getStack()).getItem();
			else return super.isItemValid(stack) && !result.isEmpty();
		}
	};
	/** 下方放置燃料的输入框 */
	private final SlotItemHandler down = new SlotItemHandler(item, 1, 55, 57) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			//防止折叠
			return super.isItemValid(stack) && getItemBurnTime(stack) > 0;
		}
	};
	/** 产品输出 */
	private final SlotItemHandler out = new SlotItemHandler(item, 2, 111, 38) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	
	@Storage(INT) private int maxWorkingTime = 0;
	@Storage(INT) private int workingTime = 0;
	@Storage(INT) private int maxBurningTime = 0;
	@Storage(INT) private int burningTime = 0;
	
	private final CommonProgress workProgress = new CommonProgress();
	private final CommonProgress burnProgress = new CommonProgress(Style.FIRE, Front.UP);
	
	public MuffleFurnace() {
		workProgress.setLocation(80, 40);
		burnProgress.setLocation(57, 42);
	}
	
	@Override
	public void update() {
		if (world.isRemote) {
			WorldUtil.removeTickable(this);
			return;
		}
		if (updateBurningTime()) {
			ItemStack result = out.getStack();
			if (result.getCount() < result.getMaxStackSize()) updateWorkingTime();
		} else {
			workingTime = maxWorkingTime = 0;
			workProgress.setNow(workingTime);
		}
		
		if (!up.getHasStack()) {
			workingTime = maxWorkingTime = 0;
			workProgress.setNow(workingTime);
			return;
		}
		
		IBlockState oldState = world.getBlockState(pos);
		IBlockState newState = oldState.withProperty(WORKING, isWorking());
		WorldUtil.setBlockState(world, pos, oldState, newState);
	}
	
	/** 更新工作时间 */
	protected void updateWorkingTime() {
		if (maxWorkingTime == 0) {
			maxWorkingTime = getCookTime(up.getStack());
			up.decrStackSize(1);
			workProgress.setMax(maxWorkingTime);
		}
		if (++workingTime >= maxWorkingTime) {
			if (out.getStack().isEmpty()) out.putStack(getResult(up.getStack()));
			else out.getStack().grow(1);
			maxWorkingTime = workingTime = 0;
		}
		workProgress.setNow(workingTime);
	}
	
	/** 更新燃烧时间 */
	protected boolean updateBurningTime() {
		if (++burningTime >= maxBurningTime) {
			burningTime = maxBurningTime = 0;
		}
		if (maxBurningTime == 0 && down.getHasStack() && up.getHasStack()) {
			if (down.getHasStack()) {
				maxBurningTime = getItemBurnTime(down.getStack());
				down.getStack().shrink(1);
				return true;
			} else {
				maxBurningTime = 0;
				return false;
			}
		}
		burnProgress.setMax(maxBurningTime);
		burnProgress.setNow(burningTime);
		return isWorking();
	}
	
	/** 是否正在工作 */
	public boolean isWorking() { return maxBurningTime != 0; }
	/** 获取正面 */
	public EnumFacing getFront() { return world.getBlockState(pos).getValue(FACING); }
	
	/** 获取工作进度条 */
	public IProgressBar getWorkProgress() { return workProgress; }
	/** 获取燃烧进度条 */
	public IProgressBar getBurnProgress() { return burnProgress; }
	public SlotItemHandler getUp() { return up; }
	public SlotItemHandler getDown() { return down; }
	public SlotItemHandler getOut() { return out; }
	
	public static ItemStack getResult(ItemStack in) {
		return FurnaceRecipes.instance().getSmeltingResult(in).copy();
	}
	
	/** 获取工作需要的时间 */
	public int getCookTime(ItemStack stack) {
		return 130;
	}
	
}
