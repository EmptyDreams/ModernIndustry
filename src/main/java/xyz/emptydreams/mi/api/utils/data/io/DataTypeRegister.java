package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

/**
 * 注册数据类型
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class DataTypeRegister {
	
	/** 存储注册列表 */
	private static final Map<Class<?>, IDataIO> INSTANCES = new Object2ObjectOpenHashMap<>();
	
	/**
	 * 注册一个可读写的数据类型
	 * @param type 数据类型对应的Class
	 * @param io IO操作类
	 */
	public static void registry(Class<?> type, IDataIO io) {
		INSTANCES.put(type, io);
	}
	
	/**
	 * 写入数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param data 数据内容
	 */
	public static void write(NBTTagCompound nbt, String name, Object data) {
		IDataIO io = INSTANCES.get(data.getClass());
		io.writeToNBT(nbt, name, data);
	}
	
	/**
	 * 写入数据
	 * @param buf buf对象
	 * @param data 数据内容
	 */
	public static void write(ByteBuf buf, Object data) {
		IDataIO io = INSTANCES.get(data.getClass());
		io.writeToByteBuf(buf, data);
	}
	
	/**
	 * 读取数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param type 数据类型
	 */
	public static <T> T read(NBTTagCompound nbt, String name, Class type) {
		IDataIO io = INSTANCES.get(type);
		return (T) io.readFromNBT(nbt, name);
	}
	
	/**
	 * 读取数据
	 * @param buf buf对象
	 * @param type 数据类型
	 */
	public static <T> T read(ByteBuf buf, Class type) {
		IDataIO io = INSTANCES.get(type);
		return (T) io.readFromByteBuf(buf);
	}
	
}