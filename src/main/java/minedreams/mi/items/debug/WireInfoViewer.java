package minedreams.mi.items.debug;

import java.util.Arrays;

import minedreams.mi.tools.MISysInfo;
import minedreams.mi.ModernIndustry;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.blocks.wire.WireBlock;
import minedreams.mi.items.register.AutoItemRegister;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 电线信息显示器，信息将会打印在客户端的后台中
 */
@AutoItemRegister("wireinfo_viewer")
public class WireInfoViewer extends Item {
	
	public WireInfoViewer() {
		setCreativeTab(ModernIndustry.TAB_DEBUG);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return EnumActionResult.FAIL;
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof WireBlock) {
			ElectricityTransfer et = (ElectricityTransfer) worldIn.getTileEntity(pos);
			StringBuilder sb = new StringBuilder();
			sb.append("线缆信息{\n\t坐标：")
			  .append(pos)
			  .append(";\n\t上一根电线：")
			  .append(et.getPrev())
			  .append(";\n\t下一根电线：")
			  .append(et.getNext())
			  .append(";\n\t内部连接数据：")
			  .append(Arrays.toString(new boolean[] {
			  		        et.getUp(), et.getDown(), et.getEast(),
					        et.getWest(), et.getSouth(), et.getNorth()
			            }))
			  .append(";\n\t       UP, DOWN, EAST, WEST, SOUTH\n}");
			MISysInfo.print(sb);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}
}
