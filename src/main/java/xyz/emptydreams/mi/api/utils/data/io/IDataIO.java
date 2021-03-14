package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.nbt.IDataReader;
import xyz.emptydreams.mi.api.nbt.IDataWriter;

import java.util.function.Supplier;

/**
 * @author EmptyDreams
 */
public interface IDataIO<T> {
	
	/**
	 * 写入数据到writer
	 * @param writer writer对象
	 * @param data 数据内容
	 */
	void writeToData(IDataWriter writer, T data);
	
	/**
	 * 从reader读取数据
	 * @param reader reader对象
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	T readFromData(IDataReader reader, Supplier<T> getter);
	
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
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	T readFromNBT(NBTTagCompound nbt, String name, Supplier<T> getter);
	
	/**
	 * 写入数据到ByteBuf
	 * @param buf buf对象
	 * @param data 数据内容
	 */
	void writeToByteBuf(ByteBuf buf, T data);
	
	/**
	 * 从ByteBuf读取数据
	 * @param buf buf对象
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	T readFromByteBuf(ByteBuf buf, Supplier<T> getter);
	
}