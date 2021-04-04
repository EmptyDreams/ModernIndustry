package xyz.emptydreams.mi.api.dor;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.ByteList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * 支持数据读取的Operator
 * @author EmptyDreams
 */
public interface IDataReader {
	
	/** 获取下一个读取位点 */
	int nextReadIndex();
	/** 当前读取位点 */
	int nowReadIndex();
	/** 设置读取位点 */
	void setReadIndex(int index);
	/** 含有的数据总大小（单位：Bit） */
	int size();
	/** 将数据写入到NBT */
	void readToNBT(NBTTagCompound nbt, String key);
	
	/** 将reader中的数据读取到writer中 */
	void readToWriter(IDataWriter writer);
	/** 将reader中的数据读取到指定数组中 */
	void readToList(ByteList list);
	/** 将reader中的数据读取到ByteBuf中 */
	void readToByteBuf(ByteBuf buf);
	
	boolean readBoolean();
	byte readByte();
	int readInt();
	char readChar();
	short readShort();
	long readLong();
	float readFloat();
	double readDouble();
	int readVarint();
	
	@Nonnull
	UUID readUuid();
	@Nonnull String readString();
	@Nonnull int[] readIntArray();
	@Nonnull int[] readVarintArray();
	@Nonnull byte[] readByteArray();
	@Nonnull
	BlockPos readBlockPos();
	@Nonnull IDataReader readData();
	@Nonnull
	IVoltage readVoltage();
	@Nonnull
	NBTBase readTag();
	@Nonnull NBTTagCompound readTagCompound();
	
	/** 拷贝自身 */
	@Nonnull
	IDataReader copy();
	
}