package xyz.emptydreams.mi.api.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import java.util.UUID;

/**
 * 数据写入
 * @author EmptyDreams
 */
public interface IDataWriter {
	
	/** 下一个写入位点 */
	int nextWriteIndex();
	
	void writeBoolean(int index, boolean data);
	void writeByte(int index, byte data);
	void writeInt(int index, int data);
	void writeChar(int index, char data);
	void writeShort(int index, short data);
	void writeLong(int index, long data);
	void writeFloat(int index, float data);
	void writeDouble(int index, double data);
	void writeVarint(int index, int data);
	void writeUuid(int index, UUID data);
	void writeString(int index, String data);
	void writeIntArray(int index, int[] data);
	void writeVarintArray(int index, int[] data);
	void writeByteArray(int index, byte[] data);
	void writeBlockPos(int index, BlockPos data);
	void writeVoltage(int index, IVoltage data);
	void writeTag(int index, NBTBase data);
	/** 将指定reader写入到当前writer */
	default void writeData(int index, IDataReader data) {
		data.readToWriter(index, this);
	}
	
	default void writeBoolean(boolean data) {
		writeBoolean(nextWriteIndex(), data);
	}
	default void writeByte(byte data) {
		writeByte(nextWriteIndex(), data);
	}
	default void writeInt(int data) {
		writeInt(nextWriteIndex(), data);
	}
	default void writeChar(char data) {
		writeChar(nextWriteIndex(), data);
	}
	default void writeShort(short data) {
		writeShort(nextWriteIndex(), data);
	}
	default void writeLong(long data) {
		writeLong(nextWriteIndex(), data);
	}
	default void writeFloat(float data) {
		writeFloat(nextWriteIndex(), data);
	}
	default void writeDouble(double data) {
		writeDouble(nextWriteIndex(), data);
	}
	default void writeVarint(int data) {
		writeVarint(nextWriteIndex(), data);
	}
	default void writeUuid(UUID data) {
		writeUuid(nextWriteIndex(), data);
	}
	default void writeString(String data) {
		writeString(nextWriteIndex(), data);
	}
	default void writeIntArray(int[] data) {
		writeIntArray(nextWriteIndex(), data);
	}
	default void writeVarintArray(int[] data) {
		writeVarintArray(nextWriteIndex(), data);
	}
	default void writeByteArray(byte[] data) {
		writeByteArray(nextWriteIndex(), data);
	}
	default void writeBlockPos(BlockPos data) {
		writeBlockPos(nextWriteIndex(), data);
	}
	default void writeVoltage(IVoltage data) {
		writeVoltage(nextWriteIndex(), data);
	}
	default void writeTag(NBTBase data) {
		writeTag(nextWriteIndex(), data);
	}
	/** 将指定reader写入到当前writer */
	default void writeData(IDataReader data) {
		data.readToWriter(this);
	}
	
}