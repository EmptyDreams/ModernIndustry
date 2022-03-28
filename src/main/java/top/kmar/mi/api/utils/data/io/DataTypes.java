package top.kmar.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.dor.ByteDataOperator;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.dor.interfaces.IDorSerialize;
import top.kmar.mi.api.electricity.interfaces.IVoltage;
import top.kmar.mi.api.exception.TransferException;
import top.kmar.mi.api.fluid.data.FluidData;
import top.kmar.mi.api.register.others.AutoLoader;
import top.kmar.mi.api.utils.ExpandFunctionKt;
import top.kmar.mi.api.utils.container.Wrapper;
import top.kmar.mi.coremod.other.ICapManagerCheck;
import top.kmar.mi.coremod.other.ICapStorageType;
import top.kmar.mi.data.info.EnumVoltage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static top.kmar.mi.api.utils.ExpandFunctionKt.readString;
import static top.kmar.mi.api.utils.ExpandFunctionKt.writeString;

/**
 * @author EmptyDreams
 */
@AutoLoader
public final class DataTypes {
	
	static {
	
	}
	
	public static final class DorSerializeData implements IDataIO<IDorSerialize> {
		
		@Override
		public boolean match(@Nonnull Class<?> objType, @Nullable Class<?> fieldType) {
			return IDorSerialize.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, IDorSerialize data) {
			writer.writeData(data.serializeDor());
		}
		
		@Override
		public IDorSerialize readFromData(IDataReader reader, Class<?> fieldType, Supplier<IDorSerialize> getter) {
			IDorSerialize result = getter.get();
			result.deserializedDor(reader.readData());
			return result;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, IDorSerialize data) {
			data.serializeDor().readToNBT(nbt, name);
		}
		
		@Override
		public IDorSerialize readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<IDorSerialize> getter) {
			ByteDataOperator operator = new ByteDataOperator();
			operator.writeFromNBT(nbt, name);
			IDorSerialize result = getter.get();
			result.deserializedDor(operator);
			return result;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, IDorSerialize data) {
			data.serializeDor().readToByteBuf(buf);
		}
		
