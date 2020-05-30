package xyz.emptydreams.mi.blocks.te.user;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.DictDisorderList;
import xyz.emptydreams.mi.api.electricity.clock.OrdinaryCounter;
import xyz.emptydreams.mi.api.electricity.src.info.BiggerVoltage;
import xyz.emptydreams.mi.api.electricity.src.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.api.electricity.src.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.api.gui.component.MProgressBar;
import xyz.emptydreams.mi.api.utils.data.DataType;
import xyz.emptydreams.mi.blocks.base.MIProperty;
import xyz.emptydreams.mi.blocks.machine.user.CompressorBlock;
import xyz.emptydreams.mi.register.item.ItemRegister;
import xyz.emptydreams.mi.register.te.AutoTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * 压缩机的TileEntity，存储方块内物品、工作时间等内容
 * @author EmptyDreams
 * @version V1.2
 */
@AutoTileEntity(CompressorBlock.NAME)
public class EUCompressor extends FrontTileEntity implements ITickable {
	
	/** 压缩机的合成表 */
	public static final CraftGuide<ItemStack, ItemStack> CRAFT_GUIDE = new CraftGuide<>();
	
	static {
		CRAFT_GUIDE.register(DictDisorderList.create(ItemRegister.ITEM_COPPER, 1,
				new ItemStack(ItemRegister.ITEM_COPPER_POWDER, 2)));
		CRAFT_GUIDE.register(DictDisorderList.create(ItemRegister.ITEM_TIN, 1,
				new ItemStack(ItemRegister.ITEM_TIN_POWER, 2)));
	}
	
	/** 已工作时间 */
	@Storage(type = DataType.INT)
	private int workingTime = 0;
	/**
	 * 三个物品框<br>
	 * 	0-上端，1-下端，2-输出
	 */
	@Storage(type = DataType.OTHER)
	private final ItemStackHandler item = new ItemStackHandler(3);
	private final SlotMI up = new SlotMI(item, 0, 56, 17);
	private final SlotMI down = new SlotMI(item, 1, 56, 53);
	private final SlotItemHandler out = new SlotItemHandler(item, 2, 120, 34) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	/** 进度条 */
	private final MProgressBar progressBar = new MProgressBar();
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
	public NBTTagCompound getUpdateTag() {
		if (getCounter() instanceof OrdinaryCounter) {
			OrdinaryCounter counter = (OrdinaryCounter) getCounter();
			counter.setWorld(world);
			counter.setPos(pos);
		}
		return super.getUpdateTag();
	}
	
	@Override
	public void update() {
		if (world.isRemote) return;
		progressBar.setMax(getNeedTime());
		//检查输入框是否合法 如果不合法则清零工作时间并结束函数
		DictDisorderList guide = new DictDisorderList(2, null, 0);
		guide.add(new ItemStack(up.getStack().getItem())); guide.add(new ItemStack(down.getStack().getItem()));
		ItemStack outStack = CRAFT_GUIDE.get(guide);
		IBlockState old = world.getBlockState(pos);
		//判断配方是否存在
		if (outStack == null || getNowEnergy() < getNeedEnergy()) {
			//如果不存在，更新方块显示
			if (outStack == null) workingTime = 0;
			IBlockState state = old.withProperty(MIProperty.WORKING, false)
					                    .withProperty(MIProperty.EMPTY, isEmpty());
			world.setBlockState(pos, state);
			world.markBlockRangeForRenderUpdate(pos, pos);
			progressBar.set(workingTime);
			markDirty();
			return;
		}
		//若配方存在则继续计算
		ItemStack itemStack = item.extractItem(0, 1, true);
		ItemStack itemStack2 = item.extractItem(1, 1, true);
		boolean isWorking = false;  //保存是否正在工作
		
		//检查输入物品数目是否足够
		if (!(itemStack.equals(ItemStack.EMPTY) && itemStack2.equals(ItemStack.EMPTY) &&
				      item.insertItem(2, outStack, true).equals(ItemStack.EMPTY))) {
			if (this.out.getStack().getCount() == 0) {
				this.out.putStack(ItemStack.EMPTY);
			}
			isWorking = true;
			++workingTime;
			if (workingTime >= getNeedTime()) {
				workingTime = 0;
				item.extractItem(0, 1, false);
				item.extractItem(1, 1, false);
				this.out.putStack(new ItemStack(outStack.getItem(),
						outStack.getCount() + this.out.getStack().getCount()));
			}
			setNowEnergy(getNowEnergy() - getNeedEnergy());
		} else {
			workingTime = 0;
		}
		progressBar.set(workingTime);
		IBlockState state = old.withProperty(MIProperty.EMPTY, isEmpty())
				                    .withProperty(MIProperty.WORKING, isWorking);
		if (!old.equals(state)) world.setBlockState(pos, state);
		markDirty();
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	/** 获取需要的工作时间 */
	public int getNeedTime() { return 135; }
	/** 获取已工作时间 */
	public int getWorkingTime() { return workingTime; }
	/** 获取每Tick需要的能量 */
	public int getNeedEnergy() { return needEnergy; }
	/** 设置每Tick需要的能量 */
	public void setNeedEnergy(int needEnergy) { this.needEnergy = needEnergy; }
	/** 判断输入是否为空 */
	public boolean isEmptyForInput() {
		return up.getHasStack() || down.getHasStack();
	}
	/** 判断是否为空 */
	public boolean isEmpty() {
		return up.getHasStack() || down.getHasStack() || out.getHasStack();
	}
	/** 获取进度条 */
	public MProgressBar getProgressBar() { return progressBar; }
	
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
	public EnumFacing getFront() {
		return world.getBlockState(pos).getValue(MIProperty.FACING);
	}
	
	private final class SlotMI extends SlotItemHandler {
		
		int index;
		
		public SlotMI(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
			this.index = index;
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			boolean b = stack != null && CRAFT_GUIDE.contains(stack)
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
