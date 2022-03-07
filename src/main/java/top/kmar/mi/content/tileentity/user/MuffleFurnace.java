package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.gui.component.interfaces.IProgressBar;
import top.kmar.mi.api.tools.BaseTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.utils.data.io.Storage;
import top.kmar.mi.api.gui.component.CommonProgress;
import top.kmar.mi.api.gui.component.CommonProgress.Front;
import top.kmar.mi.api.gui.component.CommonProgress.Style;
import top.kmar.mi.api.register.others.AutoTileEntity;
import top.kmar.mi.data.info.MIProperty;

import static net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime;

/**
 * 高温火炉
 * @author EmptyDreams
 */
@AutoTileEntity("MuffleFurnace")
public class MuffleFurnace extends BaseTileEntity implements ITickable {
	
	@Storage
    private final ItemStackHandler item = new ItemStackHandler(3);
	/** 上方放置原料的输入框 */
	private final SlotItemHandler up = new SlotItemHandler(item, 0, 55, 21) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			ItemStack result = getResult(stack);
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
	
	@Storage private int maxWorkingTime = 0;
	@Storage private int workingTime = 0;
	@Storage private int maxBurningTime = 0;
	@Storage private int burningTime = 0;
	
	private final CommonProgress workProgress = new CommonProgress();
	private final CommonProgress burnProgress = new CommonProgress(Style.FIRE, Front.UP);
	
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
		IBlockState newState = oldState.withProperty(MIProperty.getWORKING(), isWorking());
		WorldUtil.setBlockState(world, pos, newState);
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
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
	}
	
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
	@SuppressWarnings("unused")
	public int getCookTime(ItemStack stack) {
		return 130;
	}
	
}