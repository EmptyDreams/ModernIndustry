package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.register.AutoLoader;

import java.nio.charset.StandardCharsets;

/**
 * @author EmptyDreams
 */
@AutoLoader
public final class DataTypes {
	
	public static final class IntData implements IDataIO<Integer> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Integer data) {
			nbt.setInteger(name, data);
		}
		
		@Override
		public Integer readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getInteger(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Integer data) {
			buf.writeInt(data);
		}
		
		@Override
		public Integer readFromByteBuf(ByteBuf buf) {
			return buf.readInt();
		}
		
	}
	
	public static final class ByteData implements IDataIO<Byte> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Byte data) {
			nbt.setByte(name, data);
		}
		
		@Override
		public Byte readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getByte(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Byte data) {
			buf.writeByte(data);
		}
		
		@Override
		public Byte readFromByteBuf(ByteBuf buf) {
			return buf.readByte();
		}
		
	}
	
	public static final class BooleanData implements IDataIO<Boolean> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Boolean data) {
			nbt.setBoolean(name, data);
		}
		
		@Override
		public Boolean readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getBoolean(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Boolean data) {
			buf.writeBoolean(data);
		}
		
		@Override
		public Boolean readFromByteBuf(ByteBuf buf) {
			return buf.readBoolean();
		}
		
	}
	
	public static final class LongData implements IDataIO<Long> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Long data) {
			nbt.setLong(name, data);
		}
		
		@Override
		public Long readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getLong(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Long data) {
			buf.writeLong(data);
		}
		
		@Override
		public Long readFromByteBuf(ByteBuf buf) {
			return buf.readLong();
		}
		
	}
	
	public static final class DoubleData implements IDataIO<Double> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Double data) {
			nbt.setDouble(name, data);
		}
		
		@Override
		public Double readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getDouble(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Double data) {
			buf.writeDouble(data);
		}
		
		@Override
		public Double readFromByteBuf(ByteBuf buf) {
			return buf.readDouble();
		}
		
	}
	
	public static final class ShortData implements IDataIO<Short> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Short data) {
			nbt.setShort(name, data);
		}
		
		@Override
		public Short readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getShort(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Short data) {
			buf.writeShort(data);
		}
		
		@Override
		public Short readFromByteBuf(ByteBuf buf) {
			return buf.readShort();
		}
		
	}
	
	public static final class FloatData implements IDataIO<Float> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, Float data) {
			nbt.setFloat(name, data);
		}
		
		@Override
		public Float readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getFloat(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, Float data) {
			buf.writeFloat(data);
		}
		
		@Override
		public Float readFromByteBuf(ByteBuf buf) {
			return buf.readFloat();
		}
		
	}
	
	public static final class IntArrayData implements IDataIO<int[]> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, int[] data) {
			nbt.setIntArray(name, data);
		}
		
		@Override
		public int[] readFromNBT(NBTTagCompound nbt, String name) {
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
		public int[] readFromByteBuf(ByteBuf buf) {
			int size = buf.readInt();
			int[] result = new int[size];
			for (int i = 0; i < size; ++i) {
				result[i] = buf.readInt();
			}
			return result;
		}
		
	}
	
	public static final class ByteArrayData implements IDataIO<byte[]> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, byte[] data) {
			nbt.setByteArray(name, data);
		}
		
		@Override
		public byte[] readFromNBT(NBTTagCompound nbt, String name) {
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
		public byte[] readFromByteBuf(ByteBuf buf) {
			int size = buf.readInt();
			byte[] result = new byte[size];
			for (int i = 0; i < size; ++i) {
				result[i] = buf.readByte();
			}
			return result;
		}
		
	}
	
	public static final class StringData implements IDataIO<String> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, String data) {
			nbt.setString(name, data);
		}
		
		@Override
		public String readFromNBT(NBTTagCompound nbt, String name) {
			return nbt.getString(name);
		}
		
		@Override
		public void writeToByteBuf(ByteBuf buf, String data) {
			byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
			buf.writeInt(bytes.length);
			for (byte b : bytes) {
				buf.writeByte(b);
			}
		}
		
		@Override
		public String readFromByteBuf(ByteBuf buf) {
			int size = buf.readInt();
			byte[] result = new byte[size];
			for (int i = 0; i < size; ++i) {
				result[i] = buf.readByte();
			}
			return new String(result);
		}
		
	}
	
	public static final class StringBuilderData implements IDataIO<StringBuilder> {
		
		@Override
		public void writeToNBT(NBTTagCompound nbt, String name, StringBuilder data) {
			nbt.setString(name, data.toString());
		}
		
		@Override
		public StringBuilder readFromNBT(NBTTagCompound nbt, String name) {
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
		public StringBuilder readFromByteBuf(ByteBuf buf) {
			int size = buf.readInt();
			StringBuilder result = new StringBuilder(size);
			for (int i = 0; i < size; ++i) {
				result.append(buf.readChar());
			}
			return result;
		}
		
	}
	
}