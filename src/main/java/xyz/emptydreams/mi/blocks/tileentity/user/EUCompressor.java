package xyz.emptydreams.mi.blocks.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
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
import xyz.emptydreams.mi.api.utils.data.auto.DataType;
import xyz.emptydreams.mi.blocks.CraftList;
import xyz.emptydreams.mi.blocks.base.MIProperty;
import xyz.emptydreams.mi.blocks.machine.user.CompressorBlock;
import xyz.emptydreams.mi.blocks.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.data.info.BiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumVoltage;

import static xyz.emptydreams.mi.api.utils.ItemUtil.hasEmpty;

/**
 * 压缩机的TileEntity，存储方块内物品、工作时间等内容
 * @author EmptyDreams
 */
@AutoTileEntity(CompressorBlock.NAME)
public class EUCompressor extends FrontTileEntity implements ITickable {
	
	/**
	 * 三个物品框<br>
	 * 	0-上端，1-下端，2-输出
	 */
	@Storage(DataType.SERIALIZABLE)
	private final ItemStackHandler item = new ItemStackHandler(3);
	private final SlotMI up = new SlotMI(item, 0, 56, 17);
	private final SlotMI down = new SlotMI(item, 1, 56, 53);
	private final SlotItemHandler out = new SlotItemHandler(item, 2, 125, 40) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	private final SlotGroup slotGroup = new AbstractSlotGroup(up, down);
	/** 已工作时间 */
	@Storage(DataType.INT)
	private int workingTime = 0;
	/** 进度条 */
	private final CommonProgress progressBar = new CommonProgress();
	/** 每次工作消耗的电能 */
	private int needEnergy = 10;
	
	public EUCompressor() {
		setReceiveRange(1, 20, EnumVoltage.C, EnumVoltage.D);
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setReceive(true);
		setMaxEnergy(20);
		progressBar.setCraftButton(CraftList.COMPRESSOR, te -> ((EUCompressor) te).slotGroup);
	}

	/** 设置世界时更新计数器的设置 */
	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);
		((OrdinaryCounter) getCounter()).setWorld(worldIn);
	}

	/** 设置坐标时更新计数器的设置 */
	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		((OrdinaryCounter) getCounter()).setPos(posIn);
	}
	
	@Override
	public void update() {
		if (world.isRemote) {
			WorldUtil.removeTickable(this);
			return;
		}
		progressBar.setMax(getNeedTime());
		
		//检查输入框是否合法 如果不合法则清零工作时间并结束函数
		ItemStack outStack = checkInput();
		if (outStack == null || getNowEnergy() < getNeedEnergy()) {
			whenFailed(outStack == null);
			return;
		}
		
		//若配方存在则继续计算
		boolean isWorking = updateData(outStack);
		progressBar.setNow(workingTime);
		updateShow(isWorking);
		markDirty();
	}

	/**
	 * 更新内部数据
	 * @param outStack 产品
	 * @return 是否正在工作
	 */
	private boolean updateData(ItemStack outStack) {
		boolean isWorking = false;  //保存是否正在工作
		ItemStack nowOut = out.getStack();
		//检查输入物品数目是否足够
		if (item.insertItem(2, outStack, true).isEmpty()) {
			isWorking = true;
			++workingTime;
			if (workingTime >= getNeedTime()) {
				workingTime = 0;
				item.extractItem(0, 1, false);
				item.extractItem(1, 1, false);
				nowOut.grow(outStack.getCount());
			}
			shrinkEnergy(getNeedEnergy());
		} else {
			workingTime = 0;
		}
		return isWorking;
	}

	/**
	 * 更新方块显示
	 * @param isWorking 是否正在工作
	 */
	private void updateShow(boolean isWorking) {
		IBlockState old = world.getBlockState(pos);
		IBlockState state = old.withProperty(MIProperty.EMPTY, isEmpty())
				                    .withProperty(MIProperty.WORKING, isWorking);
		WorldUtil.setBlockState(world, pos, old, state);
	}
	
	/**
	 * 检查输入内容并获取输出
	 * @return 返回产品，若输入不合法则返回null
	 */
	private ItemStack checkInput() {
		if (!hasEmpty(up.getStack(), down.getStack())) {
			ItemSet set = new ItemSet();
			set.add(ItemElement.instance(up.getStack()));
			set.add(ItemElement.instance(down.getStack()));
			ItemElement craft = CraftList.COMPRESSOR.apply(set);
			if (craft == null) return null;
			return craft.getStack();
		}
		return null;
	}
	
	/**
	 * 当工作失败时执行替换方块的操作
	 * @param isOutputFailed 是否是因为合成表不存在导致的失败
	 */
	private void whenFailed(boolean isOutputFailed) {
		//如果不存在，更新方块显示
		if (isOutputFailed) workingTime = 0;
		updateShow(false);
		progressBar.setNow(workingTime);
		markDirty();
	}
	
	/** 获取需要的工作时间 */
	public int getNeedTime() { return 100; }
	/** 获取已工作时间 */
	public int getWorkingTime() { return workingTime; }
	/** 获取每Tick需要的能量 */
	public int getNeedEnergy() { return needEnergy; }
	/** 设置每Tick需要的能量 */
	public void setNeedEnergy(int needEnergy) { this.needEnergy = needEnergy; }
	/** 判断输入是否为空 */
	public boolean isEmptyForInput() {
		return hasEmpty(up.getStack(), down.getStack());
	}
	/** 判断是否为空 */
	public boolean isEmpty() {
		return hasEmpty(up.getStack(), down.getStack(), out.getStack());
	}
	/** 获取进度条 */
	public IProgressBar getProgressBar() { return progressBar; }
	
	/**
	 * @param index 0-上端，1-下端，2-输出
	 */
	public SlotItemHandler getSlot(int index) {
		switch (index) {
			case 0 : return up;
			case 1 : return down;
			case 2 : return out;
			default : return null;
		}
	}
	
	@Override
	public boolean isReAllowable(EnumFacing facing) {
		return true;
	}
	@Override
	public boolean isExAllowable(EnumFacing facing) {
		return false;
	}
	@Override
	public EnumFacing getFront() { return world.getBlockState(pos).getValue(MIProperty.HORIZONTAL); }
	
	private final class SlotMI extends SlotItemHandler {
		
		public SlotMI(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			boolean b = stack != null && CraftList.COMPRESSOR.haveInput(stack)
					            && super.isItemValid(stack);
			if (b && !isEmptyForInput()) {
				SlotItemHandler s = getSlot(getSlotIndex() == 0 ? 1 : 0);
				if (s.getHasStack() && !getHasStack()) {
					b = s.getStack().getItem().equals(getStack().getItem());
				}
			}
			
			return b;
		}
		
	}
	
}