package top.kmar.mi.content.tileentity.user;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.craft.CraftGuide;
import top.kmar.mi.api.craft.elements.CraftOutput;
import top.kmar.mi.api.craft.elements.ElementList;
import top.kmar.mi.api.electricity.EleEnergy;
import top.kmar.mi.api.graphics.GuiLoader;
import top.kmar.mi.api.graphics.components.ProgressBarCmpt;
import top.kmar.mi.api.graphics.components.SlotCmpt;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;
import top.kmar.mi.api.tools.FrontTileEntity;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;
import top.kmar.mi.api.utils.interfaces.JvmNoneFunction;
import top.kmar.mi.content.blocks.BlockGuiList;
import top.kmar.mi.content.blocks.machine.user.CompressorBlock;
import top.kmar.mi.data.CraftList;
import top.kmar.mi.data.properties.MIProperty;

/**
 * 压缩机的TileEntity，存储方块内物品、工作时间等内容
 * @author EmptyDreams
 */
@AutoTileEntity(CompressorBlock.NAME)
@Mod.EventBusSubscriber
public class EUCompressor extends FrontTileEntity implements ITickable {
    
    public static final int MAX_VOLTAGE = EleEnergy.COMMON + 100;
    public static final int MIN_VOLTAGE = EleEnergy.COMMON - 50;
    
    /**
     * 三个物品框<br>
     * 	0-上端，1-下端，2-输出
     */
    @AutoSave
    private final ItemStackHandler items = new ItemStackHandler(3);
    /** 已工作时间 */
    @AutoSave private int workingTime = 0;
    /** 正在进行的任务 */
    @AutoSave
    private CraftOutput output = null;
    
    @Override
    public void update() {
        if (world.isRemote) {
            removeTickable();
            return;
        }
        if (output == null) {
            if (workingTime != 0) {
                workingTime = 0;
                markDirty();
            }
            removeTickable();
            updateShow(false);
            return;
        }
        ItemStack outStack = output.getFirstStack();
        int maxEnergy = getNeedEnergy();
        EleEnergy energy = requestEnergy(Math.min(50, maxEnergy - workingTime));
        boolean check = checkEnergy(
                energy, MIN_VOLTAGE, MAX_VOLTAGE,
                (JvmNoneFunction) () -> updateShow(false),
                (JvmNoneFunction) () -> updateShow(true),
                (JvmNoneFunction) () -> explode(1, true)
        );
        if (!check) return;
        ItemStack up = getInputUpStack();
        ItemStack down = getInputDownStack();
        if (workingTime == 0) {
            up.shrink(1);
            down.shrink(1);
        }
        workingTime += energy.getCapacity();
        if (workingTime >= getNeedTime()) {
            workingTime = 0;
            items.insertItem(2, outStack, false);
            output = findOutput(up, down);
        }
        updateShow(true);
        markDirty();
    }
    
    /**
     * 更新方块显示
     * @param isWorking 是否正在工作
     */
    private void updateShow(boolean isWorking) {
        IBlockState old = world.getBlockState(pos);
        IBlockState state = old.withProperty(MIProperty.getEMPTY(), isEmpty())
                .withProperty(MIProperty.getWORKING(), isWorking);
        WorldExpandsKt.setBlockWithMark(world, pos, state);
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
        return output == null ? 0 : output.getInt("time", 5000);
    }
    /** 获取已工作时间 */
    public int getWorkingTime() {
        return workingTime;
    }
    /** 获取每Tick需要的能量 */
    public int getNeedEnergy() {
        return output == null ? 0 : output.getInt("energy", 10);
    }
    /** 判断是否为空 */
    public boolean isEmpty() {
        for (int i = 0; i != 3; ++i) {
            if (!items.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }
    
    @Override
    public EnumFacing getFront() {
        return world.getBlockState(pos).getValue(MIProperty.getHORIZONTAL());
    }
    
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void initGui(GuiLoader.MIGuiRegistryEvent event) {
        event.registryInitTask(BlockGuiList.getCompressor(), gui -> {
            EUCompressor compressor = (EUCompressor) gui.getTileEntity();
            gui.initItemStackHandler(compressor.items);
            SlotCmpt up = (SlotCmpt) gui.getElementByID("up");
            SlotCmpt down = (SlotCmpt) gui.getElementByID("down");
            up.getSlotAttributes().setInputChecker(
                    it -> compressor.inputChecker(it, down.getSlot().getStack())
            );
            down.getSlotAttributes().setInputChecker(
                    it -> compressor.inputChecker(it, up.getSlot().getStack())
            );
        });
        event.registryLoopTask(BlockGuiList.getCompressor(), gui -> {
            EUCompressor compressor = (EUCompressor) gui.getTileEntity();
            ProgressBarCmpt progress = (ProgressBarCmpt) gui.getElementByID("work");
            progress.setMax(compressor.getNeedTime());
            progress.setValue(compressor.getWorkingTime());
        });
    }
    
    public CraftOutput findOutput(ItemStack arg0, ItemStack arg1) {
        ItemStack existing = getOutputStack();
        if (existing.getCount() >= existing.getMaxStackSize()) return null;
        ElementList list = ElementList.build(arg0.copy(), arg1.copy());
        CraftOutput output = CraftGuide.findDisorderlyOutput(CraftList.compressor, list);
        if (output == null ||
                !items.insertItem(2, output.getFirstStack(), true).isEmpty()) return null;
        else return output;
    }
    
    private boolean inputChecker(ItemStack input, ItemStack other) {
        if (other.isEmpty()) return true;
        output = findOutput(input, other);
        if (output == null) return false;
        addTickable();
        return true;
    }
    
}