package top.kmar.mi.content.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.kmar.mi.api.utils.expands.PlayerExpandsKt;
import top.kmar.mi.data.properties.MIProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

import static top.kmar.mi.data.properties.MIProperty.getWorking;

/**
 * 封装了对于State的常用操作
 * @author EmptyDreams
 */
public final class CommonUtil {
    
    /**
     * 创建一个输出框
     * @param handler 句柄
     * @param index 下标
     * @param x X轴坐标
     * @param y Y轴坐标
     */
    @Nonnull
    public static SlotItemHandler createOutputSlot(ItemStackHandler handler, int index, int x, int y) {
        return new SlotItemHandler(handler, index, x, y) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }
        };
    }
    
    /**
     * 创建一个输入框
     * @param handler 句柄
     * @param index 下标
     * @param x X轴坐标
     * @param y Y轴坐标
     * @param inputCheck 输入检查，若为null表示永远合法
     */
    @Nonnull
    public static SlotItemHandler createInputSlot(ItemStackHandler handler, int index, int x, int y,
                                                  @Nullable Predicate<ItemStack> inputCheck) {
        return new SlotItemHandler(handler, index, x, y) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return super.isItemValid(stack) && (inputCheck == null || inputCheck.test(stack));
            }
        };
    }
    
    /**
     * 打开一个GUI
     * @param player 要打开GUI的玩家
     * @param key GUI的key
     * @param pos 方块坐标
     * @return true
     */
    public static boolean openGui(EntityPlayer player, ResourceLocation key, BlockPos pos) {
        if (player.world.isRemote) return true;
        PlayerExpandsKt.openGui(player, key, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    /** 为指定方块生成一个带方向与工作状态的{@link BlockStateContainer} */
    public static BlockStateContainer createBlockState(Block block) {
        return new BlockStateContainer(block, MIProperty.getHorizontal(), getWorking());
    }
    
    /** 依据IBlockState获取meta */
    public static int getMetaFromState(@Nonnull IBlockState state) {
        return state.getValue(getFacing(state)).ordinal() |
                (state.getValue(getWorking()) ? 0b1000 : 0b0000);
    }
    
    /**
     * 根据meta获取IBlockState
     * @param block 方块种类
     * @param meta meta值
     */
    @Nonnull
    public static IBlockState getStateFromMeta(Block block, int meta) {
        EnumFacing facing = EnumFacing.values()[meta & 0b0111];
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }
        return block.getDefaultState()
                .withProperty(getFacing(block.getDefaultState()), facing)
                .withProperty(getWorking(), (meta & 0b1000) == 0b1000);
    }
    
    @Nonnull
    private static PropertyDirection getFacing(IBlockState state) {
        for (IProperty<?> key : state.getPropertyKeys()) {
            if (key instanceof PropertyDirection) return (PropertyDirection) key;
        }
        throw new IllegalArgumentException("没有对应的值：" + state);
    }
    
}