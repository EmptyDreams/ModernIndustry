package minedreams.mi.blocks.register;

import java.util.Random;

import javax.annotation.Nonnull;

import minedreams.mi.register.block.BlockItemHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;

/**
 * 带TE的基础方块，定义了经常需要重写的函数
 * @author EmptyDremas
 * @version V1.0
 */
public abstract class BlockBaseT extends BlockContainer implements BlockItemHelper {

	protected BlockBaseT(Material materialIn) {
		super(materialIn);
	}
	
	/** 获取凋落物 */
	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return getBlockItem();
	}
	
	/** 渲染方式 */
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	/** 掉落数量 */
	@Override
	public abstract int quantityDropped(Random random);
	
}
