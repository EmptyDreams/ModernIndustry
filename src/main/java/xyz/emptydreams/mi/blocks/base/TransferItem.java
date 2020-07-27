package xyz.emptydreams.mi.blocks.base;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.utils.BlockUtil;
import xyz.emptydreams.mi.blocks.te.EleSrcCable;

/**
 * 普通电线物品
 * @author EmptyDremas
 * @version V1.2.1
 */
public final class TransferItem extends ItemBlock {
	
	public TransferItem(TransferBlock block, String name) {
		super(block);
		setRegistryName(ModernIndustry.MODID, name);
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
        } else {
        	blockPos = pos.offset(facing);
        	if (!block.isReplaceable(worldIn, blockPos)) {
        		return EnumActionResult.FAIL;
        	}
        }
        
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty() && player.canPlayerEdit(blockPos, facing, itemstack)) {
        	//Object[] os = whatState(worldIn, this.block, blockPos, new BlockPos[] { pos });
            IBlockState iblockstate1 = placeBlockAt(itemstack, player, worldIn, blockPos, this.block.getDefaultState());
            if (iblockstate1 != null) {
            	//更新TileEntity
	            EleSrcCable nbt = (EleSrcCable) ((TransferBlock) this.block).createNewTileEntity(worldIn, 0);
	            nbt.setWorld(worldIn);
	            nbt.setPos(blockPos);
	            nbt.setBlockType(this.block);
	            if (pos != blockPos) nbt.link(pos);
	            BlockUtil.forEachAroundTE(worldIn, blockPos, (te, fa) -> {
	            	if (pos != te.getPos()) nbt.link(te.getPos());
	            });
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
    
}
