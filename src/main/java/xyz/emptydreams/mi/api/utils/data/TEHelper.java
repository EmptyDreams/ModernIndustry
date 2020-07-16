package xyz.emptydreams.mi.api.utils.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动化的TE数据处理
 * @author EmptyDreams
 */
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
						if (type == DataType.SERIALIZABLE && !storage.def().equals(""))
							field.set(this, clazz.getDeclaredMethod(storage.def(), (Class<?>) null));
						type.read(data, name, this, field);
					} catch (ClassCastException | NoSuchMethodException | IllegalAccessException e) {
						throw new RuntimeException(e);
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

		/**
		 * 获取默认值的方法名称（目前仅{@link DataType#SERIALIZABLE}支持默认值），
		 * 留空表示由MI计算默认值，默认值必须由运行时计算产生，离线存储时MI不会存储默认值<br>
		 * 例：输入"getDefault":<br>
		 * 则类中应当包含<b>public [数据类型] getDefault()</b>方法<br>
		 * 其中[数据类型]与要读取的类型保持一致
		 */
		String def() default "";

	}
	
}
