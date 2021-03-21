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
	public void setReadIndex(int readIndex) {
		this.readIndex = readIndex;
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
	public boolean readBoolean() {
		return readByte() == 0;
	}
	
	@Override
	public byte readByte() {
		return memory.get(nextReadIndex());
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
	
	private NBTTagCompound readNBTTagCompound() {
		int size = readVarint();
		NBTTagCompound result = new NBTTagCompound();
		for (int i = 0; i < size; ++i) {
			result.setTag(readString(), readTag());
		}
		return result;
	}
	
}