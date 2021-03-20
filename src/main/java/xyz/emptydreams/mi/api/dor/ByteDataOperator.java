package xyz.emptydreams.mi.api.dor;

import com.google.common.base.Throwables;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.exception.IntransitException;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * @author EmptyDreams
 */
public class ByteDataOperator implements IDataOperator {
	
	private final ByteList memory;
	/** 读取时的下标 */
	private int readIndex = -1;
	/** 写入时的下标 */
	private int writeIndex = -1;
	
	public ByteDataOperator() {
		this(32);
	}
	
	public ByteDataOperator(int size) {
		memory = new ByteArrayList(size);
	}
	
	public ByteDataOperator(byte[] data) {
		this(data.length);
		memory.addElements(0, data);
	}
	
	@Override
	public int nextReadIndex() {
		return ++readIndex;
	}
	
	@Override
	public int nowReadIndex() {
		return readIndex + 1;
	}
	
	@Override
	public int size() {
		return memory.size();
	}
	
	@Override
	public int nextWriteIndex() {
		return nowReadIndex();
	}
	
	@Override
	public int nowWriteIndex() {
		return writeIndex + 1;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		byte[] data = nbt.getByteArray(".");
		for (int i = data.length - 1; i >= 0; i--) {
			writeByte(nextWriteIndex(), data[i]);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setByteArray(".", memory.toByteArray());
	}
	
	@Override
	public void readToWriter(int index, IDataWriter writer) {
		writer.writeByteArray(index, memory.toByteArray());
	}
	
	@Override
	public boolean readBoolean(int index) {
		return readByte(index) == 0;
	}
	
	@Override
	public byte readByte(int index) {
		return memory.get(index);
	}
	
	@Override
	public int readInt(int index) {
		int a = readByte(index), b = readByte(++index), c = readByte(++index), d = readByte(++index);
		return a | (b << 8) | (c << 16) | (d << 24);
	}
	
	@Override
	public char readChar(int index) {
		return (char) (readByte(index) | (((int) readByte(++index)) << 8));
	}
	
	@Override
	public short readShort(int index) {
		int a = readByte(index), b = readByte(++index);
		return (short) (a | (b << 8));
	}
	
	@Override
	public long readLong(int index) {
		long a = readByte(index),   b = readByte(++index), c = readByte(++index), d = readByte(++index);
		long e = readByte(++index), f = readByte(++index), g = readByte(++index), h = readByte(++index);
		return a | (b << 8) | (c << 16) | (d << 24)
				| (e << 32) | (f << 40) | (g << 48) | (g << 56);
	}
	
	@Override
	public float readFloat(int index) {
		return Float.intBitsToFloat(readInt(index));
	}
	
	@Override
	public double readDouble(int index) {
		return Double.longBitsToDouble(readLong(index));
	}
	
	@Override
	public int readVarint(int index) {
		int result = 0;
		for (int i = 0; i < 5; ++i) {
			byte data = readByte(index++);
			result |= (data & 0b01111111);
			if ((data & 0b10000000) == 0) break;
		}
		return result;
	}
	
	@Override
	public UUID readUuid(int index) {
		return new UUID(readLong(index), readLong(++index));
	}
	
	@Override
	public String readString(int index) {
		return new String(readByteArray(index));
	}
	
	@Override
	public int[] readIntArray(int index) {
		int size = readVarint(index);
		int[] result = new int[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readInt(++index);
		}
		return result;
	}
	
	@Override
	public int[] readVarintArray(int index) {
		int size = readVarint(index);
		int[] result = new int[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readVarint(++index);
		}
		return result;
	}
	
	@Override
	public byte[] readByteArray(int index) {
		int size = readVarint(index);
		byte[] result = new byte[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readByte(++index);
		}
		return result;
	}
	
	@Override
	public BlockPos readBlockPos(int index) {
		return new BlockPos(readInt(index), readInt(++index), readInt(++index));
	}
	
	@Override
	public IDataReader readData(int index) {
		return new ByteDataOperator(readByteArray(index));
	}
	
	@Override
	public IVoltage readVoltage(int index) {
		int voltage = readVarint(index);
		double loss = readDouble(++index);
		return IVoltage.getInstance(voltage, loss);
	}
	
	@Override
	public NBTBase readTag(int index) {
		int id = readByte(index);
		switch (id) {
			case 1: return new NBTTagByte(readByte(++index));
			case 2: return new NBTTagShort(readShort(++index));
			case 3: return new NBTTagInt(readVarint(++index));
			case 4: return new NBTTagLong(readLong(++index));
			case 5: return new NBTTagFloat(readFloat(++index));
			case 6: return new NBTTagDouble(readDouble(++index));
			case 7: return new NBTTagByteArray(readByteArray(++index));
			case 8: return new NBTTagString(readString(++index));
			case 10: return readNBTTagCompound(++index);
			case 11: return new NBTTagIntArray(readIntArray(++index));
			case 9:
				int size = readVarint(++index);
				NBTTagList list = new NBTTagList();
				for (int i = 0; i < size; ++i) {
					list.appendTag(readTag(++index));
				}
				return list;
			default: throw
					new UnsupportedOperationException("不支持读写该类型：NBTBase.id = " + id);
		}
	}
	
	@Override
	public NBTTagCompound readTagCompound(int index) {
		return (NBTTagCompound) readTag(index);
	}
	
	@Override
	public void writeBoolean(int index, boolean data) {
		writeByte(index, data ? (byte) 0 : (byte) 1);
	}
	
	@Override
	public void writeByte(int index, byte data) {
		memory.add(index, data);
		++writeIndex;
	}
	
	@Override
	public void writeInt(int index, int data) {
		memory.add(index, (byte) (data >>> 24));
		memory.add(index, (byte) (data >>> 16));
		memory.add(index, (byte) (data >>> 8));
		memory.add(index, (byte) (data));
	}
	
	@Override
	public void writeChar(int index, char data) {
		writeByte(index, (byte) (data >>> 8));
		writeByte(index, (byte) data);
	}
	
	@Override
	public void writeShort(int index, short data) {
		memory.add(index, (byte) (data >>> 8));
		memory.add(index, (byte) data);
	}
	
	@Override
	public void writeLong(int index, long data) {
		memory.add(index, (byte) (data >>> 56));
		memory.add(index, (byte) (data >>> 48));
		memory.add(index, (byte) (data >>> 40));
		memory.add(index, (byte) (data >>> 32));
		memory.add(index, (byte) (data >>> 24));
		memory.add(index, (byte) (data >>> 16));
		memory.add(index, (byte) (data >>> 8));
		memory.add(index, (byte) data);
	}
	
	@Override
	public void writeFloat(int index, float data) {
		writeInt(index, Float.floatToIntBits(data));
	}
	
	@Override
	public void writeDouble(int index, double data) {
		writeLong(index, Double.doubleToLongBits(data));
	}
	
	@Override
	public void writeVarint(int index, int data) {
		if ((data & 0b11111111_11111111_11111111_10000000) == 0) {
			memory.add(index, (byte) (data & 0b01111111));
		} else if ((data & 0b11111111_11111111_11000000_00000000) == 0) {
			memory.add(index, (byte) ((data >>> 7) & 0b01111111));
			memory.add(index, (byte) ((data & 0b01111111) | 0b10000000));
		} else if ((data & 0b11111111_11100000_00000000_00000000) == 0) {
			memory.add(index, (byte) ((data >>> 14) & 0b01111111));
			memory.add(index, (byte) (((data >>> 7) & 0b01111111) | 0b10000000));
			memory.add(index, (byte) ((data & 0b01111111) | 0b10000000));
		} else if ((data & 0b11110000_00000000_00000000_00000000) == 0) {
			memory.add(index, (byte) (((data) >>> 21) & 0b01111111));
			memory.add(index, (byte) (((data >>> 14) & 0b01111111) | 0b10000000));
			memory.add(index, (byte) (((data >>> 7) & 0b01111111) | 0b10000000));
			memory.add(index, (byte) ((data & 0b01111111) | 0b10000000));
		} else {
			memory.add(index, (byte) ((data >>> 28) & 0b00001111));
			memory.add(index, (byte) ((((data) >>> 21) & 0b01111111) | 0b10000000));
			memory.add(index, (byte) (((data >>> 14) & 0b01111111) | 0b10000000));
			memory.add(index, (byte) (((data >>> 7) & 0b01111111) | 0b10000000));
			memory.add(index, (byte) ((data & 0b01111111) | 0b10000000));
		}
	}
	
	@Override
	public void writeUuid(int index, UUID data) {
		writeLong(index, data.getLeastSignificantBits());
		writeLong(index, data.getMostSignificantBits());
	}
	
	@Override
	public void writeString(int index, String data) {
		writeByteArray(index, data.getBytes(StandardCharsets.UTF_8));
	}
	
	@Override
	public void writeIntArray(int index, int[] data) {
		for (int i = data.length - 1; i >= 0; i--) {
			writeInt(index, data[i]);
		}
		writeVarint(index, data.length);
	}
	
	@Override
	public void writeVarintArray(int index, int[] data) {
		for (int i = data.length - 1; i >= 0; i--) {
			writeVarint(index, data[i]);
		}
		writeVarint(index, data.length);
	}
	
	@Override
	public void writeByteArray(int index, byte[] data) {
		for (int i = data.length - 1; i >= 0; i--) {
			writeByte(index, data[i]);
		}
		writeVarint(index, data.length);
	}
	
	@Override
	public void writeBlockPos(int index, BlockPos data) {
		writeInt(index, data.getZ());
		writeInt(index, data.getY());
		writeInt(index, data.getX());
	}
	
	@Override
	public void writeVoltage(int index, IVoltage data) {
		writeDouble(index, data.getLossIndex());
		writeVarint(index, data.getVoltage());
	}
	
	@Override
	public void writeTag(int index, NBTBase data) {
		writeByte(index, data.getId());
		switch (data.getId()) {
			case 1: writeByte(index, ((NBTPrimitive) data).getByte());                break;
			case 2: writeShort(index, ((NBTPrimitive) data).getShort());              break;
			case 3: writeVarint(index, ((NBTPrimitive) data).getInt());               break;
			case 4: writeLong(index, ((NBTPrimitive) data).getLong());                break;
			case 5: writeFloat(index, ((NBTPrimitive) data).getFloat());              break;
			case 6: writeDouble(index, ((NBTPrimitive) data).getDouble());            break;
			case 7: writeByteArray(index, ((NBTTagByteArray) data).getByteArray());   break;
			case 8: writeString(index, data.toString());                              break;
			case 10: writeNBTTagCompound(index, (NBTTagCompound) data);               break;
			case 11: writeIntArray(index, ((NBTTagIntArray) data).getIntArray());     break;
			case 9:
				NBTTagList list = (NBTTagList) data;
				writeVarint(index, list.tagCount());
				list.forEach(it -> writeTag(index, it));
				break;
			default: throw
					new UnsupportedOperationException("不支持读写该类型：" + data.getClass().getSimpleName());
		}
	}
	
	private void writeNBTTagCompound(int index, NBTTagCompound data) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, NBTBase> map =
					(Map<String, NBTBase>) data.getClass().getDeclaredField("tagMap").get(data);
			map.forEach((key, value) -> {
				writeTag(index, value);
				writeString(index, key);
			});
			writeVarint(index, data.getSize());
		} catch (IllegalAccessException | NoSuchFieldException e) {
			Throwables.throwIfUnchecked(e);
			throw new IntransitException(e);
		}
	}
	
	private NBTTagCompound readNBTTagCompound(int index) {
		int size = readVarint(index);
		NBTTagCompound result = new NBTTagCompound();
		for (int i = 0; i < size; ++i) {
			result.setTag(readString(++index), readTag(++index));
		}
		return result;
	}
	
}