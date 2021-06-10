package xyz.emptydreams.mi.content.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import xyz.emptydreams.mi.api.register.block.BlockItemHelper;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 基础方块，定义了经常需要重写的函数
 * @author EmptyDremas
 */
public abstract class BlockBase extends Block implements BlockItemHelper {
	
	public BlockBase(Material materialIn) {
		super(materialIn);
	}
	
	@Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return getBlockItem();
    }
	
	@Override
	public abstract int quantityDropped(Random random);
	
}