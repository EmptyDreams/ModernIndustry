package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.electricity.clock.OrdinaryCounter;
import top.kmar.mi.api.electricity.info.BiggerVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.EnumBiggerVoltage;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.BackpackCmpt;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.graphics.components.SlotCmpt;
import top.kmar.mi.api.graphics.components.SlotOutputCmpt;
import top.kmar.mi.api.graphics.components.interfaces.ComplexCmptExp;
import top.kmar.mi.api.register.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.content.blocks.CraftList;
import top.kmar.mi.content.blocks.machine.user.CompressorBlock;
import top.kmar.mi.data.properties.MIProperty;

/**
 * 压缩机的TileEntity，存储方块内物品、工作时间等内容
 * @author EmptyDreams
 */
@AutoTileEntity(CompressorBlock.NAME)
@Mod.EventBusSubscriber
public class EUCompressor extends FrontTileEntity implements ITickable {
    
    public static final int VOLTAGE = EleEnergy.COMMON;
    
    /**
     * 三个物品框<br>
     * 	0-上端，1-下端，2-输出
     */
    @AutoSave
    private final ItemStackHandler items = new ItemStackHandler(3);
    /** 已工作时间 */
    @AutoSave private int workingTime = 0;
    /** 每次工作消耗的电能 */
    private int needEnergy = 10;
    
    public EUCompressor() {
        OrdinaryCounter counter = new OrdinaryCounter(100);
        counter.setBigger(new BiggerVoltage(2F, EnumBiggerVoltage.BOOM));
        setCounter(counter);
        setMaxEnergy(20);
    }
    
    /** 设置世界时更新计数器的设置 */
    @Override
    public void setWorld(World worldIn) {
        super.setWorld(worldIn);
        ((OrdinaryCounter) getCounter()).setWorld(worldIn);
    }
    
    /** 设置坐标时更新计数器的设置 */
    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        ((OrdinaryCounter) getCounter()).setPos(posIn);
    }
    
    @Override
    public void update() {
        if (world.isRemote) {
            WorldUtil.removeTickable(this);
            return;
        }
        
        //检查输入框是否合法 如果不合法则清零工作时间并结束函数
        ItemStack outStack = checkInput();
        if (outStack == null || getNowEnergy() < getNeedEnergy()) {
            whenFailed(outStack == null);
            return;
        }
        
        //若配方存在则继续计算
        boolean isWorking = updateData(outStack);
        updateShow(isWorking);
        markDirty();
    }
    
    /**
     * 更新内部数据
     * @param outStack 产品
     * @return 是否正在工作
     */
    private boolean updateData(ItemStack outStack) {
        boolean isWorking = false;  //保存是否正在工作
        ItemStack nowOut = getOutputStack();
        //检查输入物品数目是否足够
        if (items.insertItem(2, outStack, true).isEmpty()) {
            isWorking = true;
            ++workingTime;
            if (workingTime >= getNeedTime()) {
                workingTime = 0;
                items.extractItem(0, 1, false);
                items.extractItem(1, 1, false);
                nowOut.grow(outStack.getCount());
            }
            shrinkEnergy(getNeedEnergy());
        } else {
            workingTime = 0;
        }
        return isWorking;
    }
    
    /**
     * 更新方块显示
     * @param isWorking 是否正在工作
     */
    private void updateShow(boolean isWorking) {
        IBlockState old = world.getBlockState(pos);
        IBlockState state = old.withProperty(MIProperty.getEMPTY(), isEmpty())
                .withProperty(MIProperty.getWORKING(), isWorking);
        WorldUtil.setBlockState(world, pos, state);
    }
    
    /**
     * 检查输入内容并获取输出
     * @return 返回产品，若输入不合法则返回null
     */
    private ItemStack checkInput() {
        if (!isEmptyForInput()) {
            ItemSet set = new ItemSet();
            set.add(ItemElement.instance(getInputUpStack()));
            set.add(ItemElement.instance(getInputDownStack()));
            ItemElement craft = CraftList.COMPRESSOR.apply(set);
            if (craft == null) return null;
            return craft.getStack();
        }
        return null;
    }
    
    /**
     * 当工作失败时执行替换方块的操作
     * @param isOutputFailed 是否是因为合成表不存在导致的失败
     */
    private void whenFailed(boolean isOutputFailed) {
        //如果不存在，更新方块显示
        if (isOutputFailed) workingTime = 0;
        updateShow(false);
        markDirty();
    }
    
    public ItemStack getInputUpStack() {
        return items.getStackInSlot(0);
    }
    public ItemStack getInputDownStack() {
        return items.getStackInSlot(1);
    }
    public ItemStack getOutputStack() {
        return items.getStackInSlot(2);
    }
    
    /** 获取需要的工作时间 */
    public int getNeedTime() {
        return 100;
    }
    /** 获取已工作时间 */
    public int getWorkingTime() {
        return workingTime;
    }
    /** 获取每Tick需要的能量 */
    public int getNeedEnergy() {
        return needEnergy;
    }
    /** 设置每Tick需要的能量 */
    public void setNeedEnergy(int needEnergy) {
        this.needEnergy = needEnergy;
    }
    /** 判断输入是否为空 */
    public boolean isEmptyForInput() {
        return getInputUpStack().isEmpty() && getInputDownStack().isEmpty();
    }
    /** 判断是否为空 */
    public boolean isEmpty() {
        for (int i = 0; i != 3; ++i) {
            if (!items.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
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
    
    @Override
    public EnumFacing getFront() {
        return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
    }
    
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        event.registryInitTask(BlockGuiList.INSTANCE.getCompressor(), gui -> {
            EUCompressor compressor = (EUCompressor) gui.getTileEntity();
            // 玩家背包
            BackpackCmpt backpack = (BackpackCmpt) gui.queryCmpt(new ComplexCmptExp("backpack"));
            backpack.setPlayer(gui.getPlayer());
            // 输入
            SlotCmpt upInput = (SlotCmpt) gui.getElementByID("up");
            upInput.setInventory(compressor.items);
            // 燃料
            SlotCmpt downInput = (SlotCmpt) gui.getElementByID("down");
            downInput.setInventory(compressor.items);
            // 输出
            SlotOutputCmpt output = (SlotOutputCmpt) gui.getElementByID("output");
            output.setInventory(compressor.items);
        });
        event.registryLoopTask(BlockGuiList.INSTANCE.getCompressor(), gui -> {
            EUCompressor compressor = (EUCompressor) gui.getTileEntity();
            ProgressBarCmpt progress = (ProgressBarCmpt) gui.getElementByID("work");
            progress.setMaxProgress(compressor.getNeedTime());
            progress.setProgress(compressor.getWorkingTime());
        });
    }
    
}