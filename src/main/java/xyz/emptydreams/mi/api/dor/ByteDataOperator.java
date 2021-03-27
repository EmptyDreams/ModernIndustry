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
	public void setReadIndex(int readIndex) {
		this.readIndex = readIndex;
	}
	
	@Override
	public int size() {
		return memory.size();
	}
	
	@Override
	public int nextWriteIndex() {
		return memory.size();
	}
	
	@Override
	public void writeFromNBT(NBTTagCompound nbt, String key) {
		byte[] data = nbt.getByteArray(key);
		for (byte b : data) {
			writeByte(b);
		}
	}
	
	@Override
	public void readToNBT(NBTTagCompound nbt, String key) {
		nbt.setByteArray(key, memory.toByteArray());
	}
	
	@Override
	public void readToWriter(int index, IDataWriter writer) {
		writer.writeByteArray(index, memory.toByteArray());
	}
	
	@Override
	public boolean readBoolean() {
		return readByte() == 0;
	}
	
	@Override
	public byte readByte() {
		return memory.get(nextReadIndex());
	}
	
	@Override
	public int readInt() {
		int a = readByte() & 0xff, b = readByte() & 0xff, c = readByte() & 0xff, d = readByte() & 0xff;
		return a | (b << 8) | (c << 16) | (d << 24);
	}
	
	@Override
	public char readChar() {
		return (char) (readByte() | (((int) readByte()) << 8));
	}
	
	@Override
	public short readShort() {
		int a = readByte() & 0xff, b = readByte() & 0xff;
		return (short) (a | (b << 8));
	}
	
	@Override
	public long readLong() {
		long a = readByte() & 0xff, b = readByte() & 0xff, c = readByte() & 0xff, d = readByte() & 0xff;
		long e = readByte() & 0xff, f = readByte() & 0xff, g = readByte() & 0xff, h = readByte() & 0xff;
		return a | (b << 8) | (c << 16) | (d << 24)
				| (e << 32) | (f << 40) | (g << 48) | (h << 56);
	}
	
	@Override
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}
	
	@Override
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}
	
	@Override
	public int readVarint() {
		int result = 0;
		for (int i = 0; i < 5; ++i) {
			byte data = readByte();
			result |= (data & 0b01111111);
			if ((data & 0b10000000) == 0) break;
		}
		return result;
	}
	
	@Override
	public UUID readUuid() {
		return new UUID(readLong(), readLong());
	}
	
	@Override
	public String readString() {
		return new String(readByteArray());
	}
	
	@Override
	public int[] readIntArray() {
		int size = readVarint();
		int[] result = new int[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readInt();
		}
		return result;
	}
	
	@Override
	public int[] readVarintArray() {
		int size = readVarint();
		int[] result = new int[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readVarint();
		}
		return result;
	}
	
	@Override
	public byte[] readByteArray() {
		int size = readVarint();
		byte[] result = new byte[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readByte();
		}
		return result;
	}
	
	@Override
	public BlockPos readBlockPos() {
		return new BlockPos(readInt(), readInt(), readInt());
	}
	
	@Override
	public IDataReader readData() {
		return new ByteDataOperator(readByteArray());
	}
	
	@Override
	public IVoltage readVoltage() {
		int voltage = readVarint();
		double loss = readDouble();
		return IVoltage.getInstance(voltage, loss);
	}
	
	@Override
	public NBTBase readTag() {
		int id = readByte();
		switch (id) {
			case 1: return new NBTTagByte(readByte());
			case 2: return new NBTTagShort(readShort());
			case 3: return new NBTTagInt(readVarint());
			case 4: return new NBTTagLong(readLong());
			case 5: return new NBTTagFloat(readFloat());
			case 6: return new NBTTagDouble(readDouble());
			case 7: return new NBTTagByteArray(readByteArray());
			case 8: return new NBTTagString(readString());
			case 10: return readNBTTagCompound();
			case 11: return new NBTTagIntArray(readIntArray());
			case 9:
				int size = readVarint();
				NBTTagList list = new NBTTagList();
				for (int i = 0; i < size; ++i) {
					list.appendTag(readTag());
				}
				return list;
			default: throw
					new UnsupportedOperationException("不支持读写该类型：NBTBase.id = " + id);
		}
	}
	
	@Override
	public NBTTagCompound readTagCompound() {
		return (NBTTagCompound) readTag();
	}
	
	@Override
	public void writeBoolean(int index, boolean data) {
		writeByte(index, data ? (byte) 0 : (byte) 1);
	}
	
	@Override
	public void writeByte(int index, byte data) {
		memory.add(index, data);
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
	
	private NBTTagCompound readNBTTagCompound() {
		int size = readVarint();
		NBTTagCompound result = new NBTTagCompound();
		for (int i = 0; i < size; ++i) {
			result.setTag(readString(), readTag());
		}
		return result;
	}
	
}