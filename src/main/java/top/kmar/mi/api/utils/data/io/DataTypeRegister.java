package top.kmar.mi.api.utils.data.io;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	 * @param objType 指定类型
	 * @throws NullPointerException 如果没有合适的处理器
	 */
	public static IDataIO searchNode(Class objType) {
		return searchNode(objType, null);
	}
	
	/**
	 * 为指定类型寻找一个合适的处理器
	 * @param objType 指定类型
	 * @param fieldType 类中声明的类型
	 * @throws NullPointerException 如果没有合适的处理器
	 */
	public static IDataIO searchNode(Class objType, Class fieldType) {
		for (IndexNode index : INDEXS) {
			List<IDataIO<?>> list = index.getList();
			for (IDataIO<?> io : list) {
				if (io.match(objType, fieldType)) return io;
			}
		}
		throw new NullPointerException("没有找到合适的处理器：" + objType.getName());
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