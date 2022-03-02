package xyz.emptydreams.mi.api.dor.interfaces;

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
	/** 获取读取的结束位点 */
	default int endIndex() {
		return size();
	}
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
	
	/** 读取一个boolean */
	boolean readBoolean();
	/** 读取一个byte */
	byte readByte();
	/** 读取一个int */
	int readInt();
	/** 读取一个char */
	char readChar();
	/** 读取一个short */
	short readShort();
	/** 读取一个long */
	long readLong();
	/** 读取一个float */
	float readFloat();
	/** 读取一个double */
	double readDouble();
	/** 读取一个VarInt */
	int readVarInt();
	
	/** 读取一个UUID */
	@Nonnull UUID readUuid();
	/** 读取一个字符串 */
	@Nonnull String readString();
	/** 读取一个int数组 */
	@Nonnull int[] readIntArray();
	/** 读取一个VarInt数组 */
	@Nonnull int[] readVarIntArray();
	/** 读取一个byte数组 */
	@Nonnull byte[] readByteArray();
	/** 读取一个BlockPos */
	@Nonnull BlockPos readBlockPos();
	/** 读取一个dor */
	@Nonnull IDataReader readData();
	/** 读取一个电压值 */
	@Nonnull IVoltage readVoltage();
	/** 读取一个NBTBase */
	@Nonnull NBTBase readTag();
	/** 读取一个NBTTagCompound */
	@Nonnull default NBTTagCompound readTagCompound() {
		return (NBTTagCompound) readTag();
	}
	
	/** 拷贝自身 */
	@Nonnull IDataReader copy();
	
}