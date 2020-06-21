package xyz.emptydreams.mi.blocks.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import xyz.emptydreams.mi.blocks.common.CommonBlocks;
import xyz.emptydreams.mi.items.common.SpannerItem;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public abstract class MachineBlock extends TEBlockBase {
	
	public MachineBlock(Material materialIn) {
		super(materialIn);
		setSoundType(SoundType.STONE);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos,
	                            @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
		TileEntity now = world.getTileEntity(pos);
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
	
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
	                                EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return playerIn.getHeldItem(hand).getItem() != SpannerItem.getInstance();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		NonNullList<ItemStack> drops = getItemDrops(worldIn, pos);
		if (drops != null)
			drops.forEach(it -> Block.spawnAsEntity(worldIn, pos, it));
		super.breakBlock(worldIn, pos, state);
	}
	
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
	
	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return new ItemBlock(CommonBlocks.MACHINE_SHELL);
	}
	
}
