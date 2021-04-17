package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.dor.IDataReader;
import xyz.emptydreams.mi.api.dor.IDataWriter;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.exception.IntransitException;
import xyz.emptydreams.mi.api.register.AutoLoader;
import xyz.emptydreams.mi.api.utils.IOUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static xyz.emptydreams.mi.api.utils.data.io.DataTypeRegister.registry;

/**
 * @author EmptyDreams
 */
@AutoLoader
public final class DataTypes {
	
	static {
		registry(new IntData(), clazz -> clazz == int.class || clazz == Integer.class);
		registry(new ByteData(), clazz -> clazz == byte.class || clazz == Byte.class);
		registry(new BooleanData(), clazz -> clazz == boolean.class || clazz == Boolean.class);
		registry(new LongData(), clazz -> clazz == long.class || clazz == Long.class);
		registry(new ByteArrayData(), clazz -> clazz == byte[].class);
		registry(new IntArrayData(), clazz -> clazz == int[].class);
		registry(new ClassData(), clazz -> clazz == Class.class);
		registry(new StringData(), clazz -> clazz == String.class);
		registry(new CollectionData(), Collection.class::isAssignableFrom);
		registry(new PosData(), BlockPos.class::isAssignableFrom);
		registry(new SerializableData(), INBTSerializable.class::isAssignableFrom);
		registry(new ElementData(), clazz -> clazz == ItemElement.class);
		registry(new MapData(), Map.class::isAssignableFrom);
		registry(new DoubleData(), clazz -> clazz == double.class || clazz == Double.class);
		registry(new EnumData(), Enum.class::isAssignableFrom);
		registry(new FloatData(), clazz -> clazz == float.class || clazz == Float.class);
		registry(new NbtData(), clazz -> clazz == NBTTagCompound.class);
		registry(new ShortData(), clazz -> clazz == short.class || clazz == Short.class);
		registry(new StringBuilderData(), StringBuilder.class::isAssignableFrom);
		registry(new StringBufferData(), StringBuffer.class::isAssignableFrom);
		registry(new UuidData(), clazz -> clazz == UUID.class);
		registry(new VoltageData(), IVoltage.class::isAssignableFrom);
		registry(new BytePackageArrayData(), clazz -> clazz == Byte[].class);
		registry(new IntPackageArrayData(), clazz -> clazz == Integer[].class);
	}
	
	public static final class IntData implements IDataIO<Integer> {
		
		@Override
		public void writeToData(IDataWriter writer, Integer data) {
			writer.writeInt(data);
		}
		
		@Override
		public Integer readFromData(IDataReader reader, Supplier<Integer> getter) {
			return reader.readInt();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Integer data) {
			nbt.setInteger(name, data);
		}
		
