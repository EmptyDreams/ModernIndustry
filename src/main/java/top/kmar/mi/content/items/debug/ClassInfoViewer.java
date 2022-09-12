package top.kmar.mi.content.items.debug;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.register.item.AutoItemRegister;

/**
 * @author EmptyDreams
 */
@AutoItemRegister("class_info_viewer")
public class ClassInfoViewer extends Item {
	
	public ClassInfoViewer() {
		setCreativeTab(ModernIndustry.TAB_DEBUG);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing,
	                                  float hitX, float hitY, float hitZ) {
		return EnumActionResult.FAIL;
		//TODO
		/*TileEntity entity = worldIn.getTileEntity(pos);
		if (entity == null) return EnumActionResult.PASS;
		boolean result = CommonUtil.openGui(player, ClassInfoViewerFrame.NAME, worldIn, pos);
		if (result) return EnumActionResult.SUCCESS;
		return EnumActionResult.FAIL;*/
	}
	
}