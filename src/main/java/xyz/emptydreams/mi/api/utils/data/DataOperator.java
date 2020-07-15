package xyz.emptydreams.mi.api.utils.data;

import io.netty.handler.codec.UnsupportedMessageTypeException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.utils.BlockPosUtil;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * 封装了数据写入和读取的操作
 * @author EmptyDreams
 */
public final class DataOperator {

	public static void writeByte(NBTTagCompound nbt, String name, byte data) {
		nbt.setByte(name, data);
	}
	public static byte readByte(NBTTagCompound nbt, String name) {
		return nbt.getByte(name);
	}

	public static void writeShort(NBTTagCompound nbt, String name, short data) {
		nbt.setShort(name, data);
	}
	public static short readShort(NBTTagCompound nbt, String name) {
		return nbt.getShort(name);
	}

	public static void writeInt(NBTTagCompound nbt, String name, int data) {
		nbt.setInteger(name, data);
	}
	public static int readInt(NBTTagCompound nbt, String name) {
		return nbt.getInteger(name);
	}

	public static void writeLong(NBTTagCompound nbt, String name, long data) {
		nbt.setLong(name, data);
	}
	public static long readLong(NBTTagCompound nbt, String name) {
		return nbt.getLong(name);
	}

	public static void writeFloat(NBTTagCompound nbt, String name, float data) {
		nbt.setFloat(name, data);
	}
	public static float readFloat(NBTTagCompound nbt, String name) {
		return nbt.getFloat(name);
	}

	public static void writeDouble(NBTTagCompound nbt, String name, double data) {
		nbt.setDouble(name, data);
	}
	public static double readDouble(NBTTagCompound nbt, String name) {
		return nbt.getDouble(name);
	}

	public static void writeBoolean(NBTTagCompound nbt, String name, boolean data) {
		nbt.setBoolean(name, data);
	}
	public static boolean readBoolean(NBTTagCompound nbt, String name) {
		return nbt.getBoolean(name);
	}

	public static void writeString(NBTTagCompound nbt, String name, String data) {
		nbt.setString(name, data);
	}
	public static String readString(NBTTagCompound nbt, String name) {
		return nbt.getString(name);
	}

	public static void writeStringBuilder(NBTTagCompound nbt, String name, StringBuilder data) {
		if (data == null) return;
		nbt.setString(name, data.toString());
	}
	public static StringBuilder readStringBuilder(NBTTagCompound nbt, String name) {
		if (!nbt.hasKey(name)) return null;
		return new StringBuilder(nbt.getString(name));
	}

	public static void writeStringBuffer(NBTTagCompound nbt, String name, StringBuffer data) {
		if (data == null) return;
		nbt.setString(name, data.toString());
	}
	public static StringBuffer readStringBuffer(NBTTagCompound nbt, String name) {
		if (!nbt.hasKey(name)) return null;
		return new StringBuffer(nbt.getString(name));
	}

	public static void writeByteArray(NBTTagCompound nbt, String name, byte[] data) {
		nbt.setByteArray(name, data);
	}
	public static byte[] readByteArray(NBTTagCompound nbt, String name) {
		return nbt.getByteArray(name);
	}

	public static void writeIntArray(NBTTagCompound nbt, String name, int[] data) {
		nbt.setIntArray(name, data);
	}
	public static int[] readIntArray(NBTTagCompound nbt, String name) {
		return nbt.getIntArray(name);
	}

	public static void writePackByteArray(NBTTagCompound nbt, String name, Byte[] data) {
		byte[] real = new byte[data.length];
		for (int i = 0; i < real.length; i++) {
			real[i] = data[i];
		}
		writeByteArray(nbt, name, real);
	}
	public static Byte[] readPackByteArray(NBTTagCompound nbt, String name) {
		byte[] read = readByteArray(nbt, name);
		Byte[] result = new Byte[read.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = read[i];
		}
		return result;
	}

	public static void writePackIntArray(NBTTagCompound nbt, String name, Integer[] data) {
		int[] real = Arrays.stream(data).mapToInt(it -> it).toArray();
		writeIntArray(nbt, name, real);
	}
	public static Integer[] readPackIntArray(NBTTagCompound nbt, String name) {
		int[] read = readIntArray(nbt, name);
		return Arrays.stream(read).boxed().toArray(Integer[]::new);
	}

