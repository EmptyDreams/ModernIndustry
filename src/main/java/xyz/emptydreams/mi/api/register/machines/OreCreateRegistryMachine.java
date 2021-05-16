package xyz.emptydreams.mi.api.register.machines;

import net.minecraft.block.Block;
import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.block.OreCreate;
import xyz.emptydreams.mi.api.register.block.WorldCreater;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;

/**
 * 矿石生成器注册机
 * @author EmptyDreams
 */
public class OreCreateRegistryMachine extends AutoRegisterMachine<OreCreate, Object> {
	
	@Nonnull
	@Override
	public Class<OreCreate> getTargetClass() {
		return OreCreate.class;
	}
	
	@Override
	public void registry(Class<?> clazz, OreCreate annotation, Object data) {
		String name = annotation.name();
		Block b = null;
		for (Block block : BlockRegistryMachine.Blocks.blocks) {
			if (block.getRegistryName().getResourcePath().equals(name)) {
				b = block;
				break;
			}
		}
		if (b == null) {
			MISysInfo.err("发现了一个没有对应方块的矿石生成器["
					+ name + "]");
			RegisterHelp.errClass(clazz, "没有对应的方块", null);
			return;
		}
		BlockRegistryMachine.Blocks.worldCreate.put(b, new WorldCreater(annotation, b));
	}
	
}