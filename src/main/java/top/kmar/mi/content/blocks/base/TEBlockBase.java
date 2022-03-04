package top.kmar.mi.content.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.api.register.block.BlockItemHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * 带TE的基础方块，定义了经常需要重写的函数
 * @author EmptyDremas
 */
@SuppressWarnings("deprecation")
abstract public class TEBlockBase extends BlockContainer implements BlockItemHelper {

	protected TEBlockBase(Material materialIn) {
		super(materialIn);
	}

	/**
	 * 当方块被破坏时掉落额外物品.
	 * 用户覆盖该方法时应该调用该方法，否则会导致{@link #dropItems(World, BlockPos)}方法失效
	 * @param worldIn 所在世界
	 * @param pos 当前坐标
	 * @param state 当前State
	 */
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		List<ItemStack> drops = dropItems(worldIn, pos);
		if (drops != null)
			drops.forEach(it -> Block.spawnAsEntity(worldIn, pos, it));
		super.breakBlock(worldIn, pos, state);
	}
	
	/**
	 * 获取方块额外的凋落物，用于在方块破坏的时候掉落方块内存储的物品
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @return 若无需要掉落的物品则返回null
	 */
	@Nullable
	public List<ItemStack> dropItems(World world, BlockPos pos) {
		return null;
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
	abstract public int quantityDropped(@Nonnull Random random);

	@Override
	@Nonnull
	abstract protected BlockStateContainer createBlockState();

	@Override
	abstract public int getMetaFromState(@Nonnull IBlockState state);

	@Override
	@Nonnull
	abstract public IBlockState getStateFromMeta(int meta);

}