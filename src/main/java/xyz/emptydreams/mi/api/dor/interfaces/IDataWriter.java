package xyz.emptydreams.mi.api.dor.interfaces;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import javax.annotation.Nonnull;
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
	
	/** 写入一个boolean */
	void writeBoolean(boolean data);
	/** 写入一个byte */
	void writeByte(byte data);
	/** 写入一个int */
	void writeInt(int data);
	/** 写入一个char */
	void writeChar(char data);
	/** 写入一个short */
	void writeShort(short data);
	/** 写入一个long */
	void writeLong(long data);
	/** 写入一个float */
	void writeFloat(float data);
	/** 写入一个double */
	void writeDouble(double data);
	/** 写入一个VarInt */
	void writeVarInt(int data);
	/** 写入一个UUID */
	void writeUuid(UUID data);
	/** 写入一个字符串 */
	void writeString(String data);
	/** 写入一个int数组 */
	void writeIntArray(int[] data);
	/** 写入一个VarInt数组 */
	void writeVarIntArray(int[] data);
	/** 写入一个byte数组 */
	void writeByteArray(byte[] data);
	/** 写入一个BlockPos */
	void writeBlockPos(BlockPos data);
	/** 写入一个电压值 */
	void writeVoltage(IVoltage data);
	/** 写入一个NBTBase */
	void writeTag(NBTBase data);
	/** 将指定reader写入到当前writer */
	default void writeData(IDataReader data) {
		data.readToWriter(this);
	}
	
	/** 复制自身 */
	@Nonnull IDataWriter copy();
	
}