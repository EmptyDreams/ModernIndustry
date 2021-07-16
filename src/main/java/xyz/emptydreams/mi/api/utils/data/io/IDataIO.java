package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;

import java.util.function.Supplier;

/**
 * @author EmptyDreams
 */
public interface IDataIO<T> {
	
	/**
	 * 判断指定的类是否符合该类
	 */
	boolean match(Class<?> type);
	
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
	
	/**
	 * 将输入的数据转换为指定类型
	 * @param data 数据
	 * @param target 目的类型的Class
	 * @throws ClassCastException 如果转换失败
	 * @throws UnsupportedOperationException 如果该类型不支持转换
	 */
	default <R> R cast(T data, Class<R> target) {
		throw new UnsupportedOperationException("不支持进行类型转换：["
				+ data.getClass().getName() + "] to [" + target.getName() + "]");
	}
	
}