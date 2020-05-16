package xyz.emptydreams.mi.blocks.base;

import java.util.Random;

import javax.annotation.Nonnull;

import xyz.emptydreams.mi.register.block.BlockItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

/**
 * 基础方块，定义了经常需要重写的函数
 * @author EmptyDremas
 * @version V1.0
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
