package minedreams.mi.blocks.wire;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import minedreams.mi.AutoRegister;
import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.EleUtils;
import minedreams.mi.api.electricity.Electricity;
import minedreams.mi.api.electricity.ElectricityMaker;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.exception.ProtocolErrorException;
import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.api.electricity.info.LinkInfo;
import minedreams.mi.blocks.register.BlockBaseT;
import minedreams.mi.blocks.register.BlockRegister;
import minedreams.mi.blocks.te.TileEntityWire;
import minedreams.mi.tools.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import static minedreams.mi.blocks.wire.Wire.*;

/**
 * 普通电线
 * @author EmptyDremas
 * @version V1.0
 */
public final class WireBlock extends BlockBaseT implements IEleInfo {
	
	private final Item ITEM;
	
	/**
	 * 创建一个WireBlock同时自动创建物品对象，获取物品对象时必须使用{@link #getBlockItem()}获取
	 *
	 * @param name 电线名称，协定规定线缆名称以"wire_"开头但是该构造函数不会自动添加"wire_"
	 */
	public WireBlock(String name) {
		super(Material.CIRCUITS);
		setSoundType(SoundType.SNOW);
		setHardness(0.35F);
		setCreativeTab(ModernIndustry.TAB_WIRE);
		setRegistryName(ModernIndustry.MODID, name);
		setUnlocalizedName(name);
		setDefaultState(getDefaultState().withProperty(SOUTH, false)
				.withProperty(NORTH, false).withProperty(WEST, false).withProperty(EAST, false)
				.withProperty(DOWN, false).withProperty(UP, false));
		ITEM = new Wire(this, name);
		
		AutoRegister.addAutoBlock(this, BlockRegister.WIRE_COPPER);
		AutoRegister.addItem(getBlockItem(), getBlockItem().getRegistryName().getResourcePath());
	}
	
	@Override
	public Item getBlockItem() {
		return ITEM;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntityWire nbt = (TileEntityWire) worldIn.getTileEntity(pos);
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
		
		addCollisionBoxToList(pos, entityBox, list, Wire.B_POINT);
		if (state.getValue(SOUTH)) addCollisionBoxToList(pos, entityBox, list, Wire.B_SOUTH);
		if (state.getValue(NORTH)) addCollisionBoxToList(pos, entityBox, list, Wire.B_NORTH);
		if (state.getValue(WEST)) addCollisionBoxToList(pos, entityBox, list, Wire.B_WEST);
		if (state.getValue(EAST)) addCollisionBoxToList(pos, entityBox, list, Wire.B_EAST);
	}
	
	/**
	 * 当电线被破坏时主动更新附近电器的连接信息
	 */
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		onBlockBreak(worldIn, pos);
	}
	
	/**
	 * 主动更新附近电器的连接信息，在当前方块被破坏时主动更新周边方块的显示状态
	 * @param world 当前世界对象
	 * @param pos 需要更新的方块的坐标
	 */
	private static void onBlockBreak(World world, BlockPos pos) {
		//更新不区分客户端与服务端，以此减少信息同步的次数
		BlockPos[] poss = Tools.getBlockPosList(pos);
		Block block;
		
		for (BlockPos p : poss) {
			TileEntity te = world.getTileEntity(p);
			if (te instanceof Electricity) {
				Wire.updateBlock(world, p, new BlockPos[] { pos }, true);
			}
		}
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(getBlockItem());
	}
	
	/**
	 * 当方块被爆炸破坏时调用该方法，该方法应该由MC自动调用，用户无需调用
	 */
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		onBlockBreak(world, pos);
		super.onBlockExploded(world, pos, explosion);
	}
	
	/** 是否绝缘 */
	public boolean isInsulation() { return false; }
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntity te = worldIn.getTileEntity(fromPos);
		Block block = te == null ? worldIn.getBlockState(fromPos).getBlock() : te.getBlockType();
		if (!(block instanceof IEleInfo)) {
			if (block instanceof BlockAir) {
				TileEntityWire tew = (TileEntityWire) worldIn.getTileEntity(pos);
				tew.deleteLink(fromPos);
			}
		} else {
			TileEntityWire tew = (TileEntityWire) worldIn.getTileEntity(pos);
			if (tew.canLink(te)) {
				tew.linkForce(te);
				tew.deleteLink(fromPos);
			}
		}
	}
	
	/**
	 * 判断调用方块是否可以连接自身(电线)
	 *
	 * @return 若可以连接则返回true
	 */
	@Override
	public boolean canLink(LinkInfo info, boolean nowIsExist, boolean fromIsExist) {
		//判断当前方块是否存在
		if (nowIsExist) {
			//当前方块存在，判断调用方块
			if (info.fromBlock instanceof WireBlock) {
				ElectricityTransfer nbt = (ElectricityTransfer)
						                          ((info.nowUser == null) ?
								                           info.world.getTileEntity(info.nowPos) :info.nowUser);
				ElectricityTransfer nbt2 = (ElectricityTransfer)
						                           ((info.fromUser == null) ?
								                            info.world.getTileEntity(info.fromPos) : info.fromUser);
				return nbt.canLink(nbt2) && nbt2.canLink(nbt);
			} else if (info.fromBlock instanceof IEleInfo) {
				LinkInfo info2 = new LinkInfo(info.world, info.nowPos, info.fromPos, info.nowBlock, info.fromBlock);
				return ((IEleInfo) info.fromBlock).canLink(info2, fromIsExist, nowIsExist);
			}
			return false;
		} else {    // else代表当前主动拉取连接的方块还没有在世界上放置
			//只有实现IEleInfo接口的类才可以主动拉取连接
			if (info.fromBlock instanceof IEleInfo) {
				//因为世界上没有主动方块的信息，所以如果用户不提供相关信息，将无法判断
				if (info.nowUser == null) throw new NullPointerException("判断信息不足，nowUser设置不能为空");
				//获取调用方块的TE
				TileEntity nbt;
				if (info.fromUser != null)
					nbt = info.fromUser;
				else
					nbt = info.world.getTileEntity(info.fromPos);
				
				//如果当前方块的TE来自于传输方块则需要另行处理
				//因为传输方块只能连接两个电线(除分线器外)
				if (info.nowUser instanceof ElectricityTransfer) {
					if (nbt instanceof ElectricityTransfer)
						return ((ElectricityTransfer) info.nowUser).canLink(nbt) &&
								       ((ElectricityTransfer) nbt).canLink(info.nowUser);
					return ((ElectricityTransfer) info.nowUser).canLink(nbt);
				} else if (nbt instanceof ElectricityTransfer) {
					return ((ElectricityTransfer) nbt).canLink(info.nowUser);
				} else if (info.nowUser instanceof ElectricityMaker) {
				
				} else {
					throw ProtocolErrorException.INHERITANCE_STRUCTURE;
				}
			} else {
				if (info.nowBlock instanceof  IEleInfo) {
					LinkInfo info0 = new LinkInfo(info.world, info.nowPos, info.fromPos, info.nowBlock, info.fromBlock);
					info0.cloneFromDown(info);
					return EleUtils.canLink(info0, fromIsExist, false, isInsulation());
				}
			}
		}
		return false;
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
	public ElectricityTransfer createNewTileEntity(World worldIn, int meta) {
		return new TileEntityWire();
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
}