	public static void writeUniqueID(NBTTagCompound nbt, String name, UUID data) {
		nbt.setUniqueId(name, data);
	}
	public static UUID readUniqueID(NBTTagCompound nbt, String name) {
		return nbt.getUniqueId(name);
	}

	public static void writeTag(NBTTagCompound nbt, String name, NBTBase data) {
		nbt.setTag(name, data);
	}
	public static NBTBase readTag(NBTTagCompound nbt, String name) {
		return nbt.getTag(name);
	}

	public static void writeEnum(NBTTagCompound nbt, String name, Enum<?> data) {
		nbt.setInteger(name + ":index", data.ordinal());
		nbt.setString(name, data.getClass().getName());
	}
	public static Enum<?> readEnum(NBTTagCompound nbt, String name) {
		int index = nbt.getInteger(name + ":index");
		try {
			Class<?> clazz = Class.forName(nbt.getString(name));
			return ((Enum<?>[]) clazz.getMethod("values", (Class<?>) null)
					.invoke(null, (Object) null))[index];
		} catch (Exception e) {
			throw new RuntimeException("数据自动读写出现了意料之外的错误", e);
		}
	}

	public static void writeSerializable(NBTTagCompound nbt, String name, INBTSerializable<?> data) {
		if (data == null) return;
		nbt.setTag(name, data.serializeNBT());
		nbt.setString(name + ":name", data.getClass().getName());
	}
	public static INBTSerializable<?> readSerializable(NBTTagCompound nbt, String name) {
		NBTBase base = nbt.getTag(name);
		try {
			Class<?> clazz = Class.forName(nbt.getString(name + ":name"));
			@SuppressWarnings("rawtypes")
			INBTSerializable serializable = (INBTSerializable) clazz.newInstance();
			//noinspection unchecked
			serializable.deserializeNBT(base);
			return serializable;
		} catch (Exception e) {
			throw new RuntimeException("数据自动读写出现了意料之外的错误", e);
		}
	}

	public static void writePos(NBTTagCompound nbt, String name, BlockPos data) {
		BlockPosUtil.writeBlockPos(nbt, data, name);
	}
	public static BlockPos readPos(NBTTagCompound nbt, String name) {
		return BlockPosUtil.readBlockPos(nbt, name);
	}

