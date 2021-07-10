package xyz.emptydreams.mi.content.items.debug;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.content.blocks.CommonUtil;
import xyz.emptydreams.mi.content.gui.ClassInfoViewerFrame;

/**
 * @author EmptyDreams
 */
//@AutoItemRegister("class_info_viewer")
public class ClassInfoViewer extends Item {
	
	public ClassInfoViewer() {
		setCreativeTab(ModernIndustry.TAB_DEBUG);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing,
	                                  float hitX, float hitY, float hitZ) {
		TileEntity entity = worldIn.getTileEntity(pos);
		if (entity == null) return EnumActionResult.PASS;
		boolean result = CommonUtil.openGui(player, ClassInfoViewerFrame.NAME, worldIn, pos);
		if (result) return EnumActionResult.SUCCESS;
		return EnumActionResult.FAIL;
	}
	
	private static StaticFrameClient createFrame(World world, EntityPlayer player, BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		if (entity == null) return null;
		return new StaticFrameClient(new ClassInfoViewerFrame(entity, player), "mi.debug.class_info_viewer");
	}
	
}