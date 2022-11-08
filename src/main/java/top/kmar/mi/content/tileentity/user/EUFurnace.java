package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.electricity.EleEnergy;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;
import top.kmar.mi.api.utils.interfaces.JvmNoneFunction;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nullable;

/**
 * 电炉
 * @author EmptyDreams
 */
@AutoTileEntity("ele_furnace")
@Mod.EventBusSubscriber
public class EUFurnace extends FrontTileEntity implements ITickable {
    
    public static final int MAX_VOLTAGE = EleEnergy.COMMON + 80;
    public static final int MIN_VOLTAGE = EleEnergy.COMMON - 40;
    
    /** 输入/输出框 */
    @AutoSave
    private final ItemStackHandler items = new ItemStackHandler(2);
    @AutoSave private int workProgress = 0;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        updateWorkingTime();
    }
    
    private void updateWorkingTime() {
        ItemStack outStack = getOutputStack();
        if (outStack.getCount() >= outStack.getMaxStackSize()) return;
        ItemStack inputStack = getInputStack();
        if (inputStack.isEmpty()) {
            workProgress = 0;
            return;
        }
        EleEnergy energy = requestEnergy(Math.min(getEfficiency(), getNeedEnergy() - workProgress));
        boolean check = checkEnergy(
                energy, MIN_VOLTAGE, MAX_VOLTAGE,
                (JvmNoneFunction) () -> updateShow(false),
                (JvmNoneFunction) () -> updateShow(true),
                (JvmNoneFunction) () -> explode(1, true)
        );
        if (!check) return;
        workProgress += energy.getCapacity();
        if (workProgress >= getNeedEnergy()) {
            workProgress = 0;
            items.insertItem(1, MuffleFurnace.getResult(inputStack), false);
            inputStack.shrink(1);
        } else updateShow(true);
        markDirty();
    }
    
    private void updateShow(boolean isWorking) {
        IBlockState old = world.getBlockState(pos);
        if (old.getValue(MIProperty.getWORKING()) == isWorking) return;
        IBlockState state = old.withProperty(MIProperty.getWORKING(), isWorking);
        WorldExpandsKt.setBlockWithMark(world, pos, state);
    }
    
    public ItemStack getInputStack() { return items.getStackInSlot(0); }
    public ItemStack getOutputStack() { return items.getStackInSlot(1); }
    /** 获取制作一个产物需要的能量 */
    public int getNeedEnergy() {
        return 5200;
    }
    /** 获取每 Tick 做大能够获取的能量 */
    public int getEfficiency() {
        return 100;
    }
    
    @Nullable
    @Override
    public EnumFacing getFront() {
        return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
    }
    
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        initGuiHelper(event, BlockGuiList.getEleFurnace());
    }
    
    @SuppressWarnings("ConstantConditions")
    public static void initGuiHelper(GuiLoader.MIGuiRegistryEvent event, ResourceLocation key) {
        event.registryInitTask(key, gui -> {
            EUFurnace furnace = (EUFurnace) gui.getTileEntity();
            gui.initItemStackHandler(furnace.items);
        });
        event.registryLoopTask(key, gui -> {
            EUFurnace furnace = (EUFurnace) gui.getTileEntity();
            ProgressBarCmpt progress = (ProgressBarCmpt) gui.getElementByID("work");
            progress.setMax(furnace.getNeedEnergy());
            progress.setValue(furnace.workProgress);
        });
    }
    
}