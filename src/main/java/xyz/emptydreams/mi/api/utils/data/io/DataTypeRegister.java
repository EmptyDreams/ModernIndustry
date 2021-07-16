package xyz.emptydreams.mi.api.utils.data.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * 注册数据类型
 * @author EmptyDreams
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class DataTypeRegister {
	
	/** 存储下标 */
	private static final Set<IndexNode> INDEXS = new TreeSet<>();
	/** 存储注册列表 */
	private static final List<List<IDataIO<?>>> NODES = new LinkedList<>();
	
	/**
	 * 注册一个可读写的数据类型. 优先级默认值：1000
	 * @param io IO操作类
	 */
	public static void registry(IDataIO io) {
		registry(io, 1000);
	}
	
	/**
	 * 注册一个可读写的数据类型
	 * @param io IO操作类
	 * @param priority 数据优先级，数值越小越先执行，子类型的优先级应当大于父类型
	 */
	public static void registry(IDataIO io, int priority) {
		for (IndexNode index : INDEXS) {
			if (index.priority == priority) {
				index.getList().add(io);
				return;
			}
		}
		IndexNode index = new IndexNode(priority);
		INDEXS.add(index);
		index.getList().add(io);
	}
	
	/**
	 * 写入数据
	 * @param writer writer对象
	 * @param data 数据内容
	 */
	public static void write(IDataWriter writer, Object data) {
		IDataIO io = searchNode(data.getClass());
		io.writeToData(writer, data);
	}
	
	/**
	 * 写入数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param data 数据内容
	 */
	public static void write(NBTTagCompound nbt, String name, Object data) {
		IDataIO io = searchNode(data.getClass());
		io.writeToNBT(nbt, name, data);
	}
	
	/**
	 * 写入数据
	 * @param buf buf对象
	 * @param data 数据内容
	 */
	public static void write(ByteBuf buf, Object data) {
		IDataIO io = searchNode(data.getClass());
		io.writeToByteBuf(buf, data);
	}
	
	/**
	 * 读取数据
	 * @param reader reader对象
	 * @param type 数据类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(IDataReader reader, Class type, Supplier<T> getter) {
		IDataIO io = searchNode(type);
		return (T) io.readFromData(reader, getter);
	}
	
	/**
	 * 读取数据
	 * @param nbt NBT对象
	 * @param name 数据名称
	 * @param type 数据类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(NBTTagCompound nbt, String name, Class type, Supplier<T> getter) {
		IDataIO io = searchNode(type);
		return (T) io.readFromNBT(nbt, name, getter);
	}
	
	/**
	 * 读取数据
	 * @param buf buf对象
	 * @param type 数据类型
	 * @param getter 获取默认值，如果读取的值不需要默认值则可以为null
	 */
	public static <T> T read(ByteBuf buf, Class type, Supplier<T> getter) {
		IDataIO io = searchNode(type);
		return (T) io.readFromByteBuf(buf, getter);
	}
	
	/**
	 * 将输入的类型转化为指定类型
	 * @param data 数据
	 * @param target 目标类型
	 * @return 转化后的数据
	 * @see IDataIO#cast(Object, Class)
	 */
	public static <T, R> R cast(T data, Class<R> target) {
		IDataIO io = searchNode(data.getClass());
		return (R) io.cast(data, target);
	}
	
	/**
	 * 为指定类型寻找一个合适的处理器
	 * @param type 指定类型
	 * @throws NullPointerException 如果没有合适的处理器
	 */
	private static IDataIO searchNode(Class type) {
		for (IndexNode index : INDEXS) {
			List<IDataIO<?>> list = index.getList();
			for (IDataIO<?> io : list) {
				if (io.match(type)) return io;
			}
		}
		throw new NullPointerException("没有找到合适的处理器：" + type.getName());
	}
	
	private static final class IndexNode implements Comparable<IndexNode> {
		
		/** 优先级 */
		final int priority;
		/** 真实下标 */
		final int index;
		
		IndexNode(int priority) {
			index = INDEXS.size();
			this.priority = priority;
			NODES.add(new LinkedList<>());
		}
		
		public List<IDataIO<?>> getList() {
			return NODES.get(index);
		}
		
		@Override
		public int compareTo(IndexNode o) {
			return Integer.compare(priority, o.priority);
		}
		
	}
	
}