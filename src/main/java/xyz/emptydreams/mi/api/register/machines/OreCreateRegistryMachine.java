package xyz.emptydreams.mi.api.register.machines;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.block.OreCreate;
import xyz.emptydreams.mi.api.register.block.WorldCreater;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

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
	@SuppressWarnings("unchecked")
	public void registryAll(ASMDataTable asm) {
		Object data = parse(asm);
		Class<?> cache = getTargetClass();
		if (!cache.isAnnotation())
			throw new IllegalArgumentException("getTargetClass方法返回了非注解的class");
		Class<? extends Annotation> annotation = (Class<? extends Annotation>) cache;
		Set<ASMDataTable.ASMData> dataSet = asm.getAll(annotation.getName());
		for (ASMDataTable.ASMData asmData : dataSet) {
			try {
				Class<?> clazz = Class.forName(asmData.getClassName());
				OreCreate an = (OreCreate) clazz.getAnnotation(annotation);
				if (an != null) registry(clazz, an, data);
				else {
					for (Field field : clazz.getFields()) {
						an = (OreCreate) field.getAnnotation(annotation);
						if (an == null) continue;
						registry(field.getType(), an, data);
					}
				}
			} catch (Throwable e) {
				MISysInfo.err("注册[" + asmData.getClassName() + "]时遇到意料之外的错误", e);
			}
		}
		atEnd();
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