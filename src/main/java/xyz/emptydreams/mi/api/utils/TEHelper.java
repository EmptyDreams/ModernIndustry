package xyz.emptydreams.mi.api.utils;

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

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.emptydreams.mi.utils.BlockPosUtil;

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
					DataType type = storage.type();
					if (type == DataType.AUTO) {
						type = getDataType(field.getType());
					}
					
					try {
						field.setAccessible(true);
						Object o = field.get(this);
						if (o == null) data.setBoolean(name + ":null", true);
						else _wirte(o, type, data, name, false);
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
					DataType type = storage.type();
					if (type == DataType.AUTO) {
						type = getDataType(field.getType());
					}
					
					try {
						field.setAccessible(true);
						if (!data.hasKey(name + ":null")) _read(field, type, data, name, this);
					} catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
						throw new RuntimeException(e);
					} catch (ClassCastException e) {
						e.initCause(new ClassCastException("标记的需要读写的数据类型不继承自INBTSerializable"));
						throw e;
					} catch (InstantiationException e) {
						throw new RuntimeException("需要存储的类不包含可见的默认构造函数", e);
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
		DataType type() default DataType.AUTO;
		
	}
	
	static void _read(Field field, DataType type, NBTTagCompound data, String name, Object o)
			throws IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchFieldException {
		switch (type) {
			case BYTE: field.set(o, data.getByte(name)); break;
			case SHORT: field.set(o, data.getShort(name)); break;
			case INT: field.set(o, data.getInteger(name)); break;
			case LONG: field.set(o, data.getLong(name)); break;
			case FLOAT: field.set(o, data.getFloat(name)); break;
			case DOUBLE: field.set(o, data.getDouble(name)); break;
			case BOOLEAN: field.set(o, data.getBoolean(name)); break;
			case STRING: field.set(o, data.getString(name)); break;
			case ARRAY_BYTE: field.set(o, data.getByteArray(name)); break;
			case ARRAY_INT: field.set(o, data.getIntArray(name)); break;
			case UNIQUE_ID: field.set(o, data.getUniqueId(name)); break;
			case TAG: field.set(o, data.getTag(name)); break;
			case POS: field.set(o, BlockPosUtil.readBlockPos(data, name)); break;
			case ENUM: field.set(o, field.getType().getField(data.getString(name)).get(null)); break;
			case COLLECTION: case MAP:
				AtomicReference atomic = new AtomicReference<>(field.get(o));
				_readHelper(atomic, type, data, name);
				break;
			default:
				NBTBase base = data.getTag(name);
				if (base != null) ((INBTSerializable) field.get(o)).deserializeNBT(base);
				break;
		}
	}
	
	static void _readHelper(AtomicReference collection, DataType type, NBTTagCompound data, String name)
			throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchFieldException {
		if (type == DataType.COLLECTION) _readCollection((AtomicReference<Collection>) collection, data, name);
		else if (type == DataType.MAP) _readMap((AtomicReference<Map>) collection, data, name);
		else throw new IllegalAccessException("输入的参数类型在支持的类型之外：" + type.name());
	}
	
	static void _readMap(AtomicReference<Map> mapAtomic, NBTTagCompound data, String name)
			throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchFieldException {
		int size = data.getInteger(name + ":size");
		if (mapAtomic.get() == null) mapAtomic.set(new HashMap(size));
		Map map = mapAtomic.get();
		String temp; DataType keyType, valueType;
		for (int i = 0; i < size; ++i) {
			temp = name + i;
			keyType = DataType.values()[data.getInteger(temp + ":keyType")];
			valueType = DataType.values()[data.getInteger(temp + ":valueType")];
			map.put(_readElement(data, keyType, temp + ":key"),
					_readElement(data, valueType, temp + ":value"));
		}
	}
	
	static void _readCollection(AtomicReference<Collection> collection,
	                            NBTTagCompound data, String name)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
		int size = data.getInteger(name + ":size");
		if (collection.get() == null) collection.set(new ArrayList(size));
		Collection co = collection.get();
		String temp; DataType type;
		for (int i = 0; i < size; ++i) {
			temp = name + i;
			type = DataType.values()[data.getInteger(temp + ":type")];
			co.add(_readElement(data, type, temp));
		}
	}
	
	static Object _readElement(NBTTagCompound data, DataType type, String name)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
		switch (type) {
			case BYTE: return data.getByte(name);
			case SHORT: return data.getShort(name);
			case INT: return data.getInteger(name);
			case LONG: return data.getLong(name);
			case FLOAT: return data.getFloat(name);
			case DOUBLE: return data.getDouble(name);
			case BOOLEAN: return data.getBoolean(name);
			case STRING: return data.getString(name);
			case ARRAY_BYTE: return data.getByteArray(name);
			case ARRAY_INT: return data.getIntArray(name);
			case UNIQUE_ID: return data.getUniqueId(name);
			case TAG: return data.getTag(name);
			case ENUM:
				return Class.forName(data.getString(name + ":class")).getField(data.getString(name)).get(null);
			case OTHER:
				Class<?> clazz = Class.forName(data.getString(name + ":class"));
				INBTSerializable o = (INBTSerializable) clazz.newInstance();
				o.deserializeNBT(data.getTag(name));
				return o;
			case POS: return BlockPosUtil.readBlockPos(data, name);
			case COLLECTION: {
				AtomicReference<Collection> atomic = new AtomicReference<>();
				_readCollection(atomic, data, name);
				return atomic.get();
			}
			case MAP: {
				AtomicReference<Map> atomic = new AtomicReference<>();
				_readMap(atomic, data, name);
				return atomic.get();
			}
		}
		return null;
	}
	
	static void _wirte(Object field, DataType type, NBTTagCompound data, String name, boolean isSon) {
		switch (type) {
			case BYTE: data.setByte(name, (byte) field); break;
			case SHORT: data.setShort(name, (short) field); break;
			case INT: data.setInteger(name, (int) field); break;
			case LONG: data.setLong(name, (long) field); break;
			case FLOAT: data.setFloat(name, (float) field); break;
			case DOUBLE: data.setDouble(name, (double) field); break;
			case BOOLEAN: data.setBoolean(name, (boolean) field); break;
			case STRING: data.setString(name, field.toString()); break;
			case ARRAY_BYTE: data.setByteArray(name, (byte[]) field); break;
			case ARRAY_INT: data.setIntArray(name, (int[]) field); break;
			case UNIQUE_ID: data.setUniqueId(name, (UUID) field); break;
			case TAG: data.setTag(name, (NBTBase) field); break;
			case POS: BlockPosUtil.writeBlockPos(data, (BlockPos) field, name); break;
			case ENUM:
				data.setString(name, ((Enum) field).name());
				if (isSon) data.setString(name + ":class", field.getClass().getName());
				break;
			case COLLECTION: {
				Collection<?> it = (Collection<?>) field;
				int k = 0;
				DataType t;
				String temp;
				for (Object o : it) {
					temp = name + k;
					t = getDataType(o.getClass());
					data.setInteger(temp + ":type", t.ordinal());
					_wirte(t, type, data, temp, true);
					++k;
				}
				data.setInteger(name + ":size", k + 1);
				break;
			}
			case MAP: {
				Map<?, ?> map = (Map<?, ?>) field;
				int k = 0;
				DataType keyType, valueType;
				String temp;
				for (Map.Entry o : map.entrySet()) {
					temp = name + k;
					keyType = getDataType(o.getKey().getClass());
					valueType = getDataType(o.getValue().getClass());
					data.setInteger(temp + ":keyType", keyType.ordinal());
					data.setInteger(temp + ":valueType", valueType.ordinal());
					_wirte(o.getKey(), keyType, data, temp + ":key", true);
					_wirte(o.getValue(), valueType, data, temp + ":value", true);
					++k;
				}
				data.setInteger(name + ":size", k);
				break;
			}
			default:
				if (isSon) {
					data.setString(name + ":class", field.getClass().getName());
				}
				data.setTag(name, ((INBTSerializable) field).serializeNBT());
		}
	}
	
	static DataType getDataType(Class<?> clazz) {
		if (int.class == clazz || Integer.class == clazz) {
			return DataType.INT;
		} else if (byte.class == clazz || Byte.class == clazz) {
			return DataType.BYTE;
		} else if (boolean.class == clazz || Boolean.class == clazz) {
			return DataType.BOOLEAN;
		} else if (Vec3i.class.isAssignableFrom(clazz)) {
			return DataType.POS;
		} else if (long.class == clazz || Long.class == clazz) {
			return DataType.LONG;
		} else if (double.class == clazz || Double.class == clazz) {
			return DataType.DOUBLE;
		} else if (float.class == clazz || Float.class == clazz) {
			return DataType.FLOAT;
		} else if (short.class == clazz || Short.class == clazz) {
			return DataType.SHORT;
		} else if (int[].class == clazz) {
			return DataType.ARRAY_INT;
		} else if (byte[].class == clazz) {
			return DataType.ARRAY_BYTE;
		} else if (String.class == clazz) {
			return DataType.STRING;
		} else if (UUID.class == clazz) {
			return DataType.UNIQUE_ID;
		} else if (Enum.class.isAssignableFrom(clazz)) {
			return DataType.ENUM;
		} else if (Collection.class.isAssignableFrom(clazz)) {
			return DataType.COLLECTION;
		} else if (Map.class.isAssignableFrom(clazz)) {
			return DataType.MAP;
		} else {
			if (NBTBase.class.isAssignableFrom(clazz)) return DataType.TAG;
			else if (INBTSerializable.class.isAssignableFrom(clazz)) return DataType.OTHER;
			else throw new ClassCastException("该对象不能被写入到NBT中：" + clazz);
		}
	}
	
}
