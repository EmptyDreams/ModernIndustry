package xyz.emptydreams.mi.blocks.base;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import xyz.emptydreams.mi.register.block.BlockItemHelper;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 带TE的基础方块，定义了经常需要重写的函数
 * @author EmptyDremas
 * @version V1.0
 */
abstract public class TEBlockBase extends BlockContainer implements BlockItemHelper {

	protected TEBlockBase(Material materialIn) {
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
	abstract public int quantityDropped(Random random);

	@Override
	abstract protected BlockStateContainer createBlockState();

	@Override
	abstract public int getMetaFromState(IBlockState state);

	@Override
	abstract public IBlockState getStateFromMeta(int meta);

}
