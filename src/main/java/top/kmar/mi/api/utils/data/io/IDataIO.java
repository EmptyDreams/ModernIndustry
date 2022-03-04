package top.kmar.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author EmptyDreams
 */
public interface IDataIO<T> {
	
	/**
	 * 判断指定的类是否符合该类
	 * @param objType 进行读写的数据的真实Class对象
	 * @param fieldType 类中声明的类型，为null表示未知
	 */
	boolean match(@Nonnull Class<?> objType, @Nullable Class<?> fieldType);
	
	/**
	 * 写入数据到writer
	 * @param writer writer对象
	 * @param data 数据内容
	 */
	void writeToData(IDataWriter writer, T data);
	
	T readFromData(IDataReader reader, Class<?> fieldType, Supplier<T> getter);
	
	/**
	 * 从reader读取数据
	 * @param reader reader对象
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	default T readFromData(IDataReader reader, Supplier<T> getter) {
		return readFromData(reader, null, getter);
	}
	
	/**
	 * 写入数据到NBTTagCompound
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param data 数据内容
	 */
	void writeToNBT(NBTTagCompound nbt, String name, T data);
	
	T readFromNBT(NBTTagCompound nbt, String name, Class<?> fieldType, Supplier<T> getter);
	
	/**
	 * 从NBTTagCompound读取一个数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	default T readFromNBT(NBTTagCompound nbt, String name, Supplier<T> getter) {
		return readFromNBT(nbt, name, null, getter);
	}
	
	/**
	 * 写入数据到ByteBuf
	 * @param buf buf对象
	 * @param data 数据内容
	 */
	void writeToByteBuf(ByteBuf buf, T data);
	
	T readFromByteBuf(ByteBuf buf, Class<?> fieldType, Supplier<T> getter);
	
	/**
	 * 从ByteBuf读取数据
	 * @param buf buf对象
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	default T readFromByteBuf(ByteBuf buf, Supplier<T> getter) {
		return readFromByteBuf(buf, null, getter);
	}
	
	/**
	 * 将输入的数据转换为指定类型
	 * @param data 数据
	 * @param target 目的类型的Class
	 * @throws ClassCastException 如果转换失败
	 * @throws UnsupportedOperationException 如果该类型不支持转换
	 */
	@SuppressWarnings("unchecked")
	default <R> R cast(T data, Class<R> target) {
		if (target == String.class) return (R) data.toString();
		throw new UnsupportedOperationException("不支持进行类型转换：["
				+ data.getClass().getName() + "] to [" + target.getName() + "]");
	}
	
}