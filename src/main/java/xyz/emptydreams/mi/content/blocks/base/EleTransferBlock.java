package xyz.emptydreams.mi.content.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.register.OreDicRegister;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.content.blocks.tileentity.EleSrcCable;
import xyz.emptydreams.mi.content.items.base.EleTransferItem;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * 普通电线
 * @author EmptyDreams
 */
@SuppressWarnings("deprecation")
abstract public class EleTransferBlock extends TEBlockBase {
	
	public static final AxisAlignedBB B_POINT =
			new AxisAlignedBB(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
	public static final AxisAlignedBB B_EAST =
			new AxisAlignedBB(0.625F, 0.375F, 0.375F, 1, 0.625F, 0.625F);
	public static final AxisAlignedBB B_WEST =
			new AxisAlignedBB(0, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F);
	public static final AxisAlignedBB B_SOUTH =
			new AxisAlignedBB(0.375F, 0.375F, 0.625F, 0.625F, 0.625F, 1);
	public static final AxisAlignedBB B_NORTH =
			new AxisAlignedBB(0.375F, 0.375F, 0, 0.625F, 0.625F, 0.375F);
	
	/** 模型标记 */
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	
	private final Item ITEM;
	
	/**
	 * 创建一个WireBlock同时自动创建物品对象，获取物品对象时必须使用{@link #getBlockItem()}获取
	 *
	 * @param name 电线名称，协定规定线缆名称以"wire_"开头但是该构造函数不会自动添加"wire_"
	 */
	public EleTransferBlock(String name, String... ores) {
		super(Material.CIRCUITS);
		setSoundType(SoundType.SNOW);
		setHardness(0.35F);
		setCreativeTab(ModernIndustry.TAB_WIRE);
		setRegistryName(ModernIndustry.MODID, name);
		setUnlocalizedName(StringUtil.getUnlocalizedName(name));
		setDefaultState(getDefaultState().withProperty(SOUTH, false)
				.withProperty(NORTH, false).withProperty(WEST, false).withProperty(EAST, false)
				.withProperty(DOWN, false).withProperty(UP, false));
		OreDicRegister.registry(this, ores);
		ITEM = new EleTransferItem(this, name);
	}
	
	@Nonnull
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Nonnull
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		EleSrcCable nbt = (EleSrcCable) worldIn.getTileEntity(pos);
		//noinspection ConstantConditions
		state = state.withProperty(UP, nbt.getUp()).withProperty(DOWN, nbt.getDown())
							.withProperty(EAST, nbt.getEast()).withProperty(WEST, nbt.getWest())
							.withProperty(NORTH, nbt.getNorth()).withProperty(SOUTH, nbt.getSouth());
		return state;
	}
	
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> list, Entity entityIn, boolean isActualState) {
		if (!isActualState)
			state = getActualState(state, worldIn, pos);
		
		addCollisionBoxToList(pos, entityBox, list, B_POINT);
		if (state.getValue(SOUTH)) addCollisionBoxToList(pos, entityBox, list, B_SOUTH);
		if (state.getValue(NORTH)) addCollisionBoxToList(pos, entityBox, list, B_NORTH);
		if (state.getValue(WEST)) addCollisionBoxToList(pos, entityBox, list, B_WEST);
		if (state.getValue(EAST)) addCollisionBoxToList(pos, entityBox, list, B_EAST);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
	                                EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
	                                float hitX, float hitY, float hitZ) {
		if (playerIn.getHeldItem(hand).getItem() == Items.SHEARS) {
			playerIn.getHeldItem(hand).damageItem(1, playerIn);
			spawnAsEntity(worldIn, pos, new ItemStack(getItemDropped(state, new Random(), 0),
					quantityDropped(new Random())));
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntity fromEntity = worldIn.getTileEntity(fromPos);
		Block block = fromEntity == null ? worldIn.getBlockState(fromPos).getBlock() : fromEntity.getBlockType();
		EleSrcCable tew = (EleSrcCable) worldIn.getTileEntity(pos);
		if (block == Blocks.AIR) {
			tew.deleteLink(fromPos);
		} else if (fromEntity != null) {
			if (!tew.link(fromPos)) tew.deleteLink(fromPos);
		}
	}
	
	//==============================常规的覆盖MC代码==============================//
	
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
	
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(@Nonnull IBlockState state) {
		return 0;
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, EAST, NORTH, SOUTH, WEST, DOWN, UP);
	}
	
	@Override
	public int quantityDropped(@Nonnull Random random) {
		return 1;
	}
	
}