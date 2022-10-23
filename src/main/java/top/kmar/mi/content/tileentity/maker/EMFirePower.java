package top.kmar.mi.content.tileentity.maker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.electricity.clock.NonCounter;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.register.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.ItemUtil;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.content.blocks.CraftList;
import top.kmar.mi.data.properties.MIProperty;

/**
 * 火力发电机的TE
 * @author EmptyDreams
 */
@AutoTileEntity("fire_power")
@Mod.EventBusSubscriber
public class EMFirePower extends FrontTileEntity implements ITickable {
    
    public static final int VOLTAGE = EleEnergy.COMMON;
    
    /** 输入/输出框 */
    @AutoSave
    private final ItemStackHandler items = new ItemStackHandler(2);
    /** 已经燃烧的时长 */
    @AutoSave private int burningTime = 0;
    /** 最大燃烧时长 */
    @AutoSave private int maxTime = 0;
    /** 正在燃烧的物品 */
    @AutoSave private ItemElement burnItem;
    
    public EMFirePower() {
        setMaxEnergy(10000);
        setCounter(NonCounter.getInstance());
		/*
		stack -> TileEntityFurnace.getItemBurnTime(stack) > 0 &&
													!stack.hasCapability(NonBurnCapability.NON_BURN, null)
		 */
    }
    
    @Override
    public void update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this);
            return;
        }
        if (maxTime <= 0) burnItem();
        else updateBurningTime();
    }
    
    /** 燃烧输入框中的物品 */
    private void burnItem() {
        ItemStack stack = getInputStack();
        IBlockState old = world.getBlockState(pos);
        IBlockState state;
        if (!stack.isEmpty() && getNowEnergy() < getMaxEnergy() / 10 * 5) {
            maxTime = TileEntityFurnace.getItemBurnTime(stack);
            burnItem = ItemElement.instance(stack.getItem(), 1);
            stack.shrink(1);
            state = old.withProperty(MIProperty.getWORKING(), true);
        } else {
            burnItem = null;
            state = old.withProperty(MIProperty.getWORKING(), false);
        }
        WorldUtil.setBlockState(world, pos, state);
        markDirty();
    }
    
    /** 更新输出 */
    private void updateProduction() {
        maxTime = burningTime = 0;
        ItemElement element = CraftList.FIRE_POWER.apply(new ItemSet(burnItem));
        if (element == null) return;
        ItemUtil.putItemTo(getOutputStack(), element.getStack(), false);
    }
    
    /** 更新燃烧时间 */
    private void updateBurningTime() {
        if ((burningTime += 5) >= maxTime) updateProduction();
        setNowEnergy(getNowEnergy() + 30);
        markDirty();
    }
    
    public ItemStack getInputStack() {
        return items.getStackInSlot(0);
    }
    
    public ItemStack getOutputStack() {
        return items.getStackInSlot(1);
    }
    
    @Override
    public boolean onReceive(EleEnergy energy) {
        if (energy.getVoltage() > VOLTAGE) getCounter().plus();
        return true;
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
    
    @Override
    public EnumFacing getFront() {
        return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
    }
    
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        event.registryInitTask(BlockGuiList.getFirePower(), gui -> {
            EMFirePower power = (EMFirePower) gui.getTileEntity();
            gui.initItemStackHandler(power.items);
        });
        event.registryLoopTask(BlockGuiList.getFirePower(), gui -> {
            EMFirePower power = (EMFirePower) gui.getTileEntity();
            ProgressBarCmpt burn = (ProgressBarCmpt) gui.getElementByID("burn");
            burn.setMax(power.maxTime);
            burn.setValue(power.burningTime);
            ProgressBarCmpt energy = (ProgressBarCmpt) gui.getElementByID("energy");
            energy.setMax(power.getMaxEnergy());
            energy.setValue(power.getNowEnergy());
        });
    }
    
}