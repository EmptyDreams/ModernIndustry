package xyz.emptydreams.mi.api.dor;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import java.util.UUID;

/**
 * 支持数据写入的Operator
 * @author EmptyDreams
 */
public interface IDataWriter {
	
	/** 下一个写入位点 */
	int nextWriteIndex();
	/** 当前写入位点 */
	int nowWriteIndex();
	/** 设置写入位点 */
	void setWriteIndex(int index);
	/** 写入的数据总大小（单位：Bit） */
	int size();
	/** 从NBT读取数据 */
	void writeFromNBT(NBTTagCompound nbt, String key);
	/** 从ByteBuf中读取数据 */
	void writeFromByteBuf(ByteBuf buf);
	
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
	void writeBlockPos(BlockPos data);
	void writeVoltage(IVoltage data);
	void writeTag(NBTBase data);
	/** 将指定reader写入到当前writer */
	default void writeData(IDataReader data) {
		data.readToWriter(this);
	}
	
}