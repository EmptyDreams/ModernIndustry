package top.kmar.mi.api.dor;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteLists;
import net.minecraft.nbt.NBTBase;
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
import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.electricity.interfaces.IVoltage;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author EmptyDreams
 */
public class VarDataReader implements IDataReader {
	
	/** 存储数据 */
	private final ByteList memory;
	/** 当前读取下标 */
	private int readIndex;
	/** 起始下标 */
	private final int startIndex;
	/** 结束下标 */
	private final int endIndex;
	
	public VarDataReader(ByteList list, int start, int size) {
		memory = ByteLists.unmodifiable(list);
		readIndex = start;
		startIndex = start;
		endIndex = start + size + 1;
	}
	
	private VarDataReader(VarDataReader reader) {
		this.memory = reader.memory;
		readIndex = reader.readIndex;
		endIndex = reader.endIndex;
		startIndex = reader.startIndex;
	}
	
	@Override
	public int nextReadIndex() {
		checkIndex(++readIndex);
		return readIndex;
	}
	
	@Override
	public int nowReadIndex() {
		return readIndex;
	}
	
	@Override
	public boolean isEnd() {
		return readIndex + 1 == endIndex;
	}
	
	@Override
	public void setReadIndex(int readIndex) {
		checkIndex(readIndex);
		this.readIndex = readIndex;
	}
	
	@Override
	public int size() {
		return endIndex - startIndex;
	}
	
	@Override
	public void readToNBT(NBTTagCompound nbt, String key) {
		nbt.setByteArray(key, toByteArray());
	}
	
	@Override
	public void readToWriter(IDataWriter writer) {
		writer.writeByteArray(toByteArray());
	}
	
	@Override
	public void readToList(ByteList list) {
		list.addAll(memory);
	}
	
	@Override
	public void readToByteBuf(ByteBuf buf) {
		buf.writeInt(size());
		for (int i = 0; i < memory.size(); i++) {
			buf.writeByte(memory.get(i + startIndex));
		}
	}
	
	private byte[] toByteArray() {
		byte[] data = new byte[size()];
		for (int i = 0; i < data.length; ++i) {
			data[i] = memory.get(startIndex + i);
		}
		return data;
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
			byte data = readByte();
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
	
	@NotNull
	@Override
	public long[] readLongArray() {
		int size = readVarInt();
		long[] result = new long[size];
		for (int i = 0; i != size; ++i) {
			result[i] = readLong();
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
	public VarDataReader copy() {
		return new VarDataReader(this);
	}
	
	private NBTTagCompound readNBTTagCompound() {
		int size = readVarInt();
		NBTTagCompound result = new NBTTagCompound();
		for (int i = 0; i < size; ++i) {
			result.setTag(readString(), readTag());
		}
		return result;
	}
	
	/** 检查下标 */
	private void checkIndex(int index) {
		if (index >= endIndex || index < startIndex) {
			throw new IndexOutOfBoundsException(
					"index[" + index
							+ "]应当属于[" + startIndex + "," + endIndex + ")");
		}
	}
	
}