		@Override
		public Integer readFromNBT(NBTTagCompound nbt, String name, Supplier<Integer> getter) {
			return nbt.getInteger(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Integer data) {
			buf.writeInt(data);
		}
		
		@Override
		public Integer readFromByteBuf(ByteBuf buf, Supplier<Integer> getter) {
			return buf.readInt();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Integer data, Class<R> target) {
			if (target == byte.class || target == Byte.class) {
				return (R) Byte.valueOf(data.byteValue());
			} else if (target == short.class || target == Short.class) {
				return (R) Short.valueOf(data.shortValue());
			} else if (target == long.class || target == Long.class) {
				return (R) Long.valueOf(data.longValue());
			} else if (target == float.class || target == Float.class) {
				return (R) Float.valueOf(data.floatValue());
			} else if (target == double.class || target == Double.class) {
				return (R) Double.valueOf(data.doubleValue());
			} else {
				throw new ClassCastException("int不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class ByteData implements IDataIO<Byte> {
		
		@Override
		public void writeToData(IDataWriter writer, Byte data) {
			writer.writeByte(data);
		}
		
		@Override
		public Byte readFromData(IDataReader reader, Supplier<Byte> getter) {
			return reader.readByte();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Byte data) {
			nbt.setByte(name, data);
		}
		
		@Override
		public Byte readFromNBT(NBTTagCompound nbt, String name, Supplier<Byte> getter) {
			return nbt.getByte(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Byte data) {
			buf.writeByte(data);
		}
		
		@Override
		public Byte readFromByteBuf(ByteBuf buf, Supplier<Byte> getter) {
			return buf.readByte();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Byte data, Class<R> target) {
			if (target == int.class || target == Integer.class) {
				return (R) Integer.valueOf(data.intValue());
			} else if (target == short.class || target == Short.class) {
				return (R) Short.valueOf(data.shortValue());
			} else if (target == long.class || target == Long.class) {
				return (R) Long.valueOf(data.longValue());
			} else if (target == float.class || target == Float.class) {
				return (R) Float.valueOf(data.floatValue());
			} else if (target == double.class || target == Double.class) {
				return (R) Double.valueOf(data.doubleValue());
			} else {
				throw new ClassCastException("byte不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class BooleanData implements IDataIO<Boolean> {
		
		@Override
		public void writeToData(IDataWriter writer, Boolean data) {
			writer.writeBoolean(data);
		}
		
		@Override
		public Boolean readFromData(IDataReader reader, Supplier<Boolean> getter) {
			return reader.readBoolean();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Boolean data) {
			nbt.setBoolean(name, data);
		}
		
		@Override
		public Boolean readFromNBT(NBTTagCompound nbt, String name, Supplier<Boolean> getter) {
			return nbt.getBoolean(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Boolean data) {
			buf.writeBoolean(data);
		}
		
		@Override
		public Boolean readFromByteBuf(ByteBuf buf, Supplier<Boolean> getter) {
			return buf.readBoolean();
		}
		
	}
	
	public static final class LongData implements IDataIO<Long> {
		
		@Override
		public void writeToData(IDataWriter writer, Long data) {
			writer.writeLong(data);
		}
		
		@Override
		public Long readFromData(IDataReader reader, Supplier<Long> getter) {
			return reader.readLong();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Long data) {
			nbt.setLong(name, data);
		}
		
		@Override
		public Long readFromNBT(NBTTagCompound nbt, String name, Supplier<Long> getter) {
			return nbt.getLong(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Long data) {
			buf.writeLong(data);
		}
		
		@Override
		public Long readFromByteBuf(ByteBuf buf, Supplier<Long> getter) {
			return buf.readLong();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Long data, Class<R> target) {
			if (target == byte.class || target == Byte.class) {
				return (R) Byte.valueOf(data.byteValue());
			} else if (target == short.class || target == Short.class) {
				return (R) Short.valueOf(data.shortValue());
			} else if (target == int.class || target == Integer.class) {
				return (R) Integer.valueOf(data.intValue());
			} else if (target == float.class || target == Float.class) {
				return (R) Float.valueOf(data.floatValue());
			} else if (target == double.class || target == Double.class) {
				return (R) Double.valueOf(data.doubleValue());
			} else {
				throw new ClassCastException("long不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class DoubleData implements IDataIO<Double> {
		
		@Override
		public void writeToData(IDataWriter writer, Double data) {
			writer.writeDouble(data);
		}
		
		@Override
		public Double readFromData(IDataReader reader, Supplier<Double> getter) {
			return reader.readDouble();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Double data) {
			nbt.setDouble(name, data);
		}
		
		@Override
		public Double readFromNBT(NBTTagCompound nbt, String name, Supplier<Double> getter) {
			return nbt.getDouble(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Double data) {
			buf.writeDouble(data);
		}
		
		@Override
		public Double readFromByteBuf(ByteBuf buf, Supplier<Double> getter) {
			return buf.readDouble();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Double data, Class<R> target) {
			if (target == byte.class || target == Byte.class) {
				return (R) Byte.valueOf(data.byteValue());
			} else if (target == short.class || target == Short.class) {
				return (R) Short.valueOf(data.shortValue());
			} else if (target == long.class || target == Long.class) {
				return (R) Long.valueOf(data.longValue());
			} else if (target == float.class || target == Float.class) {
				return (R) Float.valueOf(data.floatValue());
			} else if (target == int.class || target == Integer.class) {
				return (R) Integer.valueOf(data.intValue());
			} else {
				throw new ClassCastException("double不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class ShortData implements IDataIO<Short> {
		
		@Override
		public void writeToData(IDataWriter writer, Short data) {
			writer.writeShort(data);
		}
		
		@Override
		public Short readFromData(IDataReader reader, Supplier<Short> getter) {
			return reader.readShort();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Short data) {
			nbt.setShort(name, data);
		}
		
		@Override
		public Short readFromNBT(NBTTagCompound nbt, String name, Supplier<Short> getter) {
			return nbt.getShort(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Short data) {
			buf.writeShort(data);
		}
		
		@Override
		public Short readFromByteBuf(ByteBuf buf, Supplier<Short> getter) {
			return buf.readShort();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Short data, Class<R> target) {
			if (target == byte.class || target == Byte.class) {
				return (R) Byte.valueOf(data.byteValue());
			} else if (target == int.class || target == Integer.class) {
				return (R) Integer.valueOf(data.intValue());
			} else if (target == long.class || target == Long.class) {
				return (R) Long.valueOf(data.longValue());
			} else if (target == float.class || target == Float.class) {
				return (R) Float.valueOf(data.floatValue());
			} else if (target == double.class || target == Double.class) {
				return (R) Double.valueOf(data.doubleValue());
			} else {
				throw new ClassCastException("short不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class FloatData implements IDataIO<Float> {
		
		@Override
		public void writeToData(IDataWriter writer, Float data) {
			writer.writeFloat(data);
		}
		
		@Override
		public Float readFromData(IDataReader reader, Supplier<Float> getter) {
			return reader.readFloat();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Float data) {
			nbt.setFloat(name, data);
		}
		
		@Override
		public Float readFromNBT(NBTTagCompound nbt, String name, Supplier<Float> getter) {
			return nbt.getFloat(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Float data) {
			buf.writeFloat(data);
		}
		
		@Override
		public Float readFromByteBuf(ByteBuf buf, Supplier<Float> getter) {
			return buf.readFloat();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Float data, Class<R> target) {
			if (target == byte.class || target == Byte.class) {
				return (R) Byte.valueOf(data.byteValue());
			} else if (target == short.class || target == Short.class) {
				return (R) Short.valueOf(data.shortValue());
			} else if (target == long.class || target == Long.class) {
				return (R) Long.valueOf(data.longValue());
			} else if (target == int.class || target == Integer.class) {
				return (R) Integer.valueOf(data.intValue());
			} else if (target == double.class || target == Double.class) {
				return (R) Double.valueOf(data.doubleValue());
			} else {
				throw new ClassCastException("float不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class IntArrayData implements IDataIO<int[]> {
		
		@Override
		public void writeToData(IDataWriter writer, int[] data) {
			writer.writeIntArray(data);
		}
		
		@Override
		public int[] readFromData(IDataReader reader, Supplier<int[]> getter) {
			return reader.readIntArray();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, int[] data) {
			nbt.setIntArray(name, data);
		}
		
		@Override
		public int[] readFromNBT(NBTTagCompound nbt, String name, Supplier<int[]> getter) {
			return nbt.getIntArray(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, int[] data) {
			buf.writeInt(data.length);
			for (int i : data) {
				buf.writeInt(i);
			}
		}
		
		@Override
		public int[] readFromByteBuf(ByteBuf buf, Supplier<int[]> getter) {
			int size = buf.readInt();
			int[] result = getter == null ? new int[size] : getter.get();
			for (int i = 0; i < size; ++i) {
				result[i] = buf.readInt();
			}
			return result;
		}
		
	}
	
	public static final class ByteArrayData implements IDataIO<byte[]> {
		
		@Override
		public void writeToData(IDataWriter writer, byte[] data) {
			writer.writeByteArray(data);
		}
		
		@Override
		public byte[] readFromData(IDataReader reader, Supplier<byte[]> getter) {
			return reader.readByteArray();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, byte[] data) {
			nbt.setByteArray(name, data);
		}
		
		@Override
		public byte[] readFromNBT(NBTTagCompound nbt, String name, Supplier<byte[]> getter) {
			return nbt.getByteArray(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, byte[] data) {
			buf.writeInt(data.length);
			for (byte b : data) {
				buf.writeByte(b);
			}
		}
		
		@Override
		public byte[] readFromByteBuf(ByteBuf buf, Supplier<byte[]> getter) {
			int size = buf.readInt();
			byte[] result = getter == null ? new byte[size] : getter.get();
			for (int i = 0; i < size; ++i) {
				result[i] = buf.readByte();
			}
			return result;
		}
		
	}
	
	public static final class StringData implements IDataIO<String> {
		
		@Override
		public void writeToData(IDataWriter writer, String data) {
			writer.writeString(data);
		}
		
		@Override
		public String readFromData(IDataReader reader, Supplier<String> getter) {
			return reader.readString();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, String data) {
			nbt.setString(name, data);
		}
		
		@Override
		public String readFromNBT(NBTTagCompound nbt, String name, Supplier<String> getter) {
			return nbt.getString(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, String data) {
			IOUtils.writeStringToBuf(buf, data);
		}
		
		@Override
		public String readFromByteBuf(ByteBuf buf, Supplier<String> getter) {
			return IOUtils.readStringFromBuf(buf);
		}
		
	}
	
	public static final class StringBuilderData implements IDataIO<StringBuilder> {
		
		@Override
		public void writeToData(IDataWriter writer, StringBuilder data) {
			int size = data.length();
			writer.writeVarint(size);
			for (int i = 0; i < size; ++i) {
				writer.writeChar(data.charAt(i));
			}
		}
		
		@Override
		public StringBuilder readFromData(IDataReader reader, Supplier<StringBuilder> getter) {
			int size = reader.readVarint();
			StringBuilder result = getter == null ? new StringBuilder(size) : getter.get();
			for (int i = 0; i < size; ++i) {
				result.append(reader.readChar());
			}
			return result;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, StringBuilder data) {
			nbt.setString(name, data.toString());
		}
		
		@Override
		public StringBuilder readFromNBT(NBTTagCompound nbt, String name, Supplier<StringBuilder> getter) {
			return new StringBuilder(nbt.getString(name));
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, StringBuilder data) {
			int size = data.length();
			buf.writeInt(size);
			for (int i = 0; i < size; ++i) {
				buf.writeChar(data.charAt(i));
			}
		}
		
		@Override
		public StringBuilder readFromByteBuf(ByteBuf buf, Supplier<StringBuilder> getter) {
			int size = buf.readInt();
			StringBuilder result = getter == null ? new StringBuilder(size) : getter.get();
			for (int i = 0; i < size; ++i) {
				result.append(buf.readChar());
			}
			return result;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(StringBuilder data, Class<R> target) {
			if (target == String.class) {
				return (R) data.toString();
			} else if (target == int.class || target == Integer.class) {
				return (R) Integer.valueOf(data.toString());
			} else {
				throw new ClassCastException("StringBuilder不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class StringBufferData implements IDataIO<StringBuffer> {
		
		@Override
		public void writeToData(IDataWriter writer, StringBuffer data) {
			int size = data.length();
			writer.writeVarint(size);
			for (int i = 0; i < size; ++i) {
				writer.writeChar(data.charAt(i));
			}
		}
		
		@Override
		public StringBuffer readFromData(IDataReader reader, Supplier<StringBuffer> getter) {
			int size = reader.readVarint();
			StringBuffer result = getter == null ? new StringBuffer(size) : getter.get();
			for (int i = 0; i < size; ++i) {
				result.append(reader.readChar());
			}
			return result;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, StringBuffer data) {
			nbt.setString(name, data.toString());
		}
		
		@Override
		public StringBuffer readFromNBT(NBTTagCompound nbt, String name, Supplier<StringBuffer> getter) {
			return new StringBuffer(nbt.getString(name));
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, StringBuffer data) {
			int size = data.length();
			buf.writeInt(size);
			for (int i = 0; i < size; ++i) {
				buf.writeChar(data.charAt(i));
			}
		}
		
		@Override
		public StringBuffer readFromByteBuf(ByteBuf buf, Supplier<StringBuffer> getter) {
			int size = buf.readInt();
			StringBuffer result = getter == null ? new StringBuffer(size) : getter.get();
			for (int i = 0; i < size; ++i) {
				result.append(buf.readChar());
			}
			return result;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(StringBuffer data, Class<R> target) {
			if (target == String.class) {
				return (R) data.toString();
			} else if (target == int.class || target == Integer.class) {
				return (R) Integer.valueOf(data.toString());
			} else {
				throw new ClassCastException("StringBuffer不能转换为：" + target.getName());
			}
		}
	}
	
	public static final class BytePackageArrayData implements IDataIO<Byte[]> {
		
		@Override
		public void writeToData(IDataWriter writer, Byte[] data) {
			byte[] newData = new byte[data.length];
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			writer.writeByteArray(newData);
		}
		
		@Override
		public Byte[] readFromData(IDataReader reader, Supplier<Byte[]> getter) {
			byte[] data = reader.readByteArray();
			Byte[] result = getter == null ? new Byte[data.length] : getter.get();
			for (int i = 0; i < result.length; i++) {
				result[i] = data[i];
			}
			return result;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Byte[] data) {
			byte[] newData = new byte[data.length];
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			nbt.setByteArray(name, newData);
		}
		
		@Override
		public Byte[] readFromNBT(NBTTagCompound nbt, String name, Supplier<Byte[]> getter) {
			byte[] data = nbt.getByteArray(name);
			Byte[] result = getter == null ? new Byte[data.length] : getter.get();
			for (int i = 0; i < data.length; i++) {
				result[i] = data[i];
			}
			return result;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Byte[] data) {
			byte[] newData = new byte[data.length];
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			buf.writeInt(newData.length);
			for (byte b : newData) {
				buf.writeByte(b);
			}
		}
		
		@Override
		public Byte[] readFromByteBuf(ByteBuf buf, Supplier<Byte[]> getter) {
			Byte[] result = getter == null ? new Byte[buf.readInt()] : getter.get();
			for (int i = 0; i < result.length; i++) {
				result[i] = buf.readByte();
			}
			return result;
		}
	}
	
	public static final class IntPackageArrayData implements IDataIO<Integer[]> {
		
		@Override
		public void writeToData(IDataWriter writer, Integer[] data) {
			int[] newData = Arrays.stream(data).mapToInt(Integer::valueOf).toArray();
			writer.writeVarintArray(newData);
		}
		
		@Override
		public Integer[] readFromData(IDataReader reader, Supplier<Integer[]> getter) {
			int[] data = reader.readVarintArray();
			return Arrays.stream(data).boxed().toArray(Integer[]::new);
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Integer[] data) {
			int[] newData = Arrays.stream(data).mapToInt(Integer::valueOf).toArray();
			nbt.setIntArray(name, newData);
		}
		
		@Override
		public Integer[] readFromNBT(NBTTagCompound nbt, String name, Supplier<Integer[]> getter) {
			int[] data = nbt.getIntArray(name);
			if (getter != null) {
				Integer[] result = getter.get();
				for (int i = 0; i < data.length; i++) {
					result[i] = data[i];
				}
				return result;
			}
			return Arrays.stream(data).boxed().toArray(Integer[]::new);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Integer[] data) {
			int[] newData = Arrays.stream(data).mapToInt(Integer::valueOf).toArray();
			DataTypeRegister.write(buf, newData);
		}
		
		@Override
		public Integer[] readFromByteBuf(ByteBuf buf, Supplier<Integer[]> getter) {
			int size = buf.readInt();
			Integer[] result = getter == null ? new Integer[size] : getter.get();
			for (int i = 0; i < size; ++i) {
				result[i] = buf.readInt();
			}
			return result;
		}
		
	}
	
	public static final class UuidData implements IDataIO<UUID> {
		
		@Override
		public void writeToData(IDataWriter writer, UUID data) {
			writer.writeUuid(data);
		}
		
		@Override
		public UUID readFromData(IDataReader reader, Supplier<UUID> getter) {
			return reader.readUuid();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, UUID data) {
			nbt.setUniqueId(name, data);
		}
		
		@Override
		public UUID readFromNBT(NBTTagCompound nbt, String name, Supplier<UUID> getter) {
			return nbt.getUniqueId(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, UUID data) {
			buf.writeLong(data.getMostSignificantBits());
			buf.writeLong(data.getLeastSignificantBits());
		}
		
		@Override
		public UUID readFromByteBuf(ByteBuf buf, Supplier<UUID> getter) {
			return new UUID(buf.readLong(), buf.readLong());
		}
		
	}
	
	public static final class NbtData implements IDataIO<NBTTagCompound> {
		
		@Override
		public void writeToData(IDataWriter writer, NBTTagCompound data) {
			writer.writeTag(data);
		}
		
		@Override
		public NBTTagCompound readFromData(IDataReader reader, Supplier<NBTTagCompound> getter) {
			return reader.readTagCompound();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, NBTTagCompound data) {
			nbt.setTag(name, data);
		}
		
		@Override
		public NBTTagCompound readFromNBT(NBTTagCompound nbt, String name, Supplier<NBTTagCompound> getter) {
			return nbt.getCompoundTag(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, NBTTagCompound data) {
			ByteBufUtils.writeTag(buf, data);
		}
		
		@Override
		public NBTTagCompound readFromByteBuf(ByteBuf buf, Supplier<NBTTagCompound> getter) {
			return ByteBufUtils.readTag(buf);
		}
		
	}
	
	public static final class EnumData implements IDataIO<Enum<?>> {
		
		@Override
		public void writeToData(IDataWriter writer, Enum<?> data) {
			writer.writeString(data.name());
			writer.writeString(data.getClass().getName());
		}
		
		@Override
		public Enum<?> readFromData(IDataReader reader, Supplier<Enum<?>> getter) {
			String name = reader.readString();
			String clazz = reader.readString();
			try {
				//noinspection unchecked,rawtypes
				return Enum.valueOf((Class) Class.forName(clazz), name);
			} catch (ClassNotFoundException e) {
				throw new IntransitException(e);
			}
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Enum<?> data) {
			nbt.setString(name, data.name());
			nbt.setString(name + "k", data.getClass().getName());
		}
		
		@Override
		public Enum<?> readFromNBT(NBTTagCompound nbt, String name, Supplier<Enum<?>> getter) {
			try {
				//noinspection unchecked,rawtypes
				return Enum.valueOf(
						(Class) Class.forName(nbt.getString(name + "k")), nbt.getString(name));
			} catch (ClassNotFoundException e) {
				throw new IntransitException(e);
			}
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Enum<?> data) {
			IOUtils.writeStringToBuf(buf, data.name());
			IOUtils.writeStringToBuf(buf, data.getClass().getName());
		}
		
		@Override
		public Enum<?> readFromByteBuf(ByteBuf buf, Supplier<Enum<?>> getter) {
			String name = IOUtils.readStringFromBuf(buf);
			String clazz = IOUtils.readStringFromBuf(buf);
			try {
				//noinspection unchecked,rawtypes
				return Enum.valueOf((Class) Class.forName(clazz), name);
			} catch (ClassNotFoundException e) {
				throw new IntransitException(e);
			}
		}
		
	}
	
	public static final class SerializableData implements IDataIO<INBTSerializable<NBTTagCompound>> {
		
		@Override
		public void writeToData(IDataWriter writer, INBTSerializable<NBTTagCompound> data) {
			NBTTagCompound tag = data.serializeNBT();
			writer.writeTag(tag);
		}
		
		@Override
		public INBTSerializable<NBTTagCompound> readFromData(
				IDataReader reader, Supplier<INBTSerializable<NBTTagCompound>> getter) {
			NBTTagCompound tag = reader.readTagCompound();
			INBTSerializable<NBTTagCompound> result = getter.get();
			result.deserializeNBT(tag);
			return result;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, INBTSerializable<NBTTagCompound> data) {
			nbt.setTag(name, data.serializeNBT());
		}
		
		@Override
		public INBTSerializable<NBTTagCompound> readFromNBT(
				NBTTagCompound nbt, String name, Supplier<INBTSerializable<NBTTagCompound>> getter) {
			INBTSerializable<NBTTagCompound> result = getter.get();
			result.deserializeNBT(nbt.getCompoundTag(name));
			return result;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, INBTSerializable<NBTTagCompound> data) {
			NBTTagCompound tag = data.serializeNBT();
			ByteBufUtils.writeTag(buf, tag);
		}
		
		@Override
		public INBTSerializable<NBTTagCompound> readFromByteBuf(
				ByteBuf buf, Supplier<INBTSerializable<NBTTagCompound>> getter) {
			NBTTagCompound tag = ByteBufUtils.readTag(buf);
			INBTSerializable<NBTTagCompound> result = getter.get();
			result.deserializeNBT(tag);
			return result;
		}
		
		@Override
		public <R> R cast(INBTSerializable<NBTTagCompound> data, Class<R> target) {
			return target.cast(data);
		}
		
	}
	
	public static final class PosData implements IDataIO<BlockPos> {
		
		@Override
		public void writeToData(IDataWriter writer, BlockPos data) {
			writer.writeBlockPos(data);
		}
		
		@Override
		public BlockPos readFromData(IDataReader reader, Supplier<BlockPos> getter) {
			return reader.readBlockPos();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, BlockPos data) {
			IOUtils.writeBlockPos(nbt, data, name);
		}
		
		@Override
		public BlockPos readFromNBT(NBTTagCompound nbt, String name, Supplier<BlockPos> getter) {
			return IOUtils.readBlockPos(nbt, name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, BlockPos data) {
			buf.writeInt(data.getX());
			buf.writeInt(data.getY());
			buf.writeInt(data.getZ());
		}
		
		@Override
		public BlockPos readFromByteBuf(ByteBuf buf, Supplier<BlockPos> getter) {
			return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		}
		
	}
	
	public static final class VoltageData implements IDataIO<IVoltage> {
		
		@Override
		public void writeToData(IDataWriter writer, IVoltage data) {
			writer.writeVoltage(data);
		}
		
		@Override
		public IVoltage readFromData(IDataReader reader, Supplier<IVoltage> getter) {
			return reader.readVoltage();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, IVoltage data) {
			nbt.setInteger(name + "v", data.getVoltage());
			nbt.setDouble(name, data.getLossIndex());
		}
		
		@Override
		public IVoltage readFromNBT(NBTTagCompound nbt, String name, Supplier<IVoltage> getter) {
			int voltage = nbt.getInteger(name + "v");
			double loss = nbt.getDouble(name);
			return IVoltage.getInstance(voltage, loss);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, IVoltage data) {
			buf.writeInt(data.getVoltage());
			buf.writeDouble(data.getLossIndex());
		}
		
		@Override
		public IVoltage readFromByteBuf(ByteBuf buf, Supplier<IVoltage> getter) {
			return IVoltage.getInstance(buf.readInt(), buf.readDouble());
		}
		
	}
	
	public static final class ClassData implements IDataIO<Class<?>> {
		
		@Override
		public void writeToData(IDataWriter writer, Class<?> data) {
			writer.writeString(data.getName());
		}
		
		@Override
		public Class<?> readFromData(IDataReader reader, Supplier<Class<?>> getter) {
			try {
				return Class.forName(reader.readString());
			} catch (ClassNotFoundException e) {
				throw new IntransitException("需要读写的类不存在", e);
			}
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Class<?> data) {
			nbt.setString(name, data.getName());
		}
		
		@Override
		public Class<?> readFromNBT(NBTTagCompound nbt, String name, Supplier<Class<?>> getter) {
			if (getter != null) return getter.get();
			try {
				return Class.forName(nbt.getString(name));
			} catch (ClassNotFoundException e) {
				throw new IntransitException("需要读写的类不存在", e);
			}
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Class<?> data) {
			IOUtils.writeStringToBuf(buf, data.getName());
		}
		
		@Override
		public Class<?> readFromByteBuf(ByteBuf buf, Supplier<Class<?>> getter) {
			if (getter != null) return getter.get();
			try {
				return Class.forName(IOUtils.readStringFromBuf(buf));
			} catch (ClassNotFoundException e) {
				throw new IntransitException("需要读写的类不存在", e);
			}
		}
		
	}
	
	public static final class ElementData implements IDataIO<ItemElement> {
		
		@Override
		public void writeToData(IDataWriter writer, ItemElement data) {
			data.writeToData(writer);
		}
		
		@Override
		public ItemElement readFromData(IDataReader reader, Supplier<ItemElement> getter) {
			return ItemElement.instance(reader);
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, ItemElement data) {
			nbt.setTag(name, data.serializeNBT());
		}
		
		@Override
		public ItemElement readFromNBT(NBTTagCompound nbt, String name, Supplier<ItemElement> getter) {
			return ItemElement.instance(nbt);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, ItemElement data) {
			data.writeToBuf(buf);
		}
		
		@Override
		public ItemElement readFromByteBuf(ByteBuf buf, Supplier<ItemElement> getter) {
			return ItemElement.instance(buf);
		}
		
	}
	
	public static final class CollectionData implements IDataIO<Collection<?>> {
		
		@Override
		public void writeToData(IDataWriter writer, Collection<?> data) {
			writer.writeVarint(data.size());
			for (Object o : data) {
				DataTypeRegister.write(writer, o.getClass());
				DataTypeRegister.write(writer, o);
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Collection<?> readFromData(IDataReader reader, Supplier<Collection<?>> getter) {
			int size = reader.readVarint();
			Collection collection = getter == null ? null : getter.get();
			if (collection == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class clazz = DataTypeRegister.read(reader, Class.class, null);
				collection.add(DataTypeRegister.read(reader, clazz, null));
			}
			return collection;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Collection<?> data) {
			nbt.setInteger(name, data.size());
			int index = 0;
			for (Object o : data) {
				String str = name + index;
				DataTypeRegister.write(nbt, str + "name", o.getClass());
				DataTypeRegister.write(nbt, str, o);
				++index;
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Collection<?> readFromNBT(NBTTagCompound nbt, String name, Supplier<Collection<?>> getter) {
			int size = nbt.getInteger(name);
			Collection collection = getter == null ? null : getter.get();
			if (collection == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				String str = name + i;
				Class clazz = DataTypeRegister.read(nbt, str + "name", Class.class, null);
				collection.add(DataTypeRegister.read(nbt, str, clazz, null));
			}
			return collection;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Collection<?> data) {
			buf.writeInt(data.size());
			for (Object o : data) {
				DataTypeRegister.write(buf, o.getClass());
				DataTypeRegister.write(buf, o);
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Collection<?> readFromByteBuf(ByteBuf buf, Supplier<Collection<?>> getter) {
			int size = buf.readInt();
			Collection collection = getter == null ? null : getter.get();
			if (collection == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class clazz = DataTypeRegister.read(buf, Class.class, null);
				collection.add(DataTypeRegister.read(buf, clazz, null));
			}
			return collection;
		}
		
	}
	
	public static final class MapData implements IDataIO<Map<?, ?>> {
		
		@Override
		public void writeToData(IDataWriter writer, Map<?, ?> data) {
			if (data == null) return;
			writer.writeVarint(data.size());
			for (Map.Entry<?, ?> entry : data.entrySet()) {
				DataTypeRegister.write(writer, entry.getKey().getClass());
				DataTypeRegister.write(writer, entry.getValue().getClass());
				DataTypeRegister.write(writer, entry.getKey());
				DataTypeRegister.write(writer, entry.getValue());
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Map<?, ?> readFromData(IDataReader reader, Supplier<Map<?, ?>> getter) {
			int size = reader.readVarint();
			Map map = getter == null ? null : getter.get();
			if (map == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class<?> keyClazz = DataTypeRegister.read(reader, Class.class, null);
				Class<?> valueClazz = DataTypeRegister.read(reader, Class.class, null);
				Object key = DataTypeRegister.read(reader, keyClazz, null);
				Object value = DataTypeRegister.read(reader, valueClazz, null);
				map.put(key, value);
			}
			return map;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Map<?, ?> data) {
			if (data == null) return;
			nbt.setInteger(name, data.size());
			int k = 0;
			for (Map.Entry<?, ?> entry : data.entrySet()) {
				String str = name + k++;
				DataTypeRegister.write(nbt, str + "key", entry.getKey());
				DataTypeRegister.write(nbt, str + "value", entry.getValue());
				DataTypeRegister.write(nbt, str + "kn", entry.getKey().getClass());
				DataTypeRegister.write(nbt, str + "vn", entry.getValue().getClass());
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Map<?, ?> readFromNBT(NBTTagCompound nbt, String name, Supplier<Map<?, ?>> getter) {
			int size = nbt.getInteger(name);
			Map map = getter == null ? null : getter.get();
			if (map == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				String str = name + i;
				Class<?> keyClazz = DataTypeRegister.read(nbt, str + "kn", Class.class, null);
				Class<?> valueClazz = DataTypeRegister.read(nbt, str + "vn", Class.class, null);
				Object key = DataTypeRegister.read(nbt, str + "key", keyClazz, null);
				Object value = DataTypeRegister.read(nbt, str + "key", valueClazz, null);
				map.put(key, value);
			}
			return map;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Map<?, ?> data) {
			if (data == null) return;
			buf.writeInt(data.size());
			for (Map.Entry<?, ?> entry : data.entrySet()) {
				DataTypeRegister.write(buf, entry.getKey().getClass());
				DataTypeRegister.write(buf, entry.getValue().getClass());
				DataTypeRegister.write(buf, entry.getKey());
				DataTypeRegister.write(buf, entry.getValue());
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Map<?, ?> readFromByteBuf(ByteBuf buf, Supplier<Map<?, ?>> getter) {
			int size = buf.readInt();
			Map map = getter == null ? null : getter.get();
			if (map == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class<?> keyClazz = DataTypeRegister.read(buf, Class.class, null);
				Class<?> valueClazz = DataTypeRegister.read(buf, Class.class, null);
				Object key = DataTypeRegister.read(buf, keyClazz, null);
				Object value = DataTypeRegister.read(buf, valueClazz, null);
				map.put(key, value);
			}
			return map;
		}
		
	}
	
}