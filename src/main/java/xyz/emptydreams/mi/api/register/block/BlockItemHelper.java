package xyz.emptydreams.mi.api.register.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 方块物品辅助注册工具，其中实现方法来返回Item对象来减少{@link Item#getItemFromBlock(Block)}的调用
 * @author EmptyDreams
 */
public interface BlockItemHelper {
	
	/**
	 * 获取该类的物品对象，需要自动注册的类建议实现该方法
	 */
	@Nonnull
	Item getBlockItem();
	
	/**
	 * <p>根据方块被放置时的信息创建一个TileEntity
	 * <p><b>该方法被调用时方块并没有被放置到世界中，复写该方法时用户需手动将IBlockState和TE放置到世界中
	 * <p>放置方块时使用{@link #putBlock(World, BlockPos, IBlockState, TileEntity, EntityPlayer, ItemStack)}方法</b>
	 * @param stack 方块物品
	 * @param player 执行操作的玩家
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @param side 被放置的面
	 * @param hitX 鼠标点击位置的坐标
	 * @param hitY 鼠标点击位置的坐标
	 * @param hitZ 鼠标点击位置的坐标
	 * @return 是否复写了该方法
	 */
	@Nullable
	default boolean initTileEntity(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
	                                  EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	default void putBlock(World world, BlockPos pos, IBlockState state,
	                      TileEntity te, EntityPlayer player, ItemStack stack) {
		world.setBlockState(pos, state, 11);
		world.setTileEntity(pos, te);
		state.getBlock().onBlockPlacedBy(world, pos, state, player, stack);
		if (player instanceof EntityPlayerMP)
			CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
	}
	
}