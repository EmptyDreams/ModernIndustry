package xyz.emptydreams.mi.blocks.tileentity.user;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.electricity.clock.OrdinaryCounter;
import xyz.emptydreams.mi.api.gui.component.CommonProgress;
import xyz.emptydreams.mi.api.gui.component.MSlot.SlotHandler;
import xyz.emptydreams.mi.api.gui.component.interfaces.IProgressBar;
import xyz.emptydreams.mi.api.gui.group.SlotGroup;
import xyz.emptydreams.mi.api.utils.ItemUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.blocks.CraftList;
import xyz.emptydreams.mi.blocks.tileentity.FrontTileEntity;
import xyz.emptydreams.mi.data.info.BiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumBiggerVoltage;
import xyz.emptydreams.mi.data.info.EnumVoltage;
import xyz.emptydreams.mi.register.tileentity.AutoTileEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static xyz.emptydreams.mi.api.utils.data.auto.DataType.*;

/**
 * @author EmptyDreams
 */
@AutoTileEntity("electron_synthesizer")
public class EUElectronSynthesizer extends FrontTileEntity implements ITickable {
	
	@Storage(SERIALIZABLE) private final ItemStackHandler HANDLER = new ItemStackHandler(5 * 5 + 4);
	private final SlotGroup SLOTS = new SlotGroup(5, 5, 18, 0);
	private final SlotGroup OUTS = new SlotGroup(2, 2, 18, 0);
	private final CommonProgress PROGRESS = new CommonProgress(
										CommonProgress.Style.ARROW, CommonProgress.Front.RIGHT);
	
	@Storage(INT) private int workingTime = -10;
	@Storage(INT) private int maxTime = 0;
	@Storage(COLLECTION) private final List<ItemElement> OUTPUT = new ArrayList<>(4);
	@Storage(COLLECTION) private List<ItemStack> MERGE;
	/** 是否需要重新计算，当输入栏变动时需要将此项设置为true */
	private volatile boolean refresh = false;
	
	public EUElectronSynthesizer() {
		setReceiveRange(1, 20, EnumVoltage.C, EnumVoltage.D);
		OrdinaryCounter counter = new OrdinaryCounter(100);
		counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
		setCounter(counter);
		setReceive(true);
		setMaxEnergy(20);
		SLOTS.writeFrom(0, this::createHandler);
		OUTS.writeFrom(HANDLER, this, 25, it -> false);
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
				PROGRESS.setMax(maxTime);
			}
		}
		if (!OUTPUT.isEmpty() && shrinkEnergy(1) && ++workingTime > maxTime) {
			export();
			clear();
		}
		PROGRESS.setNow(workingTime);
	}
	
	/** 清除状态 */
	private void clear() {
		OUTPUT.clear();
		workingTime = -2;
		MERGE = null;
		refresh = true;
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
		ItemList input = new ItemList(5, 5);
		for (int y = 0; y < 5; ++y) {
			for (int x = 0; x < 5; ++x) {
				input.set(x, y, ItemElement.instance(getInput().getSlot(x, y).getStack()));
			}
		}
		return input;
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
	
	/** 创建一个{@link SlotItemHandler} */
	private SlotItemHandler createHandler(int index) {
		return new SlotHandler(HANDLER, this, index) {
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				refresh = true;
			}
		};
	}
	
	/** 获取工作需要的时间 */
	public int getMaxTime(ItemList input, ItemSet output) {
		return 100;
	}
	
	/** 获取原料入口 */
	public SlotGroup getInput() {
		return SLOTS;
	}
	
	/** 获取产品 */
	public SlotGroup getOutput() {
		return OUTS;
	}
	
	/** 获取进度条 */
	public IProgressBar getProgress() {
		return PROGRESS;
	}
	
	@Nullable
	@Override
	public EnumFacing getFront() {
		return EnumFacing.UP;
	}
	
	@Override
	public boolean isReAllowable(EnumFacing facing) {
		return true;
	}
	
	@Override
	public boolean isExAllowable(EnumFacing facing) {
		return false;
	}
	
}
