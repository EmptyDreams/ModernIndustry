package xyz.emptydreams.mi.api.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import java.util.UUID;

/**
 * 数据读取
 * @author EmptyDreams
 */
public interface IDataReader {
	
	/** 将reader中的数据读取到writer中 */
	void readToWriter(IDataWriter writer);
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