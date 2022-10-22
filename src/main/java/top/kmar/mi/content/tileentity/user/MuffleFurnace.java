package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.BurnCmpt;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.graphics.components.SlotCmpt;
import top.kmar.mi.api.register.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.BaseTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.data.properties.MIProperty;

import static net.minecraft.tileentity.TileEntityFurnace.getItemBurnTime;

/**
 * 高温火炉
 * @author EmptyDreams
 */
@AutoTileEntity("MuffleFurnace")
@Mod.EventBusSubscriber
public class MuffleFurnace extends BaseTileEntity implements ITickable {
    
    @AutoSave
    private final ItemStackHandler items = new ItemStackHandler(3);
    
    @AutoSave private int maxWorkingTime = 0;
    @AutoSave private int workingTime = 0;
    @AutoSave private int maxBurningTime = 0;
    @AutoSave private int burningTime = 0;
    
    @Override
    public void update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this);
            return;
        }
        ItemStack output = getOutputStack();
        if (updateBurningTime()) {
            if (output.getCount() < output.getMaxStackSize()) updateWorkingTime();
        } else {
            workingTime = maxWorkingTime = 0;
        }
        
        IBlockState oldState = world.getBlockState(pos);
        IBlockState newState = oldState.withProperty(MIProperty.getWORKING(), isWorking());
        WorldUtil.setBlockState(world, pos, newState);
        markDirty();
    }
    
    /** 更新工作时间 */
    protected void updateWorkingTime() {
        ItemStack input = getInputStack();
        if (maxWorkingTime == 0) {
            maxWorkingTime = getCookTime(input);
            input.shrink(1);
        }
        if (++workingTime >= maxWorkingTime) {
            items.insertItem(2, getResult(input), false);
            maxWorkingTime = workingTime = 0;
        }
    }
    
    /** 更新燃烧时间 */
    protected boolean updateBurningTime() {
        if (++burningTime >= maxBurningTime) {
            burningTime = maxBurningTime = 0;
        }
        ItemStack fuel = getFuelStack();
        ItemStack input = getInputStack();
        if (maxBurningTime == 0 && !fuel.isEmpty() && !input.isEmpty()) {
            maxBurningTime = getItemBurnTime(fuel);
            fuel.shrink(1);
            return true;
        }
        return isWorking();
    }
    
    /** 是否正在工作 */
    public boolean isWorking() { return maxBurningTime != 0; }
    /** 获取正面 */
    public EnumFacing getFront() {
        return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
    }
    
    public ItemStack getInputStack() {
        return items.getStackInSlot(0);
    }
    public ItemStack getFuelStack() {
        return items.getStackInSlot(1);
    }
    public ItemStack getOutputStack() {
        return items.getStackInSlot(2);
    }
    
    public static ItemStack getResult(ItemStack in) {
        return FurnaceRecipes.instance().getSmeltingResult(in).copy();
    }
    
    /** 获取工作需要的时间 */
    @SuppressWarnings("unused")
    public int getCookTime(ItemStack stack) {
        return 130;
    }
    
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        event.registryInitTask(BlockGuiList.getHighFurnace(), gui -> {
            MuffleFurnace furnace = (MuffleFurnace) gui.getTileEntity();
            SlotCmpt input = (SlotCmpt) gui.getElementByID("input");
            SlotCmpt fuel = (SlotCmpt) gui.getElementByID("fuel");
            SlotCmpt output = (SlotCmpt) gui.getElementByID("output");
            input.setHandler(furnace.items);
            fuel.setHandler(furnace.items);
            output.setHandler(furnace.items);
        });
        event.registryLoopTask(BlockGuiList.getHighFurnace(), gui -> {
            MuffleFurnace furnace = (MuffleFurnace) gui.getTileEntity();
            ProgressBarCmpt work = (ProgressBarCmpt) gui.getElementByID("work");
            BurnCmpt burn = (BurnCmpt) gui.getElementByID("burn");
            work.setMaxProgress(furnace.maxWorkingTime);
            work.setProgress(furnace.workingTime);
            burn.setMaxProcess(furnace.maxBurningTime);
            burn.setProgress(furnace.burningTime);
        });
    }
    
}