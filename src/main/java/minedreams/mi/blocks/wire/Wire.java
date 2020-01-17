package minedreams.mi.blocks.wire;

import java.util.ArrayList;
import java.util.List;

import minedreams.mi.tools.MISysInfo;
import minedreams.mi.api.electricity.EleUtils;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.api.electricity.info.LinkInfo;
import minedreams.mi.blocks.te.TileEntityWire;
import minedreams.mi.tools.Tools;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import static minedreams.mi.ModernIndustry.MODID;

/**
 * 普通电线物品
 * @author EmptyDremas
 * @version V1.1
 */
public final class Wire extends Item {
	
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
	
	public static PropertyBool getProperty(EnumFacing facing) {
		switch (facing) {
			case DOWN : return DOWN;
			case UP : return UP;
			case EAST : return EAST;
			case WEST : return WEST;
			case SOUTH : return SOUTH;
			default : return NORTH;
		}
	}
	
	/** 方块 */
	private final Block block;
	
	public Wire(Block block, String name) {
		this.block = block;
		setRegistryName(MODID, name);
		setUnlocalizedName(name);
		setCreativeTab(block.getCreativeTabToDisplayOn());
	}
	
	/**
	 * @param player 玩家对象
	 * @param worldIn 所在世界
	 * @param pos 右键方块所在坐标
	 * @param hand 左右手
	 * @param facing 右键方块的哪个方向
	 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        BlockPos blockPos;
        if (block.isReplaceable(worldIn, pos)) {
            blockPos = pos;
            pos = null;
        } else {
        	blockPos = pos.offset(facing);
        	if (!block.isReplaceable(worldIn, blockPos)) {
        		return EnumActionResult.FAIL;
        	}
        }
        
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty() && player.canPlayerEdit(blockPos, facing, itemstack)) {
        	Object[] os = whatState(worldIn, this.block, blockPos, new BlockPos[] { pos });
            IBlockState iblockstate1 = placeBlockAt(itemstack, player, worldIn, blockPos, (IBlockState) os[0]);
            if (iblockstate1 != null) {
            	//更新TileEntity
	            TileEntityWire nbt = (TileEntityWire) os[2];
	            nbt.update(iblockstate1);
	            worldIn.setTileEntity(blockPos, nbt);
            	
                SoundType soundtype = this.block.getSoundType(iblockstate1, worldIn, blockPos, player);
                worldIn.playSound(player, blockPos, soundtype.getPlaceSound(),
		                SoundCategory.BLOCKS,
		                (soundtype.getVolume() + 1.0F) / 2.0F,
		                soundtype.getPitch() * 0.8F);
                if (!player.isCreative()) itemstack.shrink(1);
            }
            
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
	}
	
	private IBlockState placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, IBlockState newState) {
        if (!world.setBlockState(pos, newState, 11)) return null;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block) {
            ItemBlock.setTileEntityNBT(world, player, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);
            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return state;
    }
	
	/**
	 * 更新电线显示，该方法其中主要功能通过调用
	 * {@link #whatState(World, IBlockState, BlockPos, BlockPos[], boolean, BlockPos[], TileEntityWire, boolean)}
	 * 实现，同时兼并电线TileEntity的更新功能，方法内部自动调用world.setBlockState与world.markBlockRangeForRenderUpdate。
	 * 注意：<b>该方法更新电线显示不会触发TileEntity的数据同步，如需同步数据还需自行实现</b>
	 * @param world 当前世界
	 * @param pos 需要更新的电线的坐标
	 * @param donnotLink 不要连接的方块坐标
	 * @param isDelete 是否删除该方块周围方块对该方块的连接
	 */
	public static void updateBlock(World world, BlockPos pos, BlockPos[] donnotLink, boolean isDelete) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityWire)) return;
		TileEntityWire nbt = (TileEntityWire) te;
		if (nbt == null) {
			nbt = new TileEntityWire();
			nbt.setWorld(world);
			nbt.setPos(pos);
		}
		Block block = nbt.getBlockType();
		if (!(block instanceof IEleInfo)) return;
		Object[] os = whatState(world, block.getDefaultState(), pos, donnotLink,
									true, nbt.getLinks(), nbt, isDelete);
		IBlockState state = (IBlockState) os[0];
		world.setBlockState(pos, state);
		nbt.validate();
		world.setTileEntity(pos, nbt);
		world.markBlockRangeForRenderUpdate(pos, pos);
		nbt.update(state);
	}
	
	/**
	 * 该方法时{@link #whatState(World, IBlockState, BlockPos, BlockPos[],
	 *      boolean, BlockPos[], TileEntityWire, boolean)}方法的转接，
	 * (whatState(world, block.getDefaultState(), pos, null, false, firstPos))，其中某些参数使用默认参数
	 * @param world 方块所在世界
	 * @param block 电线种类
	 * @param pos 当前方块所在坐标
	 * @param firstPos 首要连接的方块
	 */
	private static Object[] whatState(World world, Block block, BlockPos pos, BlockPos[] firstPos) {
		return whatState(world, block.getDefaultState(), pos, null,
				false, firstPos, null, false);
	}
	
	/**
	 * 判断应该使用哪一个IBlockState
	 * @param world 方块所在世界
	 * @param state 当前IBlockState，方法运行时不覆盖原有内容
	 * @param pos 当前方块所在坐标
	 * @param donnotLink 不要连接的方块
	 * @param isExisting 周围方块是否已经存在，该选项错误可能会导致渲染错误
	 * @param firstPos 首要连接的方块
	 * @param nbt 当前电线的TE，不存在可为null
	 * @param isDelete 是否删除该方块周围方块对该方块的连接
	 *
	 * @return 0 -> IBlockState，1 -> 需要更新的方块(List《BlockPos》)， 2 -> 创建的电线的TE
	 */
	private static Object[] whatState(World world, IBlockState state, BlockPos pos,
	                                  BlockPos[] donnotLink, boolean isExisting,
	                                  BlockPos[] firstPos, TileEntityWire nbt, boolean isDelete) {
		//准备设置的TE
		boolean isExistNow;
		if (nbt == null) {
			nbt = new TileEntityWire();
			nbt.setPos(pos);
			nbt.setWorld(world);
			nbt.setBlockType(state.getBlock());
			isExistNow = false;
		} else {
			nbt.deleteAllLink(false);
			isExistNow = true;
		}
		//首先清除首要连接中的null项来简化后期运算
		firstPos = Tools.removeNull(firstPos);
		//清除首要连接中与禁止连接中的重复项
		firstPos = ArrayUtils.removeElements(firstPos, donnotLink);
		/* 获取方块附近方块的信息 */
		//附近方块的BlockPos，移除不连接的方块
		BlockPos[] allPos = ArrayUtils.removeElements(Tools.getBlockPosList(pos), donnotLink);
		//附近方块的IBlockState
		IBlockState[] states = new IBlockState[allPos.length];
		//附近方块的Block
		Block[] blocks = new Block[allPos.length];
		//附近方块的TE
		TileEntity[] tes = new TileEntity[allPos.length];
		//附近方块能否连接
		//boolean[] bool = new boolean[allPos.length];
		//存储需要更新显示的方块
		List<BlockPos> needUpdate = new ArrayList<>();
		
		/* 初始化附近方块信息 */
		for (int i = 0; i < allPos.length; ++i) {
			states[i] = world.getBlockState(allPos[i]);
			blocks[i] = states[i].getBlock();
			tes[i] = world.getTileEntity(allPos[i]);
		}
		
		ElectricityTransfer transfer;
		/* 当首要连接不为空时优先遍历首要连接 */
		if (firstPos != null) {
			for (BlockPos bp : firstPos) {
				//获取对应下标
				int index = Tools.findValue(allPos, bp);
				//如果不存在则报错
				if (index == -1) {
					MISysInfo.err("警告：意外的错误“firstPos中的元素不在数列中”");
					continue;
				}
				//判断是否可以连接
				LinkInfo info = new LinkInfo(world, allPos[index], pos, blocks[index], state.getBlock());
				info.fromState = states[index];
				info.nowUser = nbt;
				if (!EleUtils.canLink(info, isExistNow, isExisting, nbt.isInsulation())) continue;
				nbt.linkForce(tes[index]);
				//更新state
				state = state.withProperty(getProperty(Tools.whatFacing(pos, bp)), true);
				if (tes[index] instanceof ElectricityTransfer) {
					transfer = (ElectricityTransfer) tes[index];
					transfer.linkForce(nbt);
					transfer.updateLink();
					transfer.markDirty();
				} else {
					//添加更新标识
					needUpdate.add(bp);
				}
			}
		}
		
		BlockPos bp;
		for (int i = 0; i < allPos.length; ++i) {
			bp = allPos[i];
			if (Tools.hasValue(firstPos, bp)) continue;
			LinkInfo info = new LinkInfo(world, allPos[i], pos, blocks[i], state.getBlock());
			info.fromState = states[i];
			info.nowUser = nbt;
			if (!EleUtils.canLink(info, isExistNow, isExisting, nbt.isInsulation())) continue;
			//连接线缆
			nbt.linkForce(tes[i]);
			if (tes[i] instanceof ElectricityTransfer) {
				transfer = (ElectricityTransfer) tes[i];
				transfer.linkForce(nbt);
				transfer.updateLink();
				transfer.markDirty();
			} else {
				needUpdate.add(bp);
			}
			state = state.withProperty(getProperty(Tools.whatFacing(pos, bp)), true);
		}
		
		return new Object[] { state, needUpdate, nbt };
	}
	
}
