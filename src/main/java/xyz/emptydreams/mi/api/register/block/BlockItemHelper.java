package xyz.emptydreams.mi.api.register.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

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
	 * 根据方块被放置时的信息创建一个TileEntity，该TileEntity会在方块被放置前设置到世界中
	 * @param stack 方块物品
	 * @param player 执行操作的玩家
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @param side 被放置的面
	 * @param hitX 鼠标点击位置的坐标
	 * @param hitY 鼠标点击位置的坐标
	 * @param hitZ 鼠标点击位置的坐标
	 * @return 若为null则表示使用默认TileEntity
	 */
	default TileEntity createTileEntity(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
	                                    EnumFacing side, float hitX, float hitY, float hitZ) {
		return null;
	}
	
}