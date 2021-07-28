package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;

import java.util.function.Supplier;

/**
 * 数据的读写操作
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class DataSerialize {
	
	/**
	 * 写入数据
	 * @param writer writer对象
	 * @param data 数据内容
	 * @param fieldType 数据声明的类型
	 */
	public static void write(IDataWriter writer, Object data, Class<?> fieldType) {
		IDataIO io = DataTypeRegister.searchNode(data.getClass(), fieldType);
		io.writeToData(writer, data);
	}
	
	/**
	 * 写入数据
	 * @param writer writer对象
	 * @param data 数据内容
	 */
	public static void write(IDataWriter writer, Object data) {
		write(writer, data, null);
	}
	
	/**
	 * 写入数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param data 数据内容
	 * @param fieldType 数据声明的类型
	 */
	public static void write(NBTTagCompound nbt, String name, Object data, Class<?> fieldType) {
		IDataIO io = DataTypeRegister.searchNode(data.getClass(), fieldType);
		io.writeToNBT(nbt, name, data);
	}
	
	/**
	 * 写入数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param data 数据内容
	 */
	public static void write(NBTTagCompound nbt, String name, Object data) {
		write(nbt, name, data, null);
	}
	
	/**
	 * 写入数据
	 * @param buf buf对象
	 * @param data 数据内容
	 * @param fieldType 数据声明的类型
	 */
	public static void write(ByteBuf buf, Object data, Class<?> fieldType) {
		IDataIO io = DataTypeRegister.searchNode(data.getClass(), fieldType);
		io.writeToByteBuf(buf, data);
	}
	
	/**
	 * 写入数据
	 * @param buf buf对象
	 * @param data 数据内容
	 */
	public static void write(ByteBuf buf, Object data) {
		write(buf, data, null);
	}
	
	/**
	 * 读取数据
	 * @param reader reader对象
	 * @param objType 数据实际类型
	 * @param fieldType 数据声明的类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(IDataReader reader, Class objType, Class fieldType, Supplier<T> getter) {
		IDataIO io = DataTypeRegister.searchNode(objType, fieldType);
		return (T) io.readFromData(reader, fieldType, getter);
	}
	
	/**
	 * 读取数据
	 * @param reader reader对象
	 * @param type 数据类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(IDataReader reader, Class type, Supplier<T> getter) {
		return read(reader, type, null, getter);
	}
	
	/**
	 * 读取数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param objType 数据实际类型
	 * @param fieldType 数据声明的类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(NBTTagCompound nbt, String name, Class objType, Class fieldType, Supplier<T>getter) {
		IDataIO io = DataTypeRegister.searchNode(objType, fieldType);
		return (T) io.readFromNBT(nbt, name, fieldType, getter);
	}
	
	/**
	 * 读取数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param type 数据类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(NBTTagCompound nbt, String name, Class type, Supplier<T> getter) {
		return read(nbt, name, type, null, getter);
	}
	
	/**
	 * 读取数据
	 * @param buf buf对象
	 * @param objType 数据实际类型
	 * @param fieldType 数据声明的类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(ByteBuf buf, Class objType, Class fieldType, Supplier<T>getter) {
		IDataIO io = DataTypeRegister.searchNode(objType, fieldType);
		return (T) io.readFromByteBuf(buf, fieldType, getter);
	}
	
	/**
	 * 读取数据
	 * @param buf buf对象
	 * @param type 数据类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(ByteBuf buf, Class type, Supplier<T> getter) {
		return read(buf, type, null, getter);
	}
	
}