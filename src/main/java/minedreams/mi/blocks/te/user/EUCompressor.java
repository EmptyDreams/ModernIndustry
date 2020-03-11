package minedreams.mi.blocks.te.user;

import minedreams.mi.api.craftguide.CraftGuide;
import minedreams.mi.api.craftguide.CraftGuideItems;
import minedreams.mi.api.craftguide.CraftGuideManager;
import minedreams.mi.api.electricity.EleWorker;
import minedreams.mi.api.electricity.src.info.BiggerVoltage;
import minedreams.mi.api.electricity.src.info.EnumBiggerVoltage;
import minedreams.mi.api.electricity.src.tileentity.EleSrcUser;
import minedreams.mi.blocks.machine.user.CompressorToolBlock;
import minedreams.mi.register.te.AutoTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import static minedreams.mi.blocks.machine.user.CompressorToolBlock.EMPTY;
import static minedreams.mi.blocks.machine.user.CompressorToolBlock.WORKING;

/**
 * 压缩机的TileEntity，存储方块内物品、工作时间等内容
 * @author EmptyDreams
 * @version V1.2
 */
@AutoTileEntity(CompressorToolBlock.NAME)
public class EUCompressor extends EleSrcUser {
	
	/** 已工作时间 */
	private int workingTime = 0;
	/** 三个物品框<br>
	 * 	0-上端，1-下端，2-输出
	 */
	private final ItemStackHandler item = new ItemStackHandler(3);
	private final SlotMI up = new SlotMI(item, 0, 56, 17, this);
	private final SlotMI down = new SlotMI(item, 1, 56, 53, this);
	private final SlotItemHandler out = new SlotItemHandler(item, 2, 116, 34) {
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	};
	
	public EUCompressor() {
		setBiggerMaxTime(100);
		setBiggerVoltageOperate(new BiggerVoltage(3, EnumBiggerVoltage.FIRE));
		setEnergy(200);
		setEnergyMin(200);
	}
	
	@Override
	public void update() {
		super.update();
		//检查输入框是否合法 如果不合法则清零工作时间并结束函数
		CraftGuide cgi = isLawful();
		if (cgi == null) return;
		ItemStack itemStack = item.extractItem(0, 1, true);
		ItemStack itemStack2 = item.extractItem(1, 1, true);
		ItemStack out = cgi.getOuts().get(0);
		
		boolean isWorking = true;
		
		//检查输入物品数目是否足够
		if (!(itemStack.equals(ItemStack.EMPTY) && itemStack2.equals(ItemStack.EMPTY) &&
				      item.insertItem(2, out, true).equals(ItemStack.EMPTY))) {
			if (this.out.getStack().getCount() == 0) this.out.putStack(ItemStack.EMPTY);
			if (EleWorker.useEleEnergy(this) != null) {
				isWorking = false;
				++workingTime;
				if (workingTime >= getNeedTime()) {
					workingTime = 0;
					item.extractItem(0, 1, false);
					item.extractItem(1, 1, false);
					this.out.putStack(new ItemStack(out.getItem(),
							out.getCount() + this.out.getStack().getCount()));
				}
			}
		}
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.withProperty(EMPTY, isEmpty()).withProperty(WORKING, isWorking));
		markDirty();
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	/** 判断输入是否合法 */
	public CraftGuide isLawful() {
		CraftGuideItems in = new CraftGuideItems().add(up.getStack().getItem(), 1)
				                     .add(down.getStack().getItem(), 1);
		return CraftGuideManager.Compressor.getCraftGuide(in);
	}
	
	/** 获取需要的工作时间 */
	public int getNeedTime() {
		CraftGuideItems in = new CraftGuideItems().add(up.getStack().getItem(), 1)
				                     .add(down.getStack().getItem(), 1);
		CraftGuide cg = CraftGuideManager.Compressor.getCraftGuide(in);
		if (cg == null) return 0;
		return cg.getTime();
	}
	
	/**
	 * 获取已工作时间
	 */
	public int getWorkingTime() {
		return workingTime;
	}
	
	/**
	 * 判断输入是否为空
	 */
	public boolean isEmptyForInput() {
		return up.getHasStack() || down.getHasStack();
	}
	
	/**
	 * 判断是否为空
	 */
	public boolean isEmpty() {
		return up.getHasStack() || down.getHasStack() || out.getHasStack();
	}
	
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
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		item.deserializeNBT(data.getCompoundTag("items"));
		workingTime = data.getInteger("workingTime");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setTag("items", item.serializeNBT());
		data.setInteger("workingTime", workingTime);
		return data;
	}
	
	public static class SlotMI extends SlotItemHandler {
		
		int index;
		EUCompressor nbt;
		
		public SlotMI(IItemHandler itemHandler, int index, int xPosition, int yPosition, EUCompressor nbt) {
			super(itemHandler, index, xPosition, yPosition);
			this.index = index;
			this.nbt = nbt;
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			boolean b = stack != null && CraftGuideManager.Compressor.findMeterial(stack.getItem())
					            && super.isItemValid(stack);
			if (b && !nbt.isEmptyForInput()) {
				SlotItemHandler s = nbt.getSolt(index == 0 ? 1 : 0);
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
