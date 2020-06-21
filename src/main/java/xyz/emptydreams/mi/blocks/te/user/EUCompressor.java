package xyz.emptydreams.mi.blocks.te.user;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.craftguide.CraftRegistry;
import xyz.emptydreams.mi.api.craftguide.ICraftGuide;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.ULCraftGuide;
import xyz.emptydreams.mi.api.electricity.clock.OrdinaryCounter;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.IProgressBar;
import xyz.emptydreams.mi.api.utils.data.DataType;
import xyz.emptydreams.mi.blocks.base.MIProperty;
import xyz.emptydreams.mi.blocks.machine.user.CompressorBlock;
import xyz.emptydreams.mi.blocks.te.FrontTileEntity;
import xyz.emptydreams.mi.data.info.BiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumVoltage;
import xyz.emptydreams.mi.register.item.ItemRegister;
import xyz.emptydreams.mi.register.te.AutoTileEntity;

import static xyz.emptydreams.mi.api.utils.ItemUtil.hasEmpty;
import static xyz.emptydreams.mi.api.utils.ItemUtil.merge;

/**
 * 压缩机的TileEntity，存储方块内物品、工作时间等内容
 * @author EmptyDreams
 * @version V1.2
 */
@AutoTileEntity(CompressorBlock.NAME)
public class EUCompressor extends FrontTileEntity implements ITickable {
	
	/** 压缩机的合成表 */
	public static final CraftRegistry REGISTRY = CraftRegistry.instance(
			new ResourceLocation(ModernIndustry.MODID, CompressorBlock.NAME));
	
	static {
		ULCraftGuide craft0 = new ULCraftGuide(2)
				                      .addElement(ItemElement.instance(ItemRegister.ITEM_COPPER_POWDER, 2))
				                      .addOutElement(ItemElement.instance(ItemRegister.ITEM_COPPER, 1));
		ULCraftGuide craft1 = new ULCraftGuide(2)
				                      .addElement(ItemElement.instance(ItemRegister.ITEM_TIN_POWER, 2))
				                      .addOutElement(ItemElement.instance(ItemRegister.ITEM_TIN, 1));
		
		REGISTRY.register(craft0, craft1);
	}
	
	/**
	 * 三个物品框<br>
	 * 	0-上端，1-下端，2-输出
	 */
	@Storage(value = DataType.OTHER)
	private final ItemStackHandler item = new ItemStackHandler(3);
	private final SlotMI up = new SlotMI(item, 0, 56, 17);
	private final SlotMI down = new SlotMI(item, 1, 56, 53);
	private final SlotItemHandler out = new SlotItemHandler(item, 2, 120, 34) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	/** 已工作时间 */
	@Storage(value = DataType.INT)
	private int workingTime = 0;
	/** 进度条 */
	private final CommonProgress progressBar = new CommonProgress();
	/** 每次工作消耗的电能 */
	private int needEnergy = 15;
	
	public EUCompressor() {
		setReciveRange(1, 50, EnumVoltage.LOWER, EnumVoltage.ORDINARY);
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setReceive(true);
		setMaxEnergy(100);
		
		progressBar.setLocation(80, 35);
	}
	
	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);
		((OrdinaryCounter) getCounter()).setWorld(worldIn);
	}
	
	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		((OrdinaryCounter) getCounter()).setPos(posIn);
	}
	
	@Override
	public void update() {
		if (world.isRemote) return;
		progressBar.setMax(getNeedTime());
		
		//检查输入框是否合法 如果不合法则清零工作时间并结束函数
		ItemStack outStack = checkInput();
		if (outStack == null || getNowEnergy() < getNeedEnergy()) {
			whenFaild(outStack == null);
			return;
		}
		
		//若配方存在则继续计算
		boolean isWorking = false;  //保存是否正在工作
		ItemStack nowOut = out.getStack();
		//检查输入物品数目是否足够
		if (item.insertItem(2, outStack, true).isEmpty()) {
			if (nowOut.getCount() == 0) out.putStack(ItemStack.EMPTY);
			isWorking = true;
			++workingTime;
			if (workingTime >= getNeedTime()) {
				workingTime = 0;
				item.extractItem(0, 1, false);
				item.extractItem(1, 1, false);
				out.putStack(new ItemStack(outStack.getItem(),
						outStack.getCount() + this.out.getStack().getCount()));
			}
			shrinkEnergy(getNeedEnergy());
		} else {
			workingTime = 0;
		}
		progressBar.setNow(workingTime);
		updateShow(isWorking);
		markDirty();
	}
	
	/**
	 * 更新方块显示
	 * @param isWorking 是否正在工作
	 */
	private void updateShow(boolean isWorking) {
		IBlockState old = world.getBlockState(pos);
		IBlockState state = old.withProperty(MIProperty.EMPTY, isEmpty())
				                    .withProperty(MIProperty.WORKING, isWorking);
		if (!old.equals(state)) {
			world.setBlockState(pos, state);
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}
	
	/**
	 * 检查输入内容并获取输出
	 * @return 返回产品，若输入不合法则返回null
	 */
	private ItemStack checkInput() {
		if (!hasEmpty(up.getStack(), down.getStack())) {
			List<ItemStack> inputs = merge(up.getStack(), down.getStack());
			Optional<ICraftGuide> craft = REGISTRY.apply(inputs);
			return craft.map(it -> it.getOuts().get(0).getStack()).orElse(null);
		}
		return null;
	}
	
	/**
	 * 当工作失败时执行替换方块的操作
	 * @param isOutputFaild 是否是因为合成表不存在导致的失败
	 */
	private void whenFaild(boolean isOutputFaild) {
		//如果不存在，更新方块显示
		if (isOutputFaild) workingTime = 0;
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
	public SlotItemHandler getSolt(int index) {
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
	public EnumFacing getFront() { return world.getBlockState(pos).getValue(MIProperty.FACING); }
	
	private final class SlotMI extends SlotItemHandler {
		
		final int index;
		
		public SlotMI(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
			this.index = index;
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			boolean b = stack != null && REGISTRY.hasItem(stack.getItem())
					            && super.isItemValid(stack);
			if (b && !isEmptyForInput()) {
				SlotItemHandler s = getSolt(index == 0 ? 1 : 0);
				if (s.getHasStack() && !getHasStack()) {
					b &= s.getStack().getItem().equals(getStack().getItem());
				}
			}
			
			return b;
		}
		
		@Override
		public int getItemStackLimit(ItemStack stack) {
			return 64;
		}
		
	}
	
}