	public static void writeCollection(NBTTagCompound nbt, String name, Collection<?> data) {
		if (data == null || data.size() == 0) return;
		nbt.setString(name + ":name", data.getClass().getName());
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
	public static Collection<?> readCollection(NBTTagCompound nbt, String name) {
		int size = nbt.getInteger(name);
		if (size == 0) return null;
		Collection collection;
		try {
			collection = (Collection) Class.forName(nbt.getString(name + ":name")).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("数据自动读写出现了意料之外的错误", e);
		}
		for (int i = 0; i < size; ++i) {
			String str = name + i;
			collection.add(readAuto(nbt, str, readClass(nbt, str + "name")));
		}
		return collection;
	}

	public static void writeMap(NBTTagCompound nbt, String name, Map<?, ?> data) {
		if (data == null || data.size() == 0) return;
		nbt.setInteger(name, data.size());
		nbt.setString(name + "name", data.getClass().getName());
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
	public static Map<?, ?> readMap(NBTTagCompound nbt, String name) {
		int size = nbt.getInteger(name);
		if (size == 0) return null;
		Map map;
		try {
			map = (Map) Class.forName(nbt.getString(name + ":name")).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("数据自动读写出现了意料之外的错误", e);
		}
		for (int i = 0; i < size; ++i) {
			String str = name + i;
			Object key = readAuto(nbt, str + "key", readClass(nbt, str + "kn"));
			Object value = readAuto(nbt, str + "value", readClass(nbt, str + "vn"));
			map.put(key, value);
		}
		return map;
	}

	public static void writeVoltage(NBTTagCompound nbt, String name, IVoltage data) {
		if (data == null) return;
		nbt.setInteger(name, data.getVoltage());
		nbt.setDouble(name + ":loss", data.getLossIndex());
	}
	public static IVoltage readVoltage(NBTTagCompound nbt, String name) {
		if (!nbt.hasKey(name)) return null;
		int voltage = nbt.getInteger(name);
		double loss = nbt.getDouble(name + ":loss");
		return IVoltage.getInstance(voltage, loss);
	}

	public static void writeClass(NBTTagCompound nbt, String name, Class<?> data) {
		if (data == null) return;
		nbt.setString(name, data.getName());
	}
	public static Class<?> readClass(NBTTagCompound nbt, String name) {
		try {
			if (nbt.hasKey(name)) return Class.forName(nbt.getString(name));
		} catch (Exception e) {
			MISysInfo.err("Class读取时发生了意料之外的错误，可能是因为存储了匿名类");
			e.printStackTrace();
		}
		return null;
	}

	public static void writeAuto(NBTTagCompound nbt, String name, Object data) {
		Class<?> clazz = data.getClass();
		if (clazz == int.class || clazz == Integer.class) writeInt(nbt, name, (int) data);
		else if (clazz == boolean.class || clazz == Boolean.class) writeBoolean(nbt, name, (boolean) data);
		else if (data instanceof NBTBase) writeTag(nbt, name, (NBTBase) data);
		else if (data instanceof INBTSerializable) writeSerializable(nbt, name, (INBTSerializable<?>) data);
		else if (clazz == BlockPos.class) writePos(nbt, name, (BlockPos) data);
		else if (data instanceof Enum) writeEnum(nbt, name, (Enum<?>) data);
		else if (data instanceof Collection) writeCollection(nbt, name, (Collection<?>) data);
		else if (data instanceof Map) writeMap(nbt, name, (Map<?, ?>) data);
		else if (data instanceof IVoltage) writeVoltage(nbt, name, (IVoltage) data);
		else if (clazz == byte.class || clazz == Byte.class) writeByte(nbt, name, (byte) data);
		else if (clazz == byte[].class) writeByteArray(nbt, name, (byte[]) data);
		else if (clazz == int[].class) writeIntArray(nbt, name, (int[]) data);
		else if (clazz == long.class) writeLong(nbt, name, (long) data);
		else if (clazz == double.class) writeDouble(nbt, name, (double) data);
		else if (clazz == short.class) writeShort(nbt, name, (short) data);
		else if (clazz == float.class) writeFloat(nbt, name, (float) data);
		else if (clazz == String.class) writeString(nbt, name, (String) data);
		else if (clazz == UUID.class) writeUniqueID(nbt, name, (UUID) data);
		else if (clazz == Byte[].class) writePackByteArray(nbt, name, (Byte[]) data);
		else if (clazz == Integer[].class) writePackIntArray(nbt, name, (Integer[]) data);
		else if (clazz == StringBuilder.class) writeStringBuilder(nbt, name, (StringBuilder) data);
		else if (clazz == StringBuffer.class) writeStringBuffer(nbt, name, (StringBuffer) data);
		else throw new UnsupportedMessageTypeException(data.getClass().getName());
	}
	public static <T> Object readAuto(NBTTagCompound nbt, String name, Class<T> returnType) {
		if (returnType == int.class || returnType == Integer.class) return readInt(nbt, name);
		else if (returnType == boolean.class || returnType == Boolean.class) return readBoolean(nbt, name);
		else if (NBTBase.class.isAssignableFrom(returnType)) return readTag(nbt, name);
		else if (INBTSerializable.class.isAssignableFrom(returnType)) return readSerializable(nbt, name);
		else if (returnType == BlockPos.class) return readPos(nbt, name);
		else if (Enum.class.isAssignableFrom(returnType)) return readEnum(nbt, name);
		else if (Collection.class.isAssignableFrom(returnType)) return readCollection(nbt, name);
		else if (Map.class.isAssignableFrom(returnType)) return readMap(nbt, name);
		else if (IVoltage.class.isAssignableFrom(returnType)) return readVoltage(nbt, name);
		else if (returnType == byte.class || returnType == Byte.class) return readByte(nbt, name);
		else if (returnType == byte[].class) return readByteArray(nbt, name);
		else if (returnType == int[].class) return readIntArray(nbt, name);
		else if (returnType == long.class) return readLong(nbt, name);
		else if (returnType == double.class) return readDouble(nbt, name);
		else if (returnType == short.class) return readShort(nbt, name);
		else if (returnType == float.class) return readFloat(nbt, name);
		else if (returnType == String.class) return readString(nbt, name);
		else if (returnType == UUID.class) return readUniqueID(nbt, name);
		else if (returnType == Byte[].class) return readPackByteArray(nbt, name);
		else if (returnType == Integer[].class) return readPackIntArray(nbt, name);
		else if (returnType == StringBuilder.class) return readStringBuilder(nbt, name);
		else if (returnType == StringBuffer.class) return readStringBuffer(nbt, name);
		else throw new UnsupportedMessageTypeException(returnType.getName());
	}

}
