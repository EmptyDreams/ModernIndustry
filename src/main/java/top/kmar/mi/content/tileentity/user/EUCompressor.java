package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.electricity.info.BiggerVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage;
import top.kmar.mi.api.register.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.ItemUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.CraftList;
import top.kmar.mi.content.blocks.machine.user.CompressorBlock;
import top.kmar.mi.data.properties.MIProperty;

/**
 * 压缩机的TileEntity，存储方块内物品、工作时间等内容
 * @author EmptyDreams
 */
@AutoTileEntity(CompressorBlock.NAME)
public class EUCompressor extends FrontTileEntity implements ITickable {
	
	public static final int VOLTAGE = EleEnergy.COMMON;
	
	/**
	 * 三个物品框<br>
	 * 	0-上端，1-下端，2-输出
	 */
	@AutoSave
    private final ItemStackHandler item = new ItemStackHandler(3);
	private final SlotMI up = new SlotMI(item, 0, 56, 17);
	private final SlotMI down = new SlotMI(item, 1, 56, 53);
	private final SlotItemHandler out = new SlotItemHandler(item, 2, 125, 40) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	//TODO
	//private final SlotGroup slotGroup = new AbstractSlotGroup(up, down);
	/** 已工作时间 */
	@AutoSave private int workingTime = 0;
	/** 每次工作消耗的电能 */
	private int needEnergy = 10;
	
	public EUCompressor() {
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setMaxEnergy(20);
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
		
		//检查输入框是否合法 如果不合法则清零工作时间并结束函数
		ItemStack outStack = checkInput();
		if (outStack == null || getNowEnergy() < getNeedEnergy()) {
			whenFailed(outStack == null);
			return;
		}
		
		//若配方存在则继续计算
		boolean isWorking = updateData(outStack);
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
		IBlockState state = old.withProperty(MIProperty.getEMPTY(), isEmpty())
				                    .withProperty(MIProperty.getWORKING(), isWorking);
		WorldUtil.setBlockState(world, pos, state);
	}
	
	/**
	 * 检查输入内容并获取输出
	 * @return 返回产品，若输入不合法则返回null
	 */
	private ItemStack checkInput() {
		if (!ItemUtil.hasEmpty(up.getStack(), down.getStack())) {
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
		return ItemUtil.hasEmpty(up.getStack(), down.getStack());
	}
	/** 判断是否为空 */
	public boolean isEmpty() {
		return ItemUtil.hasEmpty(up.getStack(), down.getStack(), out.getStack());
	}
	
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
	public boolean onReceive(EleEnergy energy) {
		if (energy.getVoltage() > VOLTAGE) getCounter().plus();
		return true;
	}
	
	@Override
	public boolean isReceiveAllowable(EnumFacing facing) {
		return true;
	}
	@Override
	public boolean isExtractAllowable(EnumFacing facing) {
		return false;
	}
	
	@Override
	public int getExVoltage() {
		return EleEnergy.COMMON;
	}
	
	@Override
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
	}
	
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