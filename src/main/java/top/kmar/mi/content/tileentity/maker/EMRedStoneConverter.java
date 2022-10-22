package top.kmar.mi.content.tileentity.maker;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.electricity.clock.NonCounter;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.graphics.components.SlotCmpt;
import top.kmar.mi.api.register.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
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
    
    @AutoSave
    private final ItemStackHandler item = new ItemStackHandler(1);
    
    @AutoSave private int burnTime = 0;
    @AutoSave private int maxTime = 0;
    
    public static int getBurnTime(ItemStack stack) {
        if (stack.getItem() == Items.REDSTONE) return 500;
        else if (stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) return 5000;
        return 0;
    }
    
    public EMRedStoneConverter() {
        setMaxEnergy(5000);
        setCounter(NonCounter.getInstance());
    }
    
    @Override
    public void update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this);
            return;
        }
        if (maxTime == 0) {
            if (!burnRedStone()) return;
        }
        updateData();
    }
    
    /** 更新数据 */
    private void updateData() {
        growEnergy(1);
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
    
    @Override
    public boolean onReceive(EleEnergy energy) {
        if (energy.getVoltage() > VOLTAGE) getCounter().plus();
        return true;
    }
    
    /** 获取输入的物品 */
    public ItemStack getInputStack() {
        return item.getStackInSlot(0);
    }
    
    @Override
    public boolean isReceiveAllowable(EnumFacing facing) {
        return false;
    }
    
    @Override
    public boolean isExtractAllowable(EnumFacing facing) {
        return true;
    }
    
    @Override
    public int getExVoltage() {
        return EleEnergy.COMMON;
    }
    
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        event.registryInitTask(BlockGuiList.getRedStoneConverter(), gui -> {
            EMRedStoneConverter converter = (EMRedStoneConverter) gui.getTileEntity();
            SlotCmpt input = (SlotCmpt) gui.getElementByID("input");
            input.setHandler(converter.item);
        });
        event.registryLoopTask(BlockGuiList.getRedStoneConverter(), gui -> {
            EMRedStoneConverter converter = (EMRedStoneConverter) gui.getTileEntity();
            ProgressBarCmpt work = (ProgressBarCmpt) gui.getElementByID("work");
            work.setMaxProgress(converter.maxTime);
            work.setProgress(converter.burnTime);
            ProgressBarCmpt energy = (ProgressBarCmpt) gui.getElementByID("energy");
            energy.setMaxProgress(converter.getMaxEnergy());
            energy.setProgress(converter.getNowEnergy());
        });
    }
    
}