		@Override
		public IDorSerialize readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<IDorSerialize> getter) {
			ByteDataOperator operator = new ByteDataOperator();
			operator.writeFromByteBuf(buf);
			IDorSerialize result = getter.get();
			result.deserializedDor(operator);
			return result;
		}
	}
	
	public static final class IntData implements IDataIO<Integer> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == int.class || objType == Integer.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Integer data) {
			writer.writeInt(data);
		}
		
		@Override
		public Integer readFromData(IDataReader reader, Class<?> fieldType, Supplier<Integer> getter) {
			return reader.readInt();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Integer data) {
			nbt.setInteger(name, data);
		}
		
		@Override
		public Integer readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Integer> getter) {
			return nbt.getInteger(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Integer data) {
			buf.writeInt(data);
		}
		
		@Override
		public Integer readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Integer> getter) {
			return buf.readInt();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Integer data, Class<R> target) {
			if (target == byte.class || target == Byte.class)
				return (R) Byte.valueOf(data.byteValue());
			if (target == short.class || target == Short.class)
				return (R) Short.valueOf(data.shortValue());
			if (target == long.class || target == Long.class)
				return (R) Long.valueOf(data.longValue());
			if (target == float.class || target == Float.class)
				return (R) Float.valueOf(data.floatValue());
			if (target == double.class || target == Double.class)
				return (R) Double.valueOf(data.doubleValue());
			throw new ClassCastException("int不能转换为：" + target.getName());
		}
	}
	
	public static final class ByteData implements IDataIO<Byte> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == byte.class || objType == Byte.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Byte data) {
			writer.writeByte(data);
		}
		
		@Override
		public Byte readFromData(IDataReader reader, Class<?> fieldType, Supplier<Byte> getter) {
			return reader.readByte();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Byte data) {
			nbt.setByte(name, data);
		}
		
		@Override
		public Byte readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Byte> getter) {
			return nbt.getByte(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Byte data) {
			buf.writeByte(data);
		}
		
		@Override
		public Byte readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Byte> getter) {
			return buf.readByte();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Byte data, Class<R> target) {
			if (target == int.class || target == Integer.class)
				return (R) Integer.valueOf(data.intValue());
			if (target == short.class || target == Short.class)
				return (R) Short.valueOf(data.shortValue());
			if (target == long.class || target == Long.class)
				return (R) Long.valueOf(data.longValue());
			if (target == float.class || target == Float.class)
				return (R) Float.valueOf(data.floatValue());
			if (target == double.class || target == Double.class)
				return (R) Double.valueOf(data.doubleValue());
			throw new ClassCastException("byte不能转换为：" + target.getName());
		}
	}
	
	public static final class BooleanData implements IDataIO<Boolean> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == boolean.class || objType == Boolean.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Boolean data) {
			writer.writeBoolean(data);
		}
		
		@Override
		public Boolean readFromData(IDataReader reader, Class<?> fieldType, Supplier<Boolean> getter) {
			return reader.readBoolean();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Boolean data) {
			nbt.setBoolean(name, data);
		}
		
		@Override
		public Boolean readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Boolean> getter) {
			return nbt.getBoolean(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Boolean data) {
			buf.writeBoolean(data);
		}
		
		@Override
		public Boolean readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Boolean> getter) {
			return buf.readBoolean();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Boolean data, Class<R> target) {
			if (target == byte.class || target == Byte.class)
				return (R) (data ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0));
			if (target == short.class || target == Short.class)
				return (R) (data ? Short.valueOf((short) 1) : Short.valueOf((short) 0));
			if (target == int.class || target == Integer.class)
				return (R) (data ? Integer.valueOf(1) : Integer.valueOf(0));
			if (target == long.class || target == Long.class)
				return (R) (data ? Long.valueOf(1) : Long.valueOf(0));
			if (target == float.class || target == Float.class)
				return (R) (data ? Float.valueOf(1) : Float.valueOf(0));
			if (target == double.class || target == Double.class)
				return (R) (data ? Double.valueOf(1) : Double.valueOf(0));
			if (target == String.class)
				return (R) data.toString();
			throw new ClassCastException("boolean不能转换为：" + target.getName());
		}
	}
	
	public static final class LongData implements IDataIO<Long> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == long.class || objType == Long.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Long data) {
			writer.writeLong(data);
		}
		
		@Override
		public Long readFromData(IDataReader reader, Class<?> fieldType, Supplier<Long> getter) {
			return reader.readLong();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Long data) {
			nbt.setLong(name, data);
		}
		
		@Override
		public Long readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Long> getter) {
			return nbt.getLong(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Long data) {
			buf.writeLong(data);
		}
		
		@Override
		public Long readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Long> getter) {
			return buf.readLong();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Long data, Class<R> target) {
			if (target == byte.class || target == Byte.class)
				return (R) Byte.valueOf(data.byteValue());
			if (target == short.class || target == Short.class)
				return (R) Short.valueOf(data.shortValue());
			if (target == int.class || target == Integer.class)
				return (R) Integer.valueOf(data.intValue());
			if (target == float.class || target == Float.class)
				return (R) Float.valueOf(data.floatValue());
			if (target == double.class || target == Double.class)
				return (R) Double.valueOf(data.doubleValue());
			throw new ClassCastException("long不能转换为：" + target.getName());
		}
	}
	
	public static final class DoubleData implements IDataIO<Double> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == double.class || objType == Double.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Double data) {
			writer.writeDouble(data);
		}
		
		@Override
		public Double readFromData(IDataReader reader, Class<?> fieldType, Supplier<Double> getter) {
			return reader.readDouble();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Double data) {
			nbt.setDouble(name, data);
		}
		
		@Override
		public Double readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Double> getter) {
			return nbt.getDouble(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Double data) {
			buf.writeDouble(data);
		}
		
		@Override
		public Double readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Double> getter) {
			return buf.readDouble();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Double data, Class<R> target) {
			if (target == byte.class || target == Byte.class)
				return (R) Byte.valueOf(data.byteValue());
			if (target == short.class || target == Short.class)
				return (R) Short.valueOf(data.shortValue());
			if (target == long.class || target == Long.class)
				return (R) Long.valueOf(data.longValue());
			if (target == float.class || target == Float.class)
				return (R) Float.valueOf(data.floatValue());
			if (target == int.class || target == Integer.class)
				return (R) Integer.valueOf(data.intValue());
			throw new ClassCastException("double不能转换为：" + target.getName());
		}
	}
	
	public static final class ShortData implements IDataIO<Short> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == short.class || objType == Short.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Short data) {
			writer.writeShort(data);
		}
		
		@Override
		public Short readFromData(IDataReader reader, Class<?> fieldType, Supplier<Short> getter) {
			return reader.readShort();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Short data) {
			nbt.setShort(name, data);
		}
		
		@Override
		public Short readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Short> getter) {
			return nbt.getShort(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Short data) {
			buf.writeShort(data);
		}
		
		@Override
		public Short readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Short> getter) {
			return buf.readShort();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Short data, Class<R> target) {
			if (target == byte.class || target == Byte.class)
				return (R) Byte.valueOf(data.byteValue());
			if (target == int.class || target == Integer.class)
				return (R) Integer.valueOf(data.intValue());
			if (target == long.class || target == Long.class)
				return (R) Long.valueOf(data.longValue());
			if (target == float.class || target == Float.class)
				return (R) Float.valueOf(data.floatValue());
			if (target == double.class || target == Double.class)
				return (R) Double.valueOf(data.doubleValue());
			throw new ClassCastException("short不能转换为：" + target.getName());
		}
	}
	
	public static final class FloatData implements IDataIO<Float> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == float.class || objType == Float.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Float data) {
			writer.writeFloat(data);
		}
		
		@Override
		public Float readFromData(IDataReader reader, Class<?> fieldType, Supplier<Float> getter) {
			return reader.readFloat();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Float data) {
			nbt.setFloat(name, data);
		}
		
		@Override
		public Float readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Float> getter) {
			return nbt.getFloat(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Float data) {
			buf.writeFloat(data);
		}
		
		@Override
		public Float readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Float> getter) {
			return buf.readFloat();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Float data, Class<R> target) {
			if (target == byte.class || target == Byte.class)
				return (R) Byte.valueOf(data.byteValue());
			if (target == short.class || target == Short.class)
				return (R) Short.valueOf(data.shortValue());
			if (target == long.class || target == Long.class)
				return (R) Long.valueOf(data.longValue());
			if (target == int.class || target == Integer.class)
				return (R) Integer.valueOf(data.intValue());
			if (target == double.class || target == Double.class)
				return (R) Double.valueOf(data.doubleValue());
			throw new ClassCastException("float不能转换为：" + target.getName());
		}
	}
	
	public static final class IntArrayData implements IDataIO<int[]> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == int[].class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, int[] data) {
			writer.writeIntArray(data);
		}
		
		@Override
		public int[] readFromData(IDataReader reader, Class<?> fieldType, Supplier<int[]> getter) {
			return reader.readIntArray();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, int[] data) {
			nbt.setIntArray(name, data);
		}
		
		@Override
		public int[] readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<int[]> getter) {
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
		public int[] readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<int[]> getter) {
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
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == byte[].class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, byte[] data) {
			writer.writeByteArray(data);
		}
		
		@Override
		public byte[] readFromData(IDataReader reader, Class<?> fieldType, Supplier<byte[]> getter) {
			return reader.readByteArray();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, byte[] data) {
			nbt.setByteArray(name, data);
		}
		
		@Override
		public byte[] readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<byte[]> getter) {
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
		public byte[] readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<byte[]> getter) {
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
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == String.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, String data) {
			writer.writeString(data);
		}
		
		@Override
		public String readFromData(IDataReader reader, Class<?> fieldType, Supplier<String> getter) {
			return reader.readString();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, String data) {
			nbt.setString(name, data);
		}
		
		@Override
		public String readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<String> getter) {
			return nbt.getString(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, String data) {
			ExpandFunctionKt.writeString(buf, data);
		}
		
		@Override
		public String readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<String> getter) {
			return readString(buf);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(String data, Class<R> target) {
			if (target == byte.class || target == Byte.class)
				return (R) Byte.valueOf(data);
			if (target == short.class || target == Short.class)
				return (R) Short.valueOf(data);
			if (target == int.class || target == Integer.class)
				return (R) Integer.valueOf(data);
			if (target == long.class || target == Long.class)
				return (R) Long.valueOf(data);
			if (target == float.class || target == Float.class)
				return (R) Float.valueOf(data);
			if (target == double.class || target == Double.class)
				return (R) Double.valueOf(data);
			if (target == boolean.class || target == Boolean.class)
				return (R) Boolean.valueOf(data);
			throw new ClassCastException("String不能转换为：" + target.getName());
		}
		
	}
	
	public static final class StringBuilderData implements IDataIO<StringBuilder> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return StringBuilder.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, StringBuilder data) {
			int size = data.length();
			writer.writeVarInt(size);
			for (int i = 0; i < size; ++i) {
				writer.writeChar(data.charAt(i));
			}
		}
		
		@Override
		public StringBuilder readFromData(IDataReader reader, Class<?> fieldType, Supplier<StringBuilder> getter) {
			int size = reader.readVarInt();
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
		public StringBuilder readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<StringBuilder> getter) {
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
		public StringBuilder readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<StringBuilder> getter) {
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
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return StringBuffer.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, StringBuffer data) {
			int size = data.length();
			writer.writeVarInt(size);
			for (int i = 0; i < size; ++i) {
				writer.writeChar(data.charAt(i));
			}
		}
		
		@Override
		public StringBuffer readFromData(IDataReader reader, Class<?> fieldType, Supplier<StringBuffer> getter) {
			int size = reader.readVarInt();
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
		public StringBuffer readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<StringBuffer> getter) {
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
		public StringBuffer readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<StringBuffer> getter) {
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
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == Byte[].class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Byte[] data) {
			byte[] newData = new byte[data.length];
			for (int i = 0; i < data.length; i++) {
				newData[i] = data[i];
			}
			writer.writeByteArray(newData);
		}
		
		@Override
		public Byte[] readFromData(IDataReader reader, Class<?> fieldType, Supplier<Byte[]> getter) {
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
		public Byte[] readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Byte[]> getter) {
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
		public Byte[] readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Byte[]> getter) {
			Byte[] result = getter == null ? new Byte[buf.readInt()] : getter.get();
			for (int i = 0; i < result.length; i++) {
				result[i] = buf.readByte();
			}
			return result;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Byte[] data, Class<R> target) {
			if (target == byte[].class) {
				byte[] result = new byte[data.length];
				for (int i = 0; i < data.length; i++) {
					result[i] = data[i];
				}
				return (R) result;
			}
			if (target == String.class)
				return (R) Arrays.toString(data);
			throw new ClassCastException("Byte[]不能转换为：" + target.getName());
		}
	}
	
	public static final class IntPackageArrayData implements IDataIO<Integer[]> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == Integer[].class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Integer[] data) {
			int[] newData = Arrays.stream(data).mapToInt(Integer::valueOf).toArray();
			writer.writeVarIntArray(newData);
		}
		
		@Override
		public Integer[] readFromData(IDataReader reader, Class<?> fieldType, Supplier<Integer[]> getter) {
			int[] data = reader.readVarIntArray();
			return Arrays.stream(data).boxed().toArray(Integer[]::new);
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Integer[] data) {
			int[] newData = Arrays.stream(data).mapToInt(Integer::valueOf).toArray();
			nbt.setIntArray(name, newData);
		}
		
		@Override
		public Integer[] readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Integer[]> getter) {
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
			DataSerialize.write(buf, newData, int[].class);
		}
		
		@Override
		public Integer[] readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Integer[]> getter) {
			int size = buf.readInt();
			Integer[] result = getter == null ? new Integer[size] : getter.get();
			for (int i = 0; i < size; ++i) {
				result[i] = buf.readInt();
			}
			return result;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(Integer[] data, Class<R> target) {
			if (target == int[].class)
				return (R) Arrays.stream(data).mapToInt(it -> it).toArray();
			if (target == String.class)
				return (R) Arrays.toString(data);
			throw new ClassCastException("Integer[]不能转换为：" + target.getName());
		}
	}
	
	public static final class UuidData implements IDataIO<UUID> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == UUID.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, UUID data) {
			writer.writeUuid(data);
		}
		
		@Override
		public UUID readFromData(IDataReader reader, Class<?> fieldType, Supplier<UUID> getter) {
			return reader.readUuid();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, UUID data) {
			nbt.setUniqueId(name, data);
		}
		
		@Override
		public UUID readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<UUID> getter) {
			return nbt.getUniqueId(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, UUID data) {
			buf.writeLong(data.getMostSignificantBits());
			buf.writeLong(data.getLeastSignificantBits());
		}
		
		@Override
		public UUID readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<UUID> getter) {
			return new UUID(buf.readLong(), buf.readLong());
		}
		
	}
	
	public static final class NbtData implements IDataIO<NBTTagCompound> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == NBTTagCompound.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, NBTTagCompound data) {
			writer.writeTag(data);
		}
		
		@Override
		public NBTTagCompound readFromData(IDataReader reader, Class<?> fieldType, Supplier<NBTTagCompound> getter) {
			return reader.readTagCompound();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, NBTTagCompound data) {
			nbt.setTag(name, data);
		}
		
		@Override
		public NBTTagCompound readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<NBTTagCompound> getter) {
			return nbt.getCompoundTag(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, NBTTagCompound data) {
			ByteBufUtils.writeTag(buf, data);
		}
		
		@Override
		public NBTTagCompound readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<NBTTagCompound> getter) {
			return ByteBufUtils.readTag(buf);
		}
		
	}
	
	public static final class EnumData implements IDataIO<Enum<?>> {
		
		@Override
		public boolean match(@Nonnull Class<?> objType, @Nullable Class<?> fieldType) {
			if (fieldType == null) return false;
			return (objType == fieldType || Enum.class.isAssignableFrom(fieldType))
					&& Enum.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, Enum<?> data) {
			writer.writeVarInt(data.ordinal());
		}
		
		@Override
		public Enum<?> readFromData(IDataReader reader, Class<?> fieldType, Supplier<Enum<?>> getter) {
			try {
				int index = reader.readVarInt();
				return ((Enum<?>[]) fieldType.getMethod("values").invoke(null ))[index];
			} catch (Exception e) {
				throw TransferException.instance("读取Enum时出现错误", e);
			}
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Enum<?> data) {
			nbt.setInteger(name, data.ordinal());
		}
		
		@Override
		public Enum<?> readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Enum<?>> getter) {
			try {
				return ((Enum<?>[]) fieldType.getMethod("values").invoke(null ))[nbt.getInteger(name)];
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				throw TransferException.instance("读取Enum时出现错误", e);
			}
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Enum<?> data) {
			buf.writeInt(data.ordinal());
		}
		
		@Override
		public Enum<?> readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Enum<?>> getter) {
			try {
				return ((Enum<?>[]) fieldType.getMethod("values").invoke(null ))[buf.readInt()];
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				throw TransferException.instance("读取Enum时出现错误", e);
			}
		}
		
	}
	
	public static final class FluidDataData implements IDataIO<FluidData> {
		
		@Override
		public boolean match(@Nonnull Class<?> objType, @Nullable Class<?> fieldType) {
			return FluidData.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, FluidData data) {
			writer.writeBoolean(data.isAir());
			if (!data.isAir()) //noinspection ConstantConditions
				writer.writeString(data.getFluid().getName());
			writer.writeVarInt(data.getAmount());
		}
		
		@Override
		public FluidData readFromData(IDataReader reader, Class<?> fieldType, Supplier<FluidData> getter) {
			boolean isAir = reader.readBoolean();
			Fluid fluid = isAir ? null : FluidRegistry.getFluid(reader.readString());
			int amount = reader.readVarInt();
			return new FluidData(fluid, amount);
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, FluidData data) {
			if (!data.isAir()) //noinspection ConstantConditions
				nbt.setString(name + 'f', data.getFluid().getName());
			nbt.setInteger(name + 'a', data.getAmount());
		}
		
		@Override
		public FluidData readFromNBT(NBTTagCompound nbt,
		                             String name, Class<?> fieldType, Supplier<FluidData> getter) {
			String fluidName = nbt.getString(name + 'f');
			Fluid fluid = fluidName.length() == 0 ? null : FluidRegistry.getFluid(fluidName);
			int amount = nbt.getInteger(name + 'a');
			return new FluidData(fluid, amount);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, FluidData data) {
			buf.writeBoolean(data.isAir());
			if (!data.isAir()) //noinspection ConstantConditions
				DataSerialize.write(buf, data.getFluid().getName());
			buf.writeInt(data.getAmount());
		}
		
		@Override
		public FluidData readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<FluidData> getter) {
			boolean isAir = buf.readBoolean();
			Fluid fluid = isAir ? null :
					FluidRegistry.getFluid(DataSerialize.read(buf, String.class, null));
			int amount = buf.readInt();
			return new FluidData(fluid, amount);
		}
	}
	
	public static final class AllEnumData implements IDataIO<Enum<?>> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return !Enum.class.isAssignableFrom(fieldType) && Enum.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, Enum<?> data) {
			writer.writeString(data.name());
			writer.writeString(data.getClass().getName());
		}
		
		@Override
		public Enum<?> readFromData(IDataReader reader, Class<?> fieldType, Supplier<Enum<?>> getter) {
			String name = reader.readString();
			String clazz = reader.readString();
			try {
				//noinspection unchecked,rawtypes
				return Enum.valueOf((Class) Class.forName(clazz), name);
			} catch (ClassNotFoundException e) {
				throw TransferException.instance(e);
			}
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Enum<?> data) {
			nbt.setString(name, data.name());
			nbt.setString(name + "k", data.getClass().getName());
		}
		
		@Override
		public Enum<?> readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Enum<?>> getter) {
			try {
				//noinspection unchecked,rawtypes
				return Enum.valueOf(
						(Class) Class.forName(nbt.getString(name + "k")), nbt.getString(name));
			} catch (ClassNotFoundException e) {
				throw TransferException.instance(e);
			}
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Enum<?> data) {
			writeString(buf, data.name());
			writeString(buf, data.getClass().getName());
		}
		
		@Override
		public Enum<?> readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Enum<?>> getter) {
			String name = readString(buf);
			String clazz = readString(buf);
			try {
				//noinspection unchecked,rawtypes
				return Enum.valueOf((Class) Class.forName(clazz), name);
			} catch (ClassNotFoundException e) {
				throw TransferException.instance(e);
			}
		}
		
	}
	
	public static final class SerializableData implements IDataIO<INBTSerializable<NBTTagCompound>> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return INBTSerializable.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, INBTSerializable<NBTTagCompound> data) {
			NBTTagCompound tag = data.serializeNBT();
			writer.writeTag(tag);
		}
		
		@Override
		public INBTSerializable<NBTTagCompound> readFromData(IDataReader reader,
		                                 Class<?> fieldType, Supplier<INBTSerializable<NBTTagCompound>> getter) {
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
		public INBTSerializable<NBTTagCompound> readFromNBT(NBTTagCompound nbt, String name,
		                                 Class<?> fieldType, Supplier<INBTSerializable<NBTTagCompound>> getter) {
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
		public INBTSerializable<NBTTagCompound> readFromByteBuf(ByteBuf buf, Class<?> fieldType,
		                                                Supplier<INBTSerializable<NBTTagCompound>> getter) {
			NBTTagCompound tag = ByteBufUtils.readTag(buf);
			INBTSerializable<NBTTagCompound> result = getter.get();
			result.deserializeNBT(tag);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <R> R cast(INBTSerializable<NBTTagCompound> data, Class<R> target) {
			if (target == String.class) return (R) data.toString();
			return target.cast(data);
		}
		
	}
	
	public static final class PosData implements IDataIO<BlockPos> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return BlockPos.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, BlockPos data) {
			writer.writeBlockPos(data);
		}
		
		@Override
		public BlockPos readFromData(IDataReader reader, Class<?> fieldType, Supplier<BlockPos> getter) {
			return reader.readBlockPos();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, BlockPos data) {
			ExpandFunctionKt.setBlockPos(nbt, name, data);
		}
		
		@Override
		public BlockPos readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<BlockPos> getter) {
			return ExpandFunctionKt.getBlockPos(nbt, name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, BlockPos data) {
			buf.writeInt(data.getX());
			buf.writeInt(data.getY());
			buf.writeInt(data.getZ());
		}
		
		@Override
		public BlockPos readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<BlockPos> getter) {
			return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		}
		
	}
	
	public static final class VoltageData implements IDataIO<IVoltage> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType != EnumVoltage.class && IVoltage.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, IVoltage data) {
			writer.writeVoltage(data);
		}
		
		@Override
		public IVoltage readFromData(IDataReader reader, Class<?> fieldType, Supplier<IVoltage> getter) {
			return reader.readVoltage();
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, IVoltage data) {
			nbt.setInteger(name + "v", data.getVoltage());
			nbt.setDouble(name, data.getLossIndex());
		}
		
		@Override
		public IVoltage readFromNBT(NBTTagCompound nbt, String name,
		                            Class<?> fieldType, Supplier<IVoltage> getter) {
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
		public IVoltage readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<IVoltage> getter) {
			return IVoltage.getInstance(buf.readInt(), buf.readDouble());
		}
		
	}
	
	public static final class ClassData implements IDataIO<Class<?>> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == Class.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, Class<?> data) {
			writer.writeString(data.getName());
		}
		
		@Override
		public Class<?> readFromData(IDataReader reader, Class<?> fieldType, Supplier<Class<?>> getter) {
			try {
				return Class.forName(reader.readString());
			} catch (ClassNotFoundException e) {
				throw TransferException.instance("需要读写的类不存在", e);
			}
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Class<?> data) {
			nbt.setString(name, data.getName());
		}
		
		@Override
		public Class<?> readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Class<?>> getter) {
			if (getter != null) return getter.get();
			try {
				return Class.forName(nbt.getString(name));
			} catch (ClassNotFoundException e) {
				throw TransferException.instance("需要读写的类不存在", e);
			}
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Class<?> data) {
			writeString(buf, data.getName());
		}
		
		@Override
		public Class<?> readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Class<?>> getter) {
			if (getter != null) return getter.get();
			try {
				return Class.forName(readString(buf));
			} catch (ClassNotFoundException e) {
				throw TransferException.instance("需要读写的类不存在", e);
			}
		}
		
	}
	
	public static final class ElementData implements IDataIO<ItemElement> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return objType == ItemElement.class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, ItemElement data) {
			data.writeToData(writer);
		}
		
		@Override
		public ItemElement readFromData(IDataReader reader, Class<?> fieldType, Supplier<ItemElement> getter) {
			return ItemElement.instance(reader);
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, ItemElement data) {
			nbt.setTag(name, data.serializeNBT());
		}
		
		@Override
		public ItemElement readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<ItemElement> getter) {
			return ItemElement.instance(nbt);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, ItemElement data) {
			data.writeToBuf(buf);
		}
		
		@Override
		public ItemElement readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<ItemElement> getter) {
			return ItemElement.instance(buf);
		}
		
	}
	
	public static final class CollectionData implements IDataIO<Collection<?>> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return Collection.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, Collection<?> data) {
			writer.writeVarInt(data.size());
			for (Object o : data) {
				DataSerialize.write(writer, o.getClass());
				DataSerialize.write(writer, o);
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Collection<?> readFromData(IDataReader reader, Class<?> fieldType, Supplier<Collection<?>> getter) {
			int size = reader.readVarInt();
			Collection collection = getter == null ? null : getter.get();
			if (collection == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class clazz = DataSerialize.read(reader, Class.class, null);
				collection.add(DataSerialize.read(reader, clazz, null));
			}
			return collection;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Collection<?> data) {
			nbt.setInteger(name, data.size());
			int index = 0;
			for (Object o : data) {
				String str = name + index;
				DataSerialize.write(nbt, str + "name", o.getClass());
				DataSerialize.write(nbt, str, o);
				++index;
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Collection<?> readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Collection<?>> getter) {
			int size = nbt.getInteger(name);
			Collection collection = getter == null ? null : getter.get();
			if (collection == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				String str = name + i;
				Class clazz = DataSerialize.read(nbt, str + "name", Class.class, null);
				collection.add(DataSerialize.read(nbt, str, clazz, null));
			}
			return collection;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Collection<?> data) {
			buf.writeInt(data.size());
			for (Object o : data) {
				DataSerialize.write(buf, o.getClass());
				DataSerialize.write(buf, o);
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Collection<?> readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Collection<?>> getter) {
			int size = buf.readInt();
			Collection collection = getter == null ? null : getter.get();
			if (collection == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class clazz = DataSerialize.read(buf, Class.class, null);
				collection.add(DataSerialize.read(buf, clazz, null));
			}
			return collection;
		}
		
	}
	
	public static final class MapData implements IDataIO<Map<?, ?>> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return Map.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, Map<?, ?> data) {
			if (data == null) return;
			writer.writeVarInt(data.size());
			for (Map.Entry<?, ?> entry : data.entrySet()) {
				DataSerialize.write(writer, entry.getKey().getClass());
				DataSerialize.write(writer, entry.getValue().getClass());
				DataSerialize.write(writer, entry.getKey());
				DataSerialize.write(writer, entry.getValue());
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Map<?, ?> readFromData(IDataReader reader, Class<?> fieldType, Supplier<Map<?, ?>> getter) {
			int size = reader.readVarInt();
			Map map = getter == null ? null : getter.get();
			if (map == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class<?> keyClazz = DataSerialize.read(reader, Class.class, null);
				Class<?> valueClazz = DataSerialize.read(reader, Class.class, null);
				Object key = DataSerialize.read(reader, keyClazz, null);
				Object value = DataSerialize.read(reader, valueClazz, null);
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
				DataSerialize.write(nbt, str + "key", entry.getKey());
				DataSerialize.write(nbt, str + "value", entry.getValue());
				DataSerialize.write(nbt, str + "kn", entry.getKey().getClass());
				DataSerialize.write(nbt, str + "vn", entry.getValue().getClass());
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Map<?, ?> readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Map<?, ?>> getter) {
			int size = nbt.getInteger(name);
			Map map = getter == null ? null : getter.get();
			if (map == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				String str = name + i;
				Class<?> keyClazz = DataSerialize.read(nbt, str + "kn", Class.class, null);
				Class<?> valueClazz = DataSerialize.read(nbt, str + "vn", Class.class, null);
				Object key = DataSerialize.read(nbt, str + "key", keyClazz, null);
				Object value = DataSerialize.read(nbt, str + "key", valueClazz, null);
				map.put(key, value);
			}
			return map;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Map<?, ?> data) {
			if (data == null) return;
			buf.writeInt(data.size());
			for (Map.Entry<?, ?> entry : data.entrySet()) {
				DataSerialize.write(buf, entry.getKey().getClass());
				DataSerialize.write(buf, entry.getValue().getClass());
				DataSerialize.write(buf, entry.getKey());
				DataSerialize.write(buf, entry.getValue());
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Map<?, ?> readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Map<?, ?>> getter) {
			int size = buf.readInt();
			Map map = getter == null ? null : getter.get();
			if (map == null) {
				throw new NullPointerException("读写Collection时该值应该具有默认值");
			}
			for (int i = 0; i < size; ++i) {
				Class<?> keyClazz = DataSerialize.read(buf, Class.class, null);
				Class<?> valueClazz = DataSerialize.read(buf, Class.class, null);
				Object key = DataSerialize.read(buf, keyClazz, null);
				Object value = DataSerialize.read(buf, valueClazz, null);
				map.put(key, value);
			}
			return map;
		}
		
	}
	
	public static final class FluidStackData implements IDataIO<FluidStack> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return FluidStack.class.isAssignableFrom(objType);
		}
		
		@Override
		public void writeToData(IDataWriter writer, FluidStack data) {
			writer.writeVarInt(data.amount);
			writer.writeString(data.getFluid().getName());
		}
		
		@Override
		public FluidStack readFromData(IDataReader reader, Class<?> fieldType, Supplier<FluidStack> getter) {
			int amount = reader.readVarInt();
			String name = reader.readString();
			Fluid fluid = FluidRegistry.getFluid(name);
			return new FluidStack(fluid, amount);
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, FluidStack data) {
			nbt.setInteger(name + "a", data.amount);
			nbt.setString(name + "n", data.getFluid().getName());
		}
		
		@Override
		public FluidStack readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<FluidStack> getter) {
			int amount = nbt.getInteger(name + "a");
			String fluidName = nbt.getString(name + "n");
			Fluid fluid = FluidRegistry.getFluid(fluidName);
			return new FluidStack(fluid, amount);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, FluidStack data) {
			buf.writeInt(data.amount);
			DataSerialize.write(buf, data.getFluid().getName(), String.class);
		}
		
		@Override
		public FluidStack readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<FluidStack> getter) {
			int amount = buf.readInt();
			String name = DataSerialize.read(buf, String.class, String.class, null);
			Fluid fluid = FluidRegistry.getFluid(name);
			return new FluidStack(fluid, amount);
		}
		
	}
	
	public static final class FluidStackArrayData implements IDataIO<FluidStack[]> {
		
		@Override
		public boolean match(@Nonnull Class<?> objType, @Nullable Class<?> fieldType) {
			return objType == FluidStack[].class;
		}
		
		@Override
		public void writeToData(IDataWriter writer, FluidStack[] data) {
			writer.writeVarInt(data.length);
			for (FluidStack value : data) {
				DataSerialize.write(writer, value, FluidStack.class);
			}
		}
		
		@Override
		public FluidStack[] readFromData(IDataReader reader, Class<?> fieldType, Supplier<FluidStack[]> getter) {
			FluidStack[] result = getter.get();
			if (result == null) result = new FluidStack[reader.readVarInt()];
			for (int i = 0; i < result.length; i++) {
				result[i] = DataSerialize.read(reader, FluidStack.class, FluidStack.class, null);
			}
			return result;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, FluidStack[] data) {
			nbt.setInteger("size", data.length);
			NBTTagCompound tag = new NBTTagCompound();
			for (int i = 0; i < data.length; i++) {
				DataSerialize.write(tag, String.valueOf(i), data[i], FluidStack.class);
			}
			nbt.setTag("tag", tag);
		}
		
		@Override
		public FluidStack[] readFromNBT(NBTTagCompound nbt, String name,
		                                Class<?> fieldType, Supplier<FluidStack[]> getter) {
			FluidStack[] result = getter.get();
			if (result == null) result = new FluidStack[nbt.getInteger("size")];
			NBTTagCompound tag = nbt.getCompoundTag("tag");
			for (int i = 0; i < result.length; i++) {
				result[i] = DataSerialize.read(tag, String.valueOf(i),
						FluidStack.class, FluidStack.class, null);
			}
			return result;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, FluidStack[] data) {
			buf.writeInt(data.length);
			for (FluidStack value : data) {
				DataSerialize.write(buf, value, FluidStack.class);
			}
		}
		
		@Override
		public FluidStack[] readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<FluidStack[]> getter) {
			FluidStack[] result = getter.get();
			if (result == null) result = new FluidStack[buf.readInt()];
			for (int i = 0; i < result.length; i++) {
				result[i] = DataSerialize.read(buf, FluidStack.class, FluidStack.class, null);
			}
			return result;
		}
		
	}
	
	public static final class CapabilityData implements IDataIO<Object> {
		
		@Override
		public boolean match(Class<?> objType, Class<?> fieldType) {
			return getCap(objType) != null;
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void writeToData(IDataWriter writer, Object data) {
			Capability cap = getCap(data.getClass());
			NBTBase nbt = cap.writeNBT(data, null);
			writer.writeBoolean(nbt == null);
			if (nbt != null) writer.writeTag(nbt);
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Object readFromData(IDataReader reader, Class<?> fieldType, Supplier<Object> getter) {
			Object obj = getter.get();
			if (reader.readBoolean()) return obj;
			Class<?> type = obj.getClass();
			Capability cap = getCap(type);
			cap.readNBT(obj, null, reader.readTag());
			return obj;
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Object data) {
			Capability cap = getCap(data.getClass());
			NBTBase base = cap.writeNBT(data, null);
			if (base != null) nbt.setTag(name, base);
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Object readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<Object> getter) {
			Object obj = getter.get();
			if (nbt.hasKey(name)) return obj;
			Class<?> type = obj.getClass();
			Capability cap = getCap(type);
			cap.readNBT(obj, null, nbt.getTag(name));
			return obj;
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void writeToByteBuf(ByteBuf buf, Object data) {
			Capability cap = getCap(data.getClass());
			NBTBase nbt = cap.writeNBT(data, null);
			buf.writeBoolean(nbt == null);
			if (nbt != null) {
				DataSerialize.write(buf, nbt.getClass(), Class.class);
				DataSerialize.write(buf, nbt);
			}
		}
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public Object readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<Object> getter) {
			Object obj = getter.get();
			if (buf.readBoolean()) return obj;
			Class<?> type = obj.getClass();
			Capability cap = getCap(type);
			Class<?> clazz = DataSerialize.read(buf, Class.class, Class.class, null);
			cap.readNBT(obj, null, DataSerialize.read(buf, clazz, Class.class, null));
			return obj;
		}
		
		@SuppressWarnings("ConstantConditions")
		private Capability<?> getCap(Class<?> type) {
			ICapManagerCheck check = (ICapManagerCheck) (Object) CapabilityManager.INSTANCE;
			Wrapper<Capability<?>> result = new Wrapper<>();
			check.forEachCaps(it -> {
				ICapStorageType getter = (ICapStorageType) it;
				if (getter.getStorageType().isAssignableFrom(type))  {
					result.set(it);
					return true;
				}
				return false;
			});
			return result.getNullable();
		}
		
	}
	
}