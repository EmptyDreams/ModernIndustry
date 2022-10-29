package top.kmar.mi.content.tileentity.user;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.craft.CraftGuide;
import top.kmar.mi.api.craft.elements.CraftOutput;
import top.kmar.mi.api.craft.elements.ElementList;
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.electricity.info.BiggerVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.ExpandFunctionKt;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.data.CraftList;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 粉碎机的TileEntity
 * @author EmptyDreams
 */
@AutoTileEntity("pulverizer")
@Mod.EventBusSubscriber
public class EUPulverizer extends FrontTileEntity implements ITickable {
    
    public static final int VOLTAGE = EleEnergy.COMMON;
    
    /** 物品栏 */
    @AutoSave
    private final ItemStackHandler items = new ItemStackHandler(2);
    /** 工作时间 */
    private int workingTime = 0;
    
    public EUPulverizer() {
        OrdinaryCounter counter = new OrdinaryCounter(100);
        counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
        setCounter(counter);
        setMaxEnergy(20);
    }
    
    @Override
    public void update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this);
            return;
        }
        if (checkInputAndOutput() || !shrinkEnergy(getNeedEnergy())) {
            updateShow();
            return;
        }
        updateData();
        updateShow();
    }
    
    private void updateShow() {
        WorldUtil.setBlockState(world, pos,
                world.getBlockState(pos)
                        .withProperty(MIProperty.getWORKING(), workingTime > 0));
    }
    
    /** 更新内部数据 */
    private void updateData() {
        if (++workingTime >= getNeedTime()) {
            ItemStack input = getInputStack();
            CraftOutput output = CraftGuide.findOutput(
                    CraftList.pulverizer, ElementList.build(input));
            //noinspection ConstantConditions
            items.insertItem(1, output.getFirstStack(), false);
            input.shrink(1);
            workingTime = 0;
        }
        markDirty();
    }
    
    /**
     * 检查输入/输出是否正确
     * @return 是否中断运行
     */
    private boolean checkInputAndOutput() {
        //如果输入框为空则不可能正常工作
        ItemStack input = getInputStack();
        if (input.isEmpty()) {
            if (workingTime != 0) {
                workingTime = 0;
                markDirty();
            }
            return true;
        }
        ItemStack output = getOutputStack();
        if (!output.isEmpty()) {
            CraftOutput craft = CraftGuide.findOutput(
                    CraftList.pulverizer, ElementList.build(input));
            if (craft == null || output.getCount() >= output.getMaxStackSize()||
                    !ExpandFunctionKt.match(craft.getFirstStack(), output)) {
                //如果输出框不为空但物品数量达到上限则不能正常运行
                //               或产品与输出框不相符则不能正常运行
                if (workingTime != 0) {
                    workingTime = 0;
                    markDirty();
                }
                return true;
            }
        }
        return false;
    }
    
    /** 获取输入槽 */
    @Nonnull
    public ItemStack getInputStack() { return items.getStackInSlot(0); }
    /** 获取输出槽 */
    @Nonnull
    public ItemStack getOutputStack() { return items.getStackInSlot(1); }
    /** 获取1tick需要的能量 */
    public int getNeedEnergy() { return 10; }
    /** 获取需要工作的时长 */
    public int getNeedTime() { return 120; }
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
    
    @Override
    public boolean isReceiveAllowable(EnumFacing facing) { return true; }
    @Override
    public boolean isExtractAllowable(EnumFacing facing) { return false; }
    
    @Override
    public int getExVoltage() {
        return EleEnergy.COMMON;
    }
    
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        ResourceLocation key = BlockGuiList.getPulverizer();
        event.registryInitTask(key, gui -> {
            EUPulverizer furnace = (EUPulverizer) gui.getTileEntity();
            gui.initItemStackHandler(furnace.items);
        });
        event.registryLoopTask(key, gui -> {
            EUPulverizer furnace = (EUPulverizer) gui.getTileEntity();
            ProgressBarCmpt progress = (ProgressBarCmpt) gui.getElementByID("work");
            progress.setMax(furnace.getNeedTime());
            progress.setValue(furnace.workingTime);
        });
    }
    
}