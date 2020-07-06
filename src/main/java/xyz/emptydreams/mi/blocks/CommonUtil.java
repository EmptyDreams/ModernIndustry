package xyz.emptydreams.mi.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.emptydreams.mi.ModernIndustry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;
import static xyz.emptydreams.mi.blocks.base.MIProperty.WORKING;

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
	 * @param id GUI的ID
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @return true
	 */
	public static boolean openGui(EntityPlayer player, int id, World world, BlockPos pos) {
		player.openGui(ModernIndustry.instance, id,
				world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	/** 为指定方块生成一个带方向与工作状态的{@link BlockStateContainer} */
	public static BlockStateContainer createWorkState(Block block) {
		return new BlockStateContainer(block, FACING, WORKING);
	}

	/** 依据IBlockState获取meta */
	public static int getMetaFromState(@Nonnull IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | (state.getValue(WORKING) ? 0b0100 : 0);
	}

	/**
	 * 根据meta获取IBlockState
	 * @param block 方块种类
	 * @param meta meta值
	 */
	@Nonnull
	public static IBlockState getStateFromMeta(Block block, int meta) {
		EnumFacing facing = EnumFacing.getFront(meta & 0b0011);
		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		return block.getDefaultState()
				.withProperty(FACING, facing)
				.withProperty(WORKING, (meta & 0b0100) == 0b0100);
	}

}
