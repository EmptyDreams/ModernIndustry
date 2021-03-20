package xyz.emptydreams.mi.api.dor;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
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
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * 只支持读取的Operator
 * @author EmptyDreams
 */
public final class ReadOnlyDataOperator implements IDataReader {
	
	/**
	 * 构建一个指定长度的虚拟读取器
	 * @param size 大小
	 * @param fill 数据填充
	 */
	@Nonnull
	public static ReadOnlyDataOperator virtual(int size, byte fill) {
		return new ReadOnlyDataOperator(size, fill);
	}
	
	/**
	 * 构建一个从指定NBT中读取数据的只读器
	 * @param nbt NBT
	 */
	@Nonnull
	public static ReadOnlyDataOperator instance(NBTTagCompound nbt) {
		return new ReadOnlyDataOperator(nbt.getByteArray("."));
	}
	
	private final ByteList memory;
	/** 读取时的下标 */
	private int readIndex = -1;
	
	public ReadOnlyDataOperator(byte[] datas) {
		memory = ByteLists.unmodifiable(new ByteArrayList(datas));
	}
	
	public ReadOnlyDataOperator(ByteList bytes) {
		this.memory = ByteLists.unmodifiable(new ByteArrayList(bytes));
	}
	
	private ReadOnlyDataOperator(int size, byte fill) {
		ByteList list = new ByteArrayList(size);
		for (int i = 0; i < size; ++i) {
			list.add(fill);
		}
		memory = ByteLists.unmodifiable(list);
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
	public void readFromNBT(NBTTagCompound nbt) {
		throw new UnsupportedOperationException("不支持修改内部数据！");
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
	
	private NBTTagCompound readNBTTagCompound(int index) {
		int size = readVarint(index);
		NBTTagCompound result = new NBTTagCompound();
		for (int i = 0; i < size; ++i) {
			result.setTag(readString(++index), readTag(++index));
		}
		return result;
	}
	
}