package top.kmar.mi.content.blocks.machine;

import net.minecraft.world.World;
import top.kmar.mi.content.tileentity.EleSrcCable;
import top.kmar.mi.api.regedits.others.AutoManager;
import top.kmar.mi.content.blocks.base.EleTransferBlock;

/**
 * 电线的管理类
 * @author EmptyDremas
 */
@AutoManager(block = true)
public final class WireManager {
	
	/** 铜质导线 */
	public final static EleTransferBlock COPPER = new EleTransferBlock(
						"wire_copper", "itemCopperCable", "mi_cable") {
		@Override
		public EleSrcCable createNewTileEntity(World worldIn, int meta) {
			//防止折叠
			return new EleSrcCable(500, 1);
		}
	};
	
}