package xyz.emptydreams.mi.api.utils.data.auto;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.exception.IntransitException;
import xyz.emptydreams.mi.api.utils.IOUtils;
import xyz.emptydreams.mi.api.utils.container.Wrapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 封装了数据写入和读取的操作
 * @author EmptyDreams
 */
public final class DataOperator {

	public static void writeByte(NBTTagCompound nbt, String name, byte data) {
		nbt.setByte(name, data);
	}
	public static void readByte(NBTTagCompound nbt, String name, Consumer<Byte> setter) {
		setter.accept(nbt.getByte(name));
	}

	public static void writeShort(NBTTagCompound nbt, String name, short data) {
		nbt.setShort(name, data);
	}
	public static void readShort(NBTTagCompound nbt, String name, Consumer<Short> setter) {
		setter.accept(nbt.getShort(name));
	}

	public static void writeInt(NBTTagCompound nbt, String name, int data) {
		nbt.setInteger(name, data);
	}
	public static void readInt(NBTTagCompound nbt, String name, Consumer<Integer> setter) {
		setter.accept(nbt.getInteger(name));
	}

	public static void writeLong(NBTTagCompound nbt, String name, long data) {
		nbt.setLong(name, data);
	}
	public static void readLong(NBTTagCompound nbt, String name, Consumer<Long> setter) {
		setter.accept(nbt.getLong(name));
	}

	public static void writeFloat(NBTTagCompound nbt, String name, float data) {
		nbt.setFloat(name, data);
	}
	public static void readFloat(NBTTagCompound nbt, String name, Consumer<Float> setter) {
		setter.accept(nbt.getFloat(name));
	}

	public static void writeDouble(NBTTagCompound nbt, String name, double data) {
		nbt.setDouble(name, data);
	}
	public static void readDouble(NBTTagCompound nbt, String name, Consumer<Double> setter) {
		setter.accept(nbt.getDouble(name));
	}

	public static void writeBoolean(NBTTagCompound nbt, String name, boolean data) {
		nbt.setBoolean(name, data);
	}
	public static void readBoolean(NBTTagCompound nbt, String name, Consumer<Boolean> setter) {
		setter.accept(nbt.getBoolean(name));
	}

	public static void writeString(NBTTagCompound nbt, String name, String data) {
		nbt.setString(name, data);
	}
	public static void readString(NBTTagCompound nbt, String name, Consumer<String> setter) {
		setter.accept(nbt.getString(name));
	}

	public static void writeStringBuilder(NBTTagCompound nbt, String name, StringBuilder data) {
		if (data == null) return;
		nbt.setString(name, data.toString());
	}
	public static void readStringBuilder(NBTTagCompound nbt, String name, Consumer<StringBuilder> setter) {
		if (!nbt.hasKey(name)) setter.accept(null);
		else setter.accept(new StringBuilder(nbt.getString(name)));
	}

	public static void writeStringBuffer(NBTTagCompound nbt, String name, StringBuffer data) {
		if (data == null) return;
		nbt.setString(name, data.toString());
	}
	public static void readStringBuffer(NBTTagCompound nbt, String name, Consumer<StringBuffer> setter) {
		if (!nbt.hasKey(name)) setter.accept(null);
		else setter.accept(new StringBuffer(nbt.getString(name)));
	}

	public static void writeByteArray(NBTTagCompound nbt, String name, byte[] data) {
		nbt.setByteArray(name, data);
	}
	public static void readByteArray(NBTTagCompound nbt, String name, Consumer<byte[]> setter) {
		setter.accept(nbt.getByteArray(name));
	}

	public static void writeIntArray(NBTTagCompound nbt, String name, int[] data) {
		nbt.setIntArray(name, data);
	}
	public static void readIntArray(NBTTagCompound nbt, String name, Consumer<int[]> setter) {
		setter.accept(nbt.getIntArray(name));
	}

	public static void writePackByteArray(NBTTagCompound nbt, String name, Byte[] data) {
		byte[] real = new byte[data.length];
		for (int i = 0; i < real.length; i++) {
			real[i] = data[i];
		}
		writeByteArray(nbt, name, real);
	}
	public static void readPackByteArray(NBTTagCompound nbt, String name, Consumer<Byte[]> setter) {
		Wrapper<byte[]> read = new Wrapper<>();
		readByteArray(nbt, name, read::set);
		@SuppressWarnings("ConstantConditions")
		Byte[] result = new Byte[read.get().length];
		for (int i = 0; i < result.length; i++) {
			result[i] = read.get()[i];
		}
		setter.accept(result);
	}

