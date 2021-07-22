package xyz.emptydreams.mi.content.items.base;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
	                            EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if (!world.setBlockState(pos, newState, 11)) return false;
		IBlockState state = world.getBlockState(pos);
		TileEntity te = createTileEntity(stack, player, world, pos, side, hitX, hitY, hitZ);
		if (te != null) world.setTileEntity(pos, te);
		if (state.getBlock() == this.block) {
			setTileEntityNBT(world, player, pos, stack);
			this.block.onBlockPlacedBy(world, pos, state, player, stack);
			if (player instanceof EntityPlayerMP)
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
		}
		return true;
	}
	
	protected TileEntity createTileEntity(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
	                                      EnumFacing side, float hitX, float hitY, float hitZ) {
		return blockItemHelper == null ? null
				: blockItemHelper.createTileEntity(stack, player, world, pos, side, hitX, hitY, hitZ);
	}
	
}