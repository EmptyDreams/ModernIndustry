package minedreams.mi.api.electricity.block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.*;
import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.api.electricity.info.LinkInfo;
import minedreams.mi.blocks.register.BlockBaseT;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 普通电线
 * @author EmptyDremas
 * @version V1.0
 */
abstract public class TransferBlock extends BlockBaseT implements IEleInfo {
	
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
	public TransferBlock(String name) {
		super(Material.CIRCUITS);
		setSoundType(SoundType.SNOW);
		setHardness(0.35F);
		setCreativeTab(ModernIndustry.TAB_WIRE);
		setRegistryName(ModernIndustry.MODID, name);
		setUnlocalizedName(name);
		setDefaultState(getDefaultState().withProperty(SOUTH, false)
				.withProperty(NORTH, false).withProperty(WEST, false).withProperty(EAST, false)
				.withProperty(DOWN, false).withProperty(UP, false));
		ITEM = new TransferItem(this, name);
	}
	
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		ElectricityTransfer nbt = (ElectricityTransfer) worldIn.getTileEntity(pos);
		state = state.withProperty(UP, nbt.getUp()).withProperty(DOWN, nbt.getDown())
							.withProperty(EAST, nbt.getEast()).withProperty(WEST, nbt.getWest())
							.withProperty(NORTH, nbt.getNorth()).withProperty(SOUTH, nbt.getSouth());
		return state;
	}
	
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
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(getBlockItem());
	}
	
	/** 是否绝缘 */
	public boolean isInsulation() { return false; }
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntity fromEntity = worldIn.getTileEntity(fromPos);
		Block block = fromEntity == null ? worldIn.getBlockState(fromPos).getBlock() : fromEntity.getBlockType();
		if (block == Blocks.AIR) {
			ElectricityTransfer tew = (ElectricityTransfer) worldIn.getTileEntity(pos);
			tew.deleteLink(fromPos);
		} else if (fromEntity != null) {
			ElectricityTransfer tew = (ElectricityTransfer) worldIn.getTileEntity(pos);
			tew.link(fromEntity);
		}
	}
	
	/**
	 * 判断调用方块是否可以连接自身(电线)
	 *
	 * @return 若可以连接则返回true
	 */
	@Override
	public boolean canLink(LinkInfo info, boolean nowIsExist, boolean fromIsExist) {
		TileEntity from = info.fromUser;
		ElectricityTransfer now = null;
		
		TileEntity temp = info.nowUser == null ? info.world.getTileEntity(info.nowPos) : info.nowUser;
		if (info.nowUser == null && !nowIsExist)
			throw new IllegalArgumentException("判断信息不足！当前方块不存在时传入参数nowUser不能为null");
		if (!(temp instanceof ElectricityTransfer))
			throw new IllegalArgumentException("判断信息错误！当前方块的TE类型应该为"
					                                   + ElectricityTransfer.class.getSimpleName());
		now = (ElectricityTransfer) temp;
		
		if (fromIsExist) {
			if (from == null) from = info.world.getTileEntity(info.fromPos);
			if (!now.canLink(from)) return false;
			if (from instanceof ElectricityTransfer) {
				return ((ElectricityTransfer) from).canLink(now);
			}
			if (from instanceof ElectricityMaker || from instanceof ElectricityUser) {
				return true;
			}
			if (now.getLinkAmount() != 2 && !now.isInsulation()) {
				return EleUtils.canLinkMinecraft(info.nowBlock);
			}
			return false;
		} else {
			return now.canLink(from);
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
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, EAST, NORTH, SOUTH, WEST, DOWN, UP);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
	/** <b>注意：电线放置时需要使用该方法，且放置时meta一定为0 */
	@Nullable
	@Override
	abstract public ElectricityTransfer createNewTileEntity(World worldIn, int meta);
	
}
