package top.kmar.mi.api.register.machines;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import top.kmar.mi.api.register.AutoRegisterMachine;
import top.kmar.mi.api.register.others.AutoManager;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;

/**
 * 自动注册类注册机
 * @author EmptyDreams
 */
public class AutoManagerRegistryMachine extends AutoRegisterMachine<AutoManager, Object> {
	
	@Nonnull
	@Override
	public Class<AutoManager> getTargetClass() {
		return AutoManager.class;
	}
	
	@Override
	public void registry(Class<?> clazz, AutoManager annotation, Object data) {
		Function<Field, Object> getter = field -> {
			try {
				if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
				return field.get(null);
			} catch (IllegalAccessException e) {
				RegisterHelp.errField(clazz, field.getName(), "无法访问", e);
				return null;
			}
		};
		
		for (Field field : clazz.getFields()) {
			if ((annotation.block() && Block.class.isAssignableFrom(field.getType()))) {
				if (Modifier.isStatic(field.getModifiers())) {
					Block block = (Block) getter.apply(field);
					if (block == null) continue;
					BlockRegistryMachine.addAutoBlock(block);
					if (annotation.blockCustom()) {
						RegisterHelp.invokeStaticMethod(clazz, "blockCustom", block);
					}
				}
			} else if (annotation.item() && Item.class.isAssignableFrom(field.getType())) {
				if (Modifier.isStatic(field.getModifiers())) {
					Item item = (Item) getter.apply(field);
					if (item == null) continue;
					ItemRegistryMachine.addAutoItem(item);
					if (annotation.itemCustom()) {
						RegisterHelp.invokeStaticMethod(clazz, "itemCustom", item);
					}
				}
			}
		}
	}
	
}