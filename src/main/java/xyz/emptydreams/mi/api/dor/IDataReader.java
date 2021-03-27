package xyz.emptydreams.mi.api.dor;

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
	/** 设置读取位点 */
	void setReadIndex(int index);
	/** 含有的数据总大小（单位：Bit） */
	int size();
	/** 将数据写入到NBT */
	void readToNBT(NBTTagCompound nbt, String key);
	
	/** 将reader中的数据读取到writer中 */
	void readToWriter(int index, IDataWriter writer);
	/** 将reader中的数据读取到writer中 */
	default void readToWriter(IDataWriter writer) {
		readToWriter(writer.nextWriteIndex(), writer);
	}
	
	boolean readBoolean();
	byte readByte();
	int readInt();
	char readChar();
	short readShort();
	long readLong();
	float readFloat();
	double readDouble();
	int readVarint();
	UUID readUuid();
	String readString();
	int[] readIntArray();
	int[] readVarintArray();
	byte[] readByteArray();
	BlockPos readBlockPos();
	IDataReader readData();
	IVoltage readVoltage();
	NBTBase readTag();
	NBTTagCompound readTagCompound();
	
}