package xyz.emptydreams.mi.content.items.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.register.block.BlockItemHelper;

/**
 * 物品方块的拓展类
 * @author EmptyDreams
 */
public class ItemBlockExpand extends ItemBlock {
	
	private final BlockItemHelper blockItemHelper;
	
	public ItemBlockExpand(Block block) {
		super(block);
		blockItemHelper = block instanceof BlockItemHelper ? (BlockItemHelper) block : null;
		setRegistryName(block.getRegistryName());
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (!block.isReplaceable(worldIn, pos)) pos = pos.offset(facing);
		ItemStack itemstack = player.getHeldItem(hand);
		
		if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack)
				&& worldIn.mayPlace(this.block, pos, false, facing, null)) {
			boolean skip = false;
			if (blockItemHelper != null) {
				skip = blockItemHelper.initTileEntity(
						itemstack.copy(), player, worldIn, pos, facing, hitX, hitY, hitZ);
			}
			if (!skip) {
				int i = this.getMetadata(itemstack.getMetadata());
				placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ,
						this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand));
			}
			IBlockState newState = worldIn.getBlockState(pos);
			SoundType soundtype = newState.getBlock().getSoundType(newState, worldIn, pos, player);
			worldIn.playSound(player, pos, soundtype.getPlaceSound(),
								SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F,
								soundtype.getPitch() * 0.8F);
			itemstack.shrink(1);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}
	
}