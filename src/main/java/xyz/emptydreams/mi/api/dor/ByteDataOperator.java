package xyz.emptydreams.mi.api.dor;

import io.netty.buffer.ByteBuf;
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
import xyz.emptydreams.mi.api.dor.interfaces.IDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.exception.TransferException;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * 通用数据操作类
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
	
	/**
	 * 构建一个指定大小的operator（可扩容）
	 * @param size 指定大小（单位：字节）
	 */
	public ByteDataOperator(int size) {
		memory = new ByteArrayList(size);
	}
	
	public ByteDataOperator(byte[] data) {
		this(data.length);
		memory.addElements(0, data);
	}
	
	public ByteDataOperator(ByteList list) {
		this(list.size());
		memory.addAll(list);
	}
	
	@Override
	public int nextReadIndex() {
		return ++readIndex;
	}
	
	@Override
	public int nowReadIndex() {
		return readIndex;
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
		return ++writeIndex;
	}
	
	@Override
	public int nowWriteIndex() {
		return writeIndex;
	}
	
	@Override
	public void setWriteIndex(int index) {
		writeIndex = index;
	}
	
	@Override
	public void writeFromNBT(NBTTagCompound nbt, String key) {
		byte[] data = nbt.getByteArray(key);
		for (byte b : data) {
			writeByte(b);
		}
	}
	
	@Override
	public void writeFromByteBuf(ByteBuf buf) {
		int size = buf.readInt();
		for (int i = 0; i < size; ++i) {
			memory.add(buf.readByte());
		}
	}
	
	@Override
	public void readToNBT(NBTTagCompound nbt, String key) {
		nbt.setByteArray(key, memory.toByteArray());
	}
	
	@Override
	public void readToWriter(IDataWriter writer) {
		writer.writeByteArray(memory.toByteArray());
	}
	
	@Override
	public void readToList(ByteList list) {
		list.addAll(memory);
	}
	
	@Override
	public void readToByteBuf(ByteBuf buf) {
		buf.writeInt(size());
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < memory.size(); i++) {
			buf.writeByte(memory.get(i));
		}
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
	public int readVarInt() {
		int result = 0;
		for (int i = 0; i < 5; ++i) {
			int data = readByte();
			result |= (data & 0b01111111) << (i * 7);
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
		int size = readVarInt();
		int[] result = new int[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readInt();
		}
		return result;
	}
	
	@Override
	public int[] readVarIntArray() {
		int size = readVarInt();
		int[] result = new int[size];
		for (int i = 0; i < size; ++i) {
			result[i] = readVarInt();
		}
		return result;
	}
	
	@Override
	public byte[] readByteArray() {
		int size = readVarInt();
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
		int size = readVarInt();
		IDataReader reader =  new VarDataReader(memory, nowReadIndex(), size);
		setReadIndex(nowReadIndex() + size);
		return reader;
	}
	
	@Override
	public IVoltage readVoltage() {
		int voltage = readVarInt();
		double loss = readDouble();
		return IVoltage.getInstance(voltage, loss);
	}
	
	@Override
	public NBTBase readTag() {
		int id = readByte();
		switch (id) {
			case 1: return new NBTTagByte(readByte());
			case 2: return new NBTTagShort(readShort());
			case 3: return new NBTTagInt(readVarInt());
			case 4: return new NBTTagLong(readLong());
			case 5: return new NBTTagFloat(readFloat());
			case 6: return new NBTTagDouble(readDouble());
			case 7: return new NBTTagByteArray(readByteArray());
			case 8: return new NBTTagString(readString());
			case 10: return readNBTTagCompound();
			case 11: return new NBTTagIntArray(readIntArray());
			case 9:
				int size = readVarInt();
				NBTTagList list = new NBTTagList();
				for (int i = 0; i < size; ++i) {
					list.appendTag(readTag());
				}
				return list;
			default: throw
					new UnsupportedOperationException("不支持读写该类型：NBTBase.id = " + id);
		}
	}
	
	@Nonnull
	@Override
	public ByteDataOperator copy() {
		return new ByteDataOperator(memory);
	}
	
	@Override
	public void writeBoolean(boolean data) {
		writeByte(data ? (byte) 0 : (byte) 1);
	}
	
	@Override
	public void writeByte(byte data) {
		memory.add(nextWriteIndex(), data);
	}
	
	@Override
	public void writeInt(int data) {
		writeByte((byte) (data));
		writeByte((byte) (data >>> 8));
		writeByte((byte) (data >>> 16));
		writeByte((byte) (data >>> 24));
	}
	
	@Override
	public void writeChar(char data) {
		writeByte((byte) data);
		writeByte((byte) (data >>> 8));
	}
	
	@Override
	public void writeShort(short data) {
		writeByte((byte) data);
		writeByte((byte) (data >>> 8));
	}
	
	@Override
	public void writeLong(long data) {
		writeByte((byte) data);
		writeByte((byte) (data >>> 8));
		writeByte((byte) (data >>> 16));
		writeByte((byte) (data >>> 24));
		writeByte((byte) (data >>> 32));
		writeByte((byte) (data >>> 40));
		writeByte((byte) (data >>> 48));
		writeByte((byte) (data >>> 56));
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
	public void writeVarInt(int data) {
		for (int i = 0; i < 5; ++i) {
			int write = data & 0b01111111;
			data >>>= 7;
			if (data == 0) {
				writeByte((byte) write);
				break;
			} else {
				writeByte((byte) (write | 0b10000000));
			}
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
		writeVarInt(data.length);
		for (int i : data) {
			writeInt(i);
		}
	}
	
	@Override
	public void writeVarIntArray(int[] data) {
		writeVarInt(data.length);
		for (int i : data) {
			writeVarInt(i);
		}
	}
	
	@Override
	public void writeByteArray(byte[] data) {
		writeVarInt(data.length);
		for (byte b : data) {
			writeByte(b);
		}
	}
	
	@Override
	public void writeBlockPos(BlockPos data) {
		writeInt(data.getX());
		writeInt(data.getY());
		writeInt(data.getZ());
	}
	
	@Override
	public void writeVoltage(IVoltage data) {
		writeVarInt(data.getVoltage());
		writeDouble(data.getLossIndex());
	}
	
	@Override
	public void writeTag(NBTBase data) {
		writeByte(data.getId());
		switch (data.getId()) {
			case 1: writeByte(((NBTPrimitive) data).getByte());                break;
			case 2: writeShort(((NBTPrimitive) data).getShort());              break;
			case 3: writeVarInt(((NBTPrimitive) data).getInt());               break;
			case 4: writeLong(((NBTPrimitive) data).getLong());                break;
			case 5: writeFloat(((NBTPrimitive) data).getFloat());              break;
			case 6: writeDouble(((NBTPrimitive) data).getDouble());            break;
			case 7: writeByteArray(((NBTTagByteArray) data).getByteArray());   break;
			case 8: writeString(((NBTTagString) data).getString());                              break;
			case 10: writeNBTTagCompound((NBTTagCompound) data);               break;
			case 11: writeIntArray(((NBTTagIntArray) data).getIntArray());     break;
			case 9:
				NBTTagList list = (NBTTagList) data;
				writeVarInt(list.tagCount());
				list.forEach(this::writeTag);
				break;
			default:
				throw new UnsupportedOperationException(
						"不支持读写该类型：" + data.getClass().getSimpleName());
		}
	}
	
	private void writeNBTTagCompound(NBTTagCompound data) {
		try {
			Field field = data.getClass().getDeclaredField("tagMap");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, NBTBase> map = (Map<String, NBTBase>) field.get(data);
			writeVarInt(data.getSize());
			map.forEach((key, value) -> {
				writeString(key);
				writeTag(value);
			});
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw TransferException.instance(e);
		}
	}
	
	private NBTTagCompound readNBTTagCompound() {
		int size = readVarInt();
		NBTTagCompound result = new NBTTagCompound();
		for (int i = 0; i < size; ++i) {
			result.setTag(readString(), readTag());
		}
		return result;
	}
	
}