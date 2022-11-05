package top.kmar.mi.content.tileentity.maker;

import kotlin.jvm.functions.Function1;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.electricity.EleEnergy;
import top.kmar.mi.api.electricity.caps.IElectricityCap;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nullable;

/**
 * 红石能转换器
 * @author EmptyDreams
 */
@AutoTileEntity("red_stone_converter")
@Mod.EventBusSubscriber
public class EMRedStoneConverter extends FrontTileEntity implements ITickable {
    
    public static final int VOLTAGE = EleEnergy.COMMON;
    public static final int maxContainer = 100000;
    
    @AutoSave
    private final ItemStackHandler item = new ItemStackHandler(1);
    
    @AutoSave private int burnTime = 0;
    @AutoSave private int maxTime = 0;
    /** 存储的能量 */
    @AutoSave private int container = 0;
    
    public static int getBurnTime(ItemStack stack) {
        if (stack.getItem() == Items.REDSTONE) return 500;
        else if (stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) return 5000;
        return 0;
    }
    
    @Override
    public void update() {
        if (world.isRemote) {
            WorldExpandsKt.removeTickable(this);
            return;
        }
        if (maxTime == 0) {
            if (!burnRedStone()) return;
        }
        updateData();
    }
    
    /** 更新数据 */
    private void updateData() {
        container += 10;
        if (++burnTime >= maxTime) {
            maxTime = burnTime = 0;
        }
        markDirty();
    }
    
    /** 消耗红石 */
    private boolean burnRedStone() {
        ItemStack input = getInputStack();
        if (input.isEmpty()) {
            markDirty();
            return false;
        }
        input.shrink(1);
        maxTime = getBurnTime(input);
        return true;
    }
    
    @Nullable
    @Override
    public EnumFacing getFront() {
        return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
    }
    
    /** 获取输入的物品 */
    public ItemStack getInputStack() {
        return item.getStackInSlot(0);
    }
    
    private IElectricityCap _cap = null;
    
    @NotNull
    @Override
    protected IElectricityCap buildCap(@NotNull EnumFacing facing) {
        if (_cap == null) {
            _cap = new IElectricityCap() {
                @NotNull
                @Override
                public EleEnergy checkEnergy(int energy, @NotNull Function1<? super EleEnergy, Integer> loss) {
                    if (energy > container) return EleEnergy.getEmpty();
                    int plus = loss.invoke(new EleEnergy(energy, VOLTAGE));
                    int sum = energy + plus;
                    return sum > container ? EleEnergy.getEmpty() : new EleEnergy(sum, VOLTAGE);
                }
    
                @Override
                public void consumeEnergy(int energy) {
                    container -= energy;
                }
            };
        }
        return _cap;
    }
    
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        event.registryInitTask(BlockGuiList.getRedStoneConverter(), gui -> {
            EMRedStoneConverter converter = (EMRedStoneConverter) gui.getTileEntity();
            gui.initItemStackHandler(converter.item);
        });
        event.registryLoopTask(BlockGuiList.getRedStoneConverter(), gui -> {
            EMRedStoneConverter converter = (EMRedStoneConverter) gui.getTileEntity();
            ProgressBarCmpt work = (ProgressBarCmpt) gui.getElementByID("work");
            work.setMax(converter.maxTime);
            work.setValue(converter.burnTime);
            ProgressBarCmpt energy = (ProgressBarCmpt) gui.getElementByID("energy");
            energy.setMax(maxContainer);
            energy.setValue(converter.container);
        });
    }
    
}