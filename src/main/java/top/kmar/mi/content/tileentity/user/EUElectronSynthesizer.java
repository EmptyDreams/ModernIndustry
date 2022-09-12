package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.craftguide.sol.ItemList;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.electricity.info.BiggerVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage;
import top.kmar.mi.api.register.others.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.ItemUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.CraftList;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 电子工作台
 * @author EmptyDreams
 */
@AutoTileEntity("electron_synthesizer")
public class EUElectronSynthesizer extends FrontTileEntity implements ITickable {
	
	public static final int VOLTAGE = EleEnergy.COMMON;
	
	@AutoSave
    private final ItemStackHandler HANDLER = new ItemStackHandler(5 * 5 + 4);
	//TODO
	//private final SlotGroup SLOTS = new SlotGroup(5, 5, 18, 0);
	//private final SlotGroup OUTS = new SlotGroup(2, 2, 18, 0);
	/** 工作时间 */
	@AutoSave private int workingTime = -10;
	@AutoSave private int maxTime = 0;
	@AutoSave private final List<ItemElement> OUTPUT = new ArrayList<>(4);
	@AutoSave private List<ItemStack> MERGE;
	/** 是否需要重新计算，当输入栏变动时需要将此项设置为true */
	private volatile boolean refresh = false;
	
	public EUElectronSynthesizer() {
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setMaxEnergy(20);
		//SLOTS.writeFromBuilder(0, 25, this::createHandler);
		//OUTS.writeFrom(HANDLER, this, 25, it -> false);
	}
	
	@Override
	public void update() {
		if (world.isRemote) {
			WorldUtil.removeTickable(this);
			return;
		}
		if (refresh) {
			refresh = false;
			ItemList input = calculateProduction();
			ItemSet output = CraftList.SYNTHESIZER.apply(input.offset());
			if (check(output)) {
				workingTime = Math.max(workingTime, 0);
				maxTime = getMaxTime(input, output);
				IBlockState newState = world.getBlockState(
						getPos()).withProperty(MIProperty.getWORKING(), true);
				WorldUtil.setBlockState(world, pos, newState);
			} else {
				clear();
			}
		}
		if (!OUTPUT.isEmpty() && shrinkEnergy(1) && ++workingTime > maxTime) {
			export();
			clear();
		}
	}
	
	/** 清除状态 */
	private void clear() {
		OUTPUT.clear();
		workingTime = -2;
		MERGE = null;
		refresh = true;
		IBlockState newState = world.getBlockState(
				getPos()).withProperty(MIProperty.getWORKING(), false);
		WorldUtil.setBlockState(world, pos, newState);
	}
	
	/** 输出产物 */
	private void export() {
		for (int i = 0; i < 25; ++i) {
			HANDLER.getStackInSlot(i).shrink(1);
		}
		for (int i = 0; i < 4; ++i) {
			if (i < MERGE.size()) {
				HANDLER.setStackInSlot(i + 25, MERGE.get(i));
			} else {
				HANDLER.setStackInSlot(i + 25, ItemStack.EMPTY);
			}
		}
	}
	
	/** 检查输出是否可用以及是否与已有物品列表冲突 */
	private boolean check(ItemSet output) {
		if (output == null) {
			workingTime = -10;
			return false;
		}
		OUTPUT.clear();
		output.forEach(OUTPUT::add);
		merge();
		if (MERGE == null) {
			workingTime = -10;
			return false;
		}
		return true;
	}
	
	/** 根据原料列表计算产物 */
	private ItemList calculateProduction() {
		/*ItemList input = new ItemList(5, 5);
		for (int y = 0; y < 5; ++y) {
			for (int x = 0; x < 5; ++x) {
				input.set(x, y, ItemElement.instance(getInput().getSlot(x, y).getStack()));
			}
		}
		return input;*/
		return null;
	}
	
	/** 合并产物列表 */
	private void merge() {
		ItemStack[] stack = new ItemStack[8];
		for (int i = 0; i < OUTPUT.size(); i++) {
			stack[i] = OUTPUT.get(i).getStack();
			stack[i + 4] = HANDLER.getStackInSlot(i + 25);
		}
		List<ItemStack> merge = ItemUtil.merge(stack);
		if (merge.size() < 4) MERGE = merge;
	}
	
	/** 获取工作需要的时间 */
	@SuppressWarnings("unused")
	public int getMaxTime(ItemList input, ItemSet output) {
		return 100;
	}
	
	@Nullable
	@Override
	public EnumFacing getFront() {
		return EnumFacing.UP;
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
	
}