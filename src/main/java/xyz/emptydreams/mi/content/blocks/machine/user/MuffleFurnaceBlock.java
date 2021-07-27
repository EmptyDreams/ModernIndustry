package xyz.emptydreams.mi.content.blocks.machine.user;

import com.google.common.collect.Lists;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.register.block.AutoBlockRegister;
import xyz.emptydreams.mi.content.blocks.CommonUtil;
import xyz.emptydreams.mi.content.blocks.base.TEBlockBase;
import xyz.emptydreams.mi.content.tileentity.user.MuffleFurnace;
import xyz.emptydreams.mi.content.gui.MuffleFurnaceFrame;
import xyz.emptydreams.mi.content.items.common.SpannerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static xyz.emptydreams.mi.api.utils.properties.MIProperty.HORIZONTAL;
import static xyz.emptydreams.mi.api.utils.properties.MIProperty.WORKING;

/**
 * 高温熔炉
 * @author EmptyDreams
 */
@AutoBlockRegister(registryName = "muffle_furnace")
public class MuffleFurnaceBlock extends TEBlockBase {
	
	private final Item ITEM = new ItemBlock(this);
	
	public MuffleFurnaceBlock() {
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState()
				                .withProperty(HORIZONTAL, EnumFacing.NORTH)
				                .withProperty(WORKING, false));
		setCreativeTab(ModernIndustry.TAB_BLOCK);
		setSoundType(SoundType.STONE);
		setHardness(3.5F);
		setHarvestLevel("pickaxe", 1);
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		if (playerIn.getHeldItem(hand).getItem() == SpannerItem.getInstance()) return false;
		return CommonUtil.openGui(playerIn, MuffleFurnaceFrame.NAME, worldIn, pos);
	}

	@Nullable
	@Override
	public List<ItemStack> dropItems(World world, BlockPos pos) {
		MuffleFurnace furnace = (MuffleFurnace) world.getTileEntity(pos);
		//noinspection ConstantConditions
		return Lists.newArrayList(furnace.getDown().getStack(),
									furnace.getUp().getStack(),
									furnace.getOut().getStack());
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return CommonUtil.getStateFromMeta(this, meta);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
	                                        float hitX, float hitY, float hitZ, int meta,
	                                        EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(HORIZONTAL, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return CommonUtil.getMetaFromState(state);
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return CommonUtil.createBlockState(this);
	}
	
	@Override
	public int quantityDropped(@Nonnull Random random) {
		return 1;
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new MuffleFurnace();
	}
	
	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
}