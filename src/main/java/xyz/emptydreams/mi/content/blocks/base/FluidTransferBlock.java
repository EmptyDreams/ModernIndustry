package xyz.emptydreams.mi.content.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.fluid.FTTileEntity;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.FluidTransferCapability;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.IFluidTransfer;
import xyz.emptydreams.mi.api.register.OreDicRegister;
import xyz.emptydreams.mi.api.utils.BlockUtil;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.fluid.FTStateEnum;
import xyz.emptydreams.mi.content.items.base.ItemBlockExpand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static xyz.emptydreams.mi.content.blocks.base.EleTransferBlock.*;
import static xyz.emptydreams.mi.content.blocks.properties.MIProperty.ALL_FACING;
import static xyz.emptydreams.mi.content.blocks.properties.MIProperty.FLUID;

/**
 * 流体管道的方块
 * @author EmptyDreams
 */
@SuppressWarnings("deprecation")
abstract public class FluidTransferBlock extends TEBlockBase {
	
	private final Item ITEM;
	
	public FluidTransferBlock(String name, String... ores) {
		super(Material.IRON);
		setSoundType(SoundType.SNOW);
		setHardness(0.5F);
		setCreativeTab(ModernIndustry.TAB_WIRE);
		setRegistryName(ModernIndustry.MODID, name);
		setUnlocalizedName(StringUtil.getUnlocalizedName(name));
		setDefaultState(blockState.getBaseState().withProperty(SOUTH, false)
				.withProperty(NORTH, false).withProperty(WEST, false).withProperty(EAST, false)
				.withProperty(DOWN, false).withProperty(UP, false)
				.withProperty(ALL_FACING, EnumFacing.NORTH)
				.withProperty(FLUID, FTStateEnum.STRAIGHT));
		OreDicRegister.registry(this, ores);
		ITEM = new ItemBlockExpand(this);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntity fromEntity = worldIn.getTileEntity(fromPos);
		Block block = fromEntity == null ? worldIn.getBlockState(fromPos).getBlock() : fromEntity.getBlockType();
		FTTileEntity nowEntity = (FTTileEntity) worldIn.getTileEntity(pos);
		@SuppressWarnings("ConstantConditions") FTTileEntity.FluidCapability cap = nowEntity.getFTCapability();
		EnumFacing facing = BlockUtil.whatFacing(pos, fromPos);
		if (block == Blocks.AIR || fromEntity == null) {
			cap.unlink(facing);
		} else {
			cap.link(facing);
		}
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		@SuppressWarnings("ConstantConditions") IFluidTransfer transfer =
				worldIn.getTileEntity(pos).getCapability(FluidTransferCapability.TRANSFER, null);
		if (transfer == null) return getDefaultState();
		state = state.withProperty(FLUID, FTStateEnum.STRAIGHT)
					.withProperty(ALL_FACING, transfer.getFacing())
					.withProperty(UP, transfer.isLinkedUp()).withProperty(DOWN, transfer.isLinkedDown())
					.withProperty(EAST, transfer.isLinkedEast()).withProperty(WEST, transfer.isLinkedWest())
					.withProperty(SOUTH, transfer.isLinkedSouth()).withProperty(NORTH, transfer.isLinkedNorth());
		return state;
	}
	
	@Override
	public TileEntity createTileEntity(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
	                                   EnumFacing side, float hitX, float hitY, float hitZ) {
		FTTileEntity te = (FTTileEntity) world.getTileEntity(pos);
		world.getBlockState(pos).getValue(FLUID).createTileEntity(world, pos, te, side);
		//noinspection ConstantConditions
		te.markDirty();
		return null;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return state.getValue(FLUID).getBoundingBox(state.getValue(ALL_FACING));
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
	                                  AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
	                                  @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState) state = getActualState(state, worldIn, pos);
		state.getValue(FLUID).addCollisionBoxToList(state,
				it -> addCollisionBoxToList(pos, entityBox, collidingBoxes, it));
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Nonnull
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ITEM);
	}
	
	@Override
	public int quantityDropped(@Nonnull Random random) {
		return 1;
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FLUID, ALL_FACING, UP, DOWN, SOUTH, NORTH, WEST, EAST);
	}
	
	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		FTStateEnum value = state.getValue(FLUID);
		switch (value) {
			case STRAIGHT: return 0;
			case ANGLE: return 1;
			case SHUNT: return 2;
			default: throw new IllegalArgumentException("输入了未知的状态：" + value);
		}
	}
	
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		switch (meta) {
			case 1: return getDefaultState().withProperty(FLUID, FTStateEnum.ANGLE);
			case 2: return getDefaultState().withProperty(FLUID, FTStateEnum.SHUNT);
			default: return getDefaultState();
		}
	}
	
	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
}