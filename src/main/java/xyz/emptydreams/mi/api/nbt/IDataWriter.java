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
	
	void writeBoolean(boolean data);
	void writeByte(byte data);
	void writeInt(int data);
	void writeChar(char data);
	void writeShort(short data);
	void writeLong(long data);
	void writeFloat(float data);
	void writeDouble(double data);
	void writeVarint(int data);
	void writeUuid(UUID data);
	void writeString(String data);
	void writeIntArray(int[] data);
	void writeVarintArray(int[] data);
	void writeByteArray(byte[] data);
	void writeBlockPos(BlockPos pos);
	void writeVoltage(IVoltage data);
	void writeTag(NBTBase data);
	
	/** 将指定reader写入到当前writer */
	default void writeData(IDataReader data) {
		data.readToWriter(this);
	}
	
}