package xyz.emptydreams.mi.api.nbt;

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
public class ByteDataOperator implements IDataWriter, IDataReader {
	
	private final ByteList memory;
	/** 读取时的下标 */
	private int index = 0;
	
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
	public void readToWriter(IDataWriter writer) {
		writer.writeByteArray(memory.toByteArray());
	}
	
	@Override
	public boolean readBoolean() {
		return readByte() == 0;
	}
	
	@Override
	public byte readByte() {
		return memory.get(index++);
	}
	
	@Override
	public int readInt() {
		int a = readByte(), b = readByte(), c = readByte(), d = readByte();
		return a | (b << 8) | (c << 16) | (d << 24);
	}
	
	@Override
	public char readChar() {
		return (char) (readByte() | (((int) readByte()) << 8));
	}
	
	@Override
	public short readShort() {
		int a = readByte(), b = readByte();
		return (short) (a | (b << 8));
	}
	
	@Override
	public long readLong() {
		long a = readByte(), b = readByte(), c = readByte(), d = readByte();
		long e = readByte(), f = readByte(), g = readByte(), h = readByte();
		return a | (b << 8) | (c << 16) | (d << 24)
				| (e << 32) | (f << 40) | (g << 48) | (g << 56);
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
		double index = readDouble();
		return IVoltage.getInstance(voltage, index);
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
	public void writeBoolean(boolean data) {
		writeByte(data ? (byte) 0 : (byte) 1);
	}
	
	@Override
	public void writeByte(byte data) {
		memory.add(data);
	}
	
	@Override
	public void writeInt(int data) {
		memory.add((byte) (data));
		memory.add((byte) (data >>> 8));
		memory.add((byte) (data >>> 16));
		memory.add((byte) (data >>> 24));
	}
	
	@Override
	public void writeChar(char data) {
		writeByte((byte) data);
		writeByte((byte) (data >>> 8));
	}
	
	@Override
	public void writeShort(short data) {
		memory.add((byte) data);
		memory.add((byte) (data >>> 8));
	}
	
	@Override
	public void writeLong(long data) {
		memory.add((byte) data);
		memory.add((byte) (data >>> 8));
		memory.add((byte) (data >>> 16));
		memory.add((byte) (data >>> 24));
		memory.add((byte) (data >>> 32));
		memory.add((byte) (data >>> 40));
		memory.add((byte) (data >>> 48));
		memory.add((byte) (data >>> 56));
	}
	
	@Override
	public void writeFloat(float data) {
		writeInt(Float.floatToIntBits(data));
	}
	
	@Override
	public void writeDouble(double data) {
		writeLong(Double.doubleToLongBits(data));
	}
	
	@Override
	public void writeVarint(int data) {
		if ((data & 0b11111111_11111111_11111111_10000000) == 0) {
			memory.add((byte) (data & 0b01111111));
		} else if ((data & 0b11111111_11111111_11000000_00000000) == 0) {
			memory.add((byte) ((data & 0b01111111) | 0b10000000));
			memory.add((byte) ((data >>> 7) & 0b01111111));
		} else if ((data & 0b11111111_11100000_00000000_00000000) == 0) {
			memory.add((byte) ((data & 0b01111111) | 0b10000000));
			memory.add((byte) (((data >>> 7) & 0b01111111) | 0b10000000));
			memory.add((byte) ((data >>> 14) & 0b01111111));
		} else if ((data & 0b11110000_00000000_00000000_00000000) == 0) {
			memory.add((byte) ((data & 0b01111111) | 0b10000000));
			memory.add((byte) (((data >>> 7) & 0b01111111) | 0b10000000));
			memory.add((byte) (((data >>> 14) & 0b01111111) | 0b10000000));
			memory.add((byte) (((data) >>> 21) & 0b01111111));
		} else {
			memory.add((byte) ((data & 0b01111111) | 0b10000000));
			memory.add((byte) (((data >>> 7) & 0b01111111) | 0b10000000));
			memory.add((byte) (((data >>> 14) & 0b01111111) | 0b10000000));
			memory.add((byte) ((((data) >>> 21) & 0b01111111) | 0b10000000));
			memory.add((byte) ((data >>> 28) & 0b00001111));
		}
	}
	
	@Override
	public void writeUuid(UUID data) {
		writeLong(data.getMostSignificantBits());
		writeLong(data.getLeastSignificantBits());
	}
	
	@Override
	public void writeString(String data) {
		writeByteArray(data.getBytes(StandardCharsets.UTF_8));
	}
	
	@Override
	public void writeIntArray(int[] data) {
		writeVarint(data.length);
		for (int i : data) {
			writeInt(i);
		}
	}
	
	@Override
	public void writeVarintArray(int[] data) {
		writeVarint(data.length);
		for (int i : data) {
			writeVarint(i);
		}
	}
	
	@Override
	public void writeByteArray(byte[] data) {
		writeVarint(data.length);
		for (byte b : data) {
			writeByte(b);
		}
	}
	
	@Override
	public void writeBlockPos(BlockPos pos) {
		writeInt(pos.getX());
		writeInt(pos.getY());
		writeInt(pos.getZ());
	}
	
	@Override
	public void writeVoltage(IVoltage data) {
		writeVarint(data.getVoltage());
		writeDouble(data.getLossIndex());
	}
	
	@Override
	public void writeTag(NBTBase data) {
		writeByte(data.getId());
		switch (data.getId()) {
			case 1: writeByte(((NBTPrimitive) data).getByte());                break;
			case 2: writeShort(((NBTPrimitive) data).getShort());              break;
			case 3: writeVarint(((NBTPrimitive) data).getInt());               break;
			case 4: writeLong(((NBTPrimitive) data).getLong());                break;
			case 5: writeFloat(((NBTPrimitive) data).getFloat());              break;
			case 6: writeDouble(((NBTPrimitive) data).getDouble());            break;
			case 7: writeByteArray(((NBTTagByteArray) data).getByteArray());   break;
			case 8: writeString(data.toString());                              break;
			case 10: writeNBTTagCompound((NBTTagCompound) data);               break;
			case 11: writeIntArray(((NBTTagIntArray) data).getIntArray());     break;
			case 9:
				NBTTagList list = (NBTTagList) data;
				writeVarint(list.tagCount());
				list.forEach(this::writeTag);
				break;
			default: throw
					new UnsupportedOperationException("不支持读写该类型：" + data.getClass().getSimpleName());
		}
	}
	
	private void writeNBTTagCompound(NBTTagCompound data) {
		try {
			writeVarint(data.getSize());
			@SuppressWarnings("unchecked")
			Map<String, NBTBase> map =
					(Map<String, NBTBase>) data.getClass().getDeclaredField("tagMap").get(data);
			map.forEach((key, value) -> {
				writeString(key);
				writeTag(value);
			});
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