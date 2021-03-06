package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author EmptyDreams
 */
public interface IDataIO<T> {
	
	/**
	 * 写入数据到NBTTagCompound
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param data 数据内容
	 */
	void writeToNBT(NBTTagCompound nbt, String name, T data);
	
	/**
	 * 从NBTTagCompound读取一个数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 */
	T readFromNBT(NBTTagCompound nbt, String name);
	
	/**
	 * 写入数据到ByteBuf
	 * @param buf buf对象
	 * @param data 数据内容
	 */
	void writeToByteBuf(ByteBuf buf, T data);
	
	/**
	 * 从ByteBuf读取数据
	 * @param buf buf对象
	 */
	T readFromByteBuf(ByteBuf buf);
	
}