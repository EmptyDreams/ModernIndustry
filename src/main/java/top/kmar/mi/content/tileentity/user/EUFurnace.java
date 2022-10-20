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
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.electricity.info.BiggerVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.BackpackCmpt;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.graphics.components.SlotCmpt;
import top.kmar.mi.api.graphics.components.SlotOutputCmpt;
import top.kmar.mi.api.register.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
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
    
    public static final int VOLTAGE = EleEnergy.COMMON;
    
    /** 输入/输出框 */
    @AutoSave
    private final ItemStackHandler items = new ItemStackHandler(2);
    @AutoSave private int workingTime = 0;
    
    public EUFurnace() {
        OrdinaryCounter counter = new OrdinaryCounter(100);
        counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
        setCounter(counter);
        setMaxEnergy(10);
    }
    
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
            workingTime = 0;
            return;
        }
        if (!shrinkEnergy(getNeedEnergy())) return;
        
        if (++workingTime >= getNeedTime()) {
            workingTime = 0;
            if (outStack.isEmpty()) {
                items.insertItem(1, MuffleFurnace.getResult(inputStack), false);
            } else {
                outStack.grow(1);
            }
            inputStack.shrink(1);
        }
        
        IBlockState old = world.getBlockState(pos);
        IBlockState state = old.withProperty(MIProperty.getWORKING(), workingTime > 0);
        WorldUtil.setBlockState(world, pos, state);
    }
    
    public ItemStack getInputStack() { return items.getStackInSlot(0); }
    public ItemStack getOutputStack() { return items.getStackInSlot(1); }
    public int getNeedEnergy() { return 5; }
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
    
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        initGuiHelper(event, BlockGuiList.getEleFurnace());
    }
    
    @SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
    public static void initGuiHelper(GuiLoader.MIGuiRegistryEvent event, ResourceLocation key) {
        event.registryInitTask(key, gui -> {
            EUFurnace furnace = (EUFurnace) gui.getTileEntity();
            BackpackCmpt backpack = (BackpackCmpt) gui.getElementByID("player");
            backpack.setPlayer(gui.getPlayer());
            SlotCmpt input = (SlotCmpt) gui.getElementByID("input");
            input.setInventory(furnace.items);
            SlotOutputCmpt output = (SlotOutputCmpt) gui.getElementByID("output");
            output.setInventory(furnace.items);
        });
        event.registryLoopTask(key, gui -> {
            EUFurnace furnace = (EUFurnace) gui.getTileEntity();
            ProgressBarCmpt progress = (ProgressBarCmpt) gui.getElementByID("work");
            progress.setMaxProgress(furnace.getNeedTime());
            progress.setProgress(furnace.workingTime);
        });
    }
    
}