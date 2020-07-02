package xyz.emptydreams.mi.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.electricity.capabilities.ILink;
import xyz.emptydreams.mi.api.electricity.capabilities.LinkCapability;
import xyz.emptydreams.mi.api.utils.wrapper.BooleanWrapper;
import xyz.emptydreams.mi.blocks.common.CommonBlocks;
import xyz.emptydreams.mi.items.common.SpannerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static xyz.emptydreams.mi.blocks.base.MIProperty.FACING;

/**
 * MI中所有耗电机器的父类
 * @author EmptyDreams
 * @version V1.0
 */
public abstract class MachineBlock extends TEBlockBase {
	
	public MachineBlock(Material materialIn) {
		super(materialIn);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 1);
	}

	/** 当临近的方块更新时更新连接状态 */
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos,
	                            @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
		TileEntity now = world.getTileEntity(pos);
		@SuppressWarnings("ConstantConditions")
		ILink link = now.getCapability(LinkCapability.LINK, null);
		blockIn = world.getBlockState(fromPos).getBlock();
		if (link != null) {
			if (blockIn == Blocks.AIR) {
				link.unLink(fromPos);
			} else {
				link.link(fromPos);
			}
		}
	}

	/**
	 * 在方块被右键时激活该方法，一般用于打开GUI.<br>
	 * @return 是否可以打开GUI，若玩家手中拿着扳手则返回false
	 */
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
	                                EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return playerIn.getHeldItem(hand).getItem() != SpannerItem.getInstance();
	}

	/**
	 * 当方块被破坏时掉落额外物品.
	 * 用户覆盖该方法时应该调用该方法，否则会导致{@link #getItemDrops(World, BlockPos)}方法失效
	 * @param worldIn 所在世界
	 * @param pos 当前坐标
	 * @param state 当前State
	 */
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		NonNullList<ItemStack> drops = getItemDrops(worldIn, pos);
		if (drops != null)
			drops.forEach(it -> Block.spawnAsEntity(worldIn, pos, it));
		super.breakBlock(worldIn, pos, state);
	}

	private BooleanWrapper hasFacing = null;

	/**
	 * 在生物放置方块时触发，用于在放置前调整方块方向.<br>
	 * 若方块没有方向则不会进行调整，是否含有方向通过{@link #getDefaultState()}判断
	 * @return 调整后的state
	 */
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
	                                        float hitX, float hitY, float hitZ, int meta,
	                                        EntityLivingBase placer, EnumHand hand) {
		if (hasFacing == null) {
			IBlockState state = getDefaultState();
			hasFacing = new BooleanWrapper(state.getProperties().containsKey(FACING));
		}
		if (hasFacing.get())
			return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
		return getDefaultState();
	}

	/**
	 * 获取方块额外的凋落物，用于在方块破坏的时候掉落方块内存储的物品
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @return 若无需要掉落的物品则返回null
	 */
	@Nullable
	public NonNullList<ItemStack> getItemDrops(World world, BlockPos pos) {
		return null;
	}
	
	@Override
	public int quantityDropped(Random random) { return 1; }
	
	/** 被爆炸破坏时掉落外壳 */
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		spawnAsEntity(world, pos, new ItemStack(CommonBlocks.MACHINE_SHELL));
		super.onBlockExploded(world, pos, explosion);
	}

	/** 获取方块的凋落物，这里用于实现机器被破坏时掉落机器外壳 */
	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return new ItemBlock(CommonBlocks.MACHINE_SHELL);
	}
	
}
