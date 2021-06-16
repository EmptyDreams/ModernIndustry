package xyz.emptydreams.mi.content.items.debug;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.register.item.AutoItemRegister;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.content.blocks.base.EleTransferBlock;
import xyz.emptydreams.mi.content.blocks.tileentity.EleSrcCable;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * 电线信息显示器，信息将会打印在客户端的后台中
 */
@AutoItemRegister("wireinfo_viewer")
public class WireInfoViewer extends Item {
	
	public WireInfoViewer() {
		setCreativeTab(ModernIndustry.TAB_DEBUG);
	}
	
	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof EleTransferBlock) {
			EleSrcCable et = (EleSrcCable) worldIn.getTileEntity(pos);
			StringBuilder sb = new StringBuilder();
			assert et != null;
			if (worldIn.isRemote) {
				sb.append("客户端线缆信息{\n\t内部连接数据：\t")
						.append(Arrays.toString(new boolean[]{
								et.getUp(), et.getDown(), et.getEast(),
								et.getWest(), et.getSouth(), et.getNorth()
						}))
						.append(";\n\t\t\t       UP, DOWN, EAST, WEST, SOUTH\n}");
			} else {
				sb.append("服务端线缆信息{\n\t坐标：")
						.append(pos)
						.append(";\n\t上一根电线：")
						.append(et.getPrev())
						.append(";\n\t下一根电线：")
						.append(et.getNext())
						.append(";\n\t内部连接数据：\t")
						.append(Arrays.toString(new boolean[]{
								et.getUp(), et.getDown(), et.getEast(),
								et.getWest(), et.getSouth(), et.getNorth()
						}))
						.append(";\n\t\t\t       UP, DOWN, EAST, WEST, SOUTH\n}");
			}
			MISysInfo.print(sb);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}
}