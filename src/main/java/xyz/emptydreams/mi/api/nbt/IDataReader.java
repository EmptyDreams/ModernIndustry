package xyz.emptydreams.mi.api.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import java.util.UUID;

/**
 * 支持数据读取的Operator
 * @author EmptyDreams
 */
public interface IDataReader {
	
	/** 获取下一个读取位点 */
	int nextReadIndex();
	
	/** 将reader中的数据读取到writer中 */
	void readToWriter(int index, IDataWriter writer);
	boolean readBoolean(int index);
	byte readByte(int index);
	int readInt(int index);
	char readChar(int index);
	short readShort(int index);
	long readLong(int index);
	float readFloat(int index);
	double readDouble(int index);
	int readVarint(int index);
	UUID readUuid(int index);
	String readString(int index);
	int[] readIntArray(int index);
	int[] readVarintArray(int index);
	byte[] readByteArray(int index);
	BlockPos readBlockPos(int index);
	IDataReader readData(int index);
	IVoltage readVoltage(int index);
	NBTBase readTag(int index);
	NBTTagCompound readTagCompound(int index);
	
	/** 将reader中的数据读取到writer中 */
	default void readToWriter(IDataWriter writer) {
		readToWriter(nextReadIndex(), writer);
	}
	default boolean readBoolean() {
		return readBoolean(nextReadIndex());
	}
	default byte readByte() {
		return readByte(nextReadIndex());
	}
	default int readInt() {
		return readInt(nextReadIndex());
	}
	default char readChar() {
		return readChar(nextReadIndex());
	}
	default short readShort() {
		return readShort(nextReadIndex());
	}
	default long readLong() {
		return readLong(nextReadIndex());
	}
	default float readFloat() {
		return readFloat(nextReadIndex());
	}
	default double readDouble() {
		return readDouble(nextReadIndex());
	}
	default int readVarint() {
		return readVarint(nextReadIndex());
	}
	default UUID readUuid() {
		return readUuid(nextReadIndex());
	}
	default String readString() {
		return readString(nextReadIndex());
	}
	default int[] readIntArray() {
		return readIntArray(nextReadIndex());
	}
	default int[] readVarintArray() {
		return readVarintArray(nextReadIndex());
	}
	default byte[] readByteArray() {
		return readByteArray(nextReadIndex());
	}
	default BlockPos readBlockPos() {
		return readBlockPos(nextReadIndex());
	}
	default IDataReader readData() {
		return readData(nextReadIndex());
	}
	default IVoltage readVoltage() {
		return readVoltage(nextReadIndex());
	}
	default NBTBase readTag() {
		return readTag(nextReadIndex());
	}
	default NBTTagCompound readTagCompound() {
		return readTagCompound(nextReadIndex());
	}
	
}