	public static void writePackIntArray(NBTTagCompound nbt, String name, Integer[] data) {
		int[] real = Arrays.stream(data).mapToInt(it -> it).toArray();
		writeIntArray(nbt, name, real);
	}
	public static void readPackIntArray(NBTTagCompound nbt, String name, Consumer<Integer[]> setter) {
		Wrapper<int[]> read = new Wrapper<>();
		readIntArray(nbt, name, read::set);
		//noinspection ConstantConditions
		setter.accept(Arrays.stream(read.get()).boxed().toArray(Integer[]::new));
	}

	public static void writeUniqueID(NBTTagCompound nbt, String name, UUID data) {
		nbt.setUniqueId(name, data);
	}
	public static void readUniqueID(NBTTagCompound nbt, String name, Consumer<UUID> setter) {
		setter.accept(nbt.getUniqueId(name));
	}

	public static void writeTag(NBTTagCompound nbt, String name, NBTBase data) {
		nbt.setTag(name, data);
	}
	public static void readTag(NBTTagCompound nbt, String name, Consumer<NBTBase> setter) {
		setter.accept(nbt.getTag(name));
	}

	public static void writeEnum(NBTTagCompound nbt, String name, Enum<?> data) {
		nbt.setInteger(name + ":index", data.ordinal());
		writeClass(nbt, name, data.getClass());
	}
	public static void readEnum(NBTTagCompound nbt, String name, Consumer<Enum<?>> setter) {
		int index = nbt.getInteger(name + ":index");
		try {
			Wrapper<Class<?>> clazz = new Wrapper<>();
			readClass(nbt, name, clazz::set);
			//noinspection ConstantConditions
			setter.accept(((Enum<?>[]) clazz.get().getMethod("values", (Class<?>) null)
											.invoke(null, (Object) null))[index]);
		} catch (Exception e) {
			throw new IntransitException("数据自动读写出现了意料之外的错误", e);
		}
	}

	public static void writeSerializable(NBTTagCompound nbt, String name, INBTSerializable<?> data) {
		if (data == null) return;
		nbt.setTag(name, data.serializeNBT());
		writeClass(nbt, name + ":name",data.getClass());
	}
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void readSerializable(NBTTagCompound nbt, String name, DataInfo<INBTSerializable<?>> setter) {
		NBTBase base = nbt.getTag(name);
		try {
			INBTSerializable serializable = setter.getDefault();
			if (serializable == null) {
				Wrapper<Class<?>> clazz = new Wrapper<>();
				readClass(nbt, name, clazz::set);
				//noinspection ConstantConditions
				serializable = (INBTSerializable) clazz.get().newInstance();
			}
			serializable.deserializeNBT(base);
			setter.accept(serializable);
		} catch (Exception e) {
			throw new IntransitException("数据自动读写出现了意料之外的错误[" + name + "]", e);
		}
	}

	public static void writePos(NBTTagCompound nbt, String name, BlockPos data) {
		if (data == null) return;
		IOUtils.writeBlockPos(nbt, data, name);
	}
	public static void readPos(NBTTagCompound nbt, String name, Consumer<BlockPos> setter) {
		BlockPos pos = IOUtils.tryReadBlockPos(nbt, name);
		setter.accept(pos);
	}

