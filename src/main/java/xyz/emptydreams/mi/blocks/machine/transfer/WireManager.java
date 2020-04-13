package xyz.emptydreams.mi.blocks.machine.transfer;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import xyz.emptydreams.mi.api.electricity.src.block.TransferBlock;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcCable;
import xyz.emptydreams.mi.blocks.register.BlockBaseT;
import xyz.emptydreams.mi.register.AutoRegister;
import xyz.emptydreams.mi.register.RegisterManager;
import net.minecraft.world.World;

/**
 * 电线的管理类
 *
 * @author EmptyDremas
 * @version V1.0
 */
@RegisterManager
public final class WireManager {
	
	public static void register() {
		Field[] fields = WireManager.class.getFields();
		BlockBaseT block;
		try {
			for (Field field : fields) {
				//判断是否为静态以及是否继承自BlockBaseT
				if (Modifier.isStatic(field.getModifiers()) &&
						    BlockBaseT.class.isAssignableFrom(field.getType())) {
					block = (BlockBaseT) field.get(null);
					AutoRegister.addAutoBlock(block);
					AutoRegister.addItem(block.getBlockItem());
				}
			}
		} catch (IllegalAccessException ignored) { }
	}
	
	/** 铜质导线 */
	public final static TransferBlock COPPER = new TransferBlock("wire_copper") {
		
		@Nullable
		@Override
		public EleSrcCable createNewTileEntity(World worldIn, int meta) {
			return new EleSrcCable(1000, 1);
		}
		
	};
	
}
