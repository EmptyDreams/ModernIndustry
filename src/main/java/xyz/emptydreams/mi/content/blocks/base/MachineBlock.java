package xyz.emptydreams.mi.content.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.capabilities.EleCapability;
import xyz.emptydreams.mi.api.electricity.capabilities.IStorage;
import xyz.emptydreams.mi.api.utils.BlockUtil;
import xyz.emptydreams.mi.api.utils.container.BooleanWrapper;
import xyz.emptydreams.mi.content.blocks.common.CommonBlocks;

import javax.annotation.Nonnull;
import java.util.Random;

import static xyz.emptydreams.mi.content.blocks.base.MIProperty.HORIZONTAL;

/**
 * MI中所有耗电机器的父类
 * @author EmptyDreams
 */
public abstract class MachineBlock extends TEBlockBase {
	
	public MachineBlock(Material materialIn) {
		super(materialIn);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 2);
		setHardness(3);
		setResistance(20);
		setCreativeTab(ModernIndustry.TAB_BLOCK);
	}

	/** 当临近的方块更新时更新连接状态 */
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos,
	                            @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
		TileEntity now = world.getTileEntity(pos);
		@SuppressWarnings("ConstantConditions")
		IStorage link = now.getCapability(EleCapability.ENERGY, BlockUtil.whatFacing(fromPos, pos));
		blockIn = world.getBlockState(fromPos).getBlock();
		if (link != null) {
			if (blockIn == Blocks.AIR) {
				link.unLink(fromPos);
			} else {
				link.link(fromPos);
			}
		}
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
			hasFacing = new BooleanWrapper(state.getProperties().containsKey(HORIZONTAL));
		}
		if (hasFacing.get())
			return getDefaultState().withProperty(HORIZONTAL, placer.getHorizontalFacing().getOpposite());
		return getDefaultState();
	}
	
	@Override
	public int quantityDropped(@Nonnull Random random) { return 1; }
	
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