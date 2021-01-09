package xyz.emptydreams.mi.api.register.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

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
	
}
