package xyz.emptydreams.mi.api.utils.data;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.utils.BlockPosUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动化的TE数据处理
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface TEHelper {
	
	/**
	 * 向指定NBT写入需要自动写入的数据
	 */
	default NBTTagCompound writeToNBT(NBTTagCompound data) {
		Class<?> clazz = getClass();
		while (clazz != null && clazz != TileEntity.class) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				Storage storage = field.getAnnotation(Storage.class);
				if (storage != null && !Modifier.isStatic(field.getModifiers())) {
					String name;
					if ("".equals(storage.name())) {
						name = field.getName();
					} else {
						name = storage.name();
					}
					DataType type = storage.value();
					if (type == DataType.AUTO) {
						type = DataType.from(field.getType());
					}
					
					try {
						field.setAccessible(true);
						type.write(data, name, field.get(this));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch (ClassCastException e) {
						e.initCause(new ClassCastException("标记的需要读写的数据类型不继承自INBTSerializable"));
						throw e;
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return data;
	}
	
	/** 读取数据 */
	default void readFromNBT(NBTTagCompound data) {
		Class<?> clazz = getClass();
		while (clazz != null && clazz != TileEntity.class) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				Storage storage = field.getAnnotation(Storage.class);
				if (storage != null && !Modifier.isStatic(field.getModifiers())) {
					String name;
					if ("".equals(storage.name())) {
						name = field.getName();
					} else {
						name = storage.name();
					}
					DataType type = storage.value();
					if (type == DataType.AUTO) {
						type = DataType.from(field.getType());
					}
					
					try {
						field.setAccessible(true);
						if (!data.hasKey(name + ":null"))
							field.set(this, type.read(data, name, null));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch (ClassCastException e) {
						e.initCause(new ClassCastException("标记的需要读写的数据类型不继承自INBTSerializable"));
						throw e;
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
	
	/**
	 * 用于标志需要被离线的数据，不能被static修饰.<br>
	 * 支持且仅支持可以直接写入到{@link net.minecraft.nbt.NBTTagCompound}中的数据
	 */
	@Documented
	@Retention(RUNTIME)
	@Target(ElementType.FIELD)
	@interface Storage {
		
		/** 数据名称，使用默认则表示自动设置 */
		String name() default "";
	
		/** 数据类型，手动指定数据类型可以减少运算量 */
		DataType value() default DataType.AUTO;
		
	}
	
}