	public static void writeCollection(NBTTagCompound nbt, String name, Collection<?> data) {
		if (data == null || data.size() == 0) return;
		writeClass(nbt, name + ":name", data.getClass());
		nbt.setInteger(name, data.size());
		int index = 0;
		for (Object o : data) {
			String str = name + index;
			writeAuto(nbt, str, o);
			writeClass(nbt, str + "name", o.getClass());
			++index;
		}
	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void readCollection(NBTTagCompound nbt, String name, DataInfo<Collection<?>> setter) {
		int size = nbt.getInteger(name);
		if (size == 0) return;
		Collection collection = setter.getDefault();
		if (collection == null) {
			try {
				Wrapper<Class<?>> temp = new Wrapper<>();
				readClass(nbt, name + ":name", temp::set);
				//noinspection ConstantConditions
				collection = (Collection) temp.get().newInstance();
			} catch (Exception e) {
				throw new IntransitException("数据自动读写出现了意料之外的错误", e);
			}
		}
		Wrapper<Class<?>> clazz = new Wrapper<>();
		for (int i = 0; i < size; ++i) {
			String str = name + i;
			readClass(nbt, str + "name", clazz::set);
			collection.add(readAuto(nbt, str, clazz.get()));
		}
		setter.accept(collection);
	}

	public static void writeMap(NBTTagCompound nbt, String name, Map<?, ?> data) {
		if (data == null || data.size() == 0) return;
		nbt.setInteger(name, data.size());
		writeClass(nbt, name + "name", data.getClass());
		int k = 0;
		for (Map.Entry<?, ?> entry : data.entrySet()) {
			String str = name + k++;
			writeAuto(nbt, str + "key", entry.getKey());
			writeAuto(nbt, str + "value", entry.getValue());
			writeClass(nbt, str + "kn", entry.getKey().getClass());
			writeClass(nbt, str + "vn", entry.getValue().getClass());
		}
	}
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void readMap(NBTTagCompound nbt, String name, DataInfo<Map<?,?>> setter) {
		int size = nbt.getInteger(name);
		if (size == 0) return;
		Map map = setter.getDefault();
		if (map == null) {
			try {
				Wrapper<Class<?>> clazz = new Wrapper<>();
				readClass(nbt, name + "name", clazz::set);
				//noinspection ConstantConditions
				map = (Map) clazz.get().newInstance();
			} catch (Exception e) {
				throw new IntransitException("数据自动读写出现了意料之外的错误", e);
			}
		}
		Wrapper<Class<?>> keyClazz = new Wrapper<>();
		Wrapper<Class<?>> valueClazz = new Wrapper<>();
		for (int i = 0; i < size; ++i) {
			String str = name + i;
			readClass(nbt, str + "kn", keyClazz::set);
			readClass(nbt, str + "vn", valueClazz::set);
			Object key = readAuto(nbt, str + "key", keyClazz.get());
			Object value = readAuto(nbt, str + "value", valueClazz.get());
			map.put(key, value);
		}
		setter.accept(map);
	}

	public static void writeVoltage(NBTTagCompound nbt, String name, IVoltage data) {
		if (data == null) return;
		nbt.setInteger(name, data.getVoltage());
		nbt.setDouble(name + ":loss", data.getLossIndex());
	}
	public static void readVoltage(NBTTagCompound nbt, String name, Consumer<IVoltage> setter) {
		if (!nbt.hasKey(name)) return;
		int voltage = nbt.getInteger(name);
		double loss = nbt.getDouble(name + ":loss");
		setter.accept(IVoltage.getInstance(voltage, loss));
	}

	public static void writeClass(NBTTagCompound nbt, String name, Class<?> data) {
		if (data == null) return;
		int key = ClassMap.loadWorld().loadClass(data);
		nbt.setInteger(name, key);
	}
	public static void readClass(NBTTagCompound nbt, String name, Consumer<Class<?>> setter) {
		int value = nbt.getInteger(name);
		if (value != 0)
			setter.accept(ClassMap.loadWorld().readClass(value));
	}

	public static void writeElement(NBTTagCompound nbt, String name, ItemElement data) {
		if (data == null) return;
		nbt.setTag(name, data.serializeNBT());
	}
	public static void readElement(NBTTagCompound nbt, String name, Consumer<ItemElement> setter) {
		if (nbt.hasKey(name)) setter.accept(ItemElement.instance(nbt.getCompoundTag(name)));
	}
	
	public static void writeAuto(NBTTagCompound nbt, String name, Object data) {
		DataType.from(data.getClass()).write(nbt, name, data);
	}
	public static void readAuto(NBTTagCompound nbt, String name, Object obj, Field field) {
		DataType.from(field.getType()).read(nbt, name, obj, field);
	}
	public static <T> T readAuto(NBTTagCompound nbt, String name, Class<T> returnType) {
		return DataType.from(returnType).read(nbt, name, returnType);
	}

	/**
	 * 无异常的将variable写入到指定对象中
	 * @param obj 指定对象
	 * @param field 句柄
	 * @param variable 变量
	 */
	public static void writeToField(Object obj, Field field, Object variable) {
		try {
			field.set(obj, variable);
		} catch (IllegalAccessException e) {
			throw new IntransitException("程序出现了意料之外的错误，可能是用户传入了错误的Field导致的", e);
		}
	}

}