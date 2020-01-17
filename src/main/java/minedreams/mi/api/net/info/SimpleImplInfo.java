package minedreams.mi.api.net.info;

import java.util.List;

import io.netty.buffer.ByteBuf;

/**
 * 所有信息管理器的接口，T代表管理的信息类型
 * @author EmptyDremas
 * @version V1.0
 */
public interface SimpleImplInfo<T> {

	/**
	 * 判断是否含有多个信息，方法通过判断getInfos()返回值是否为null判断
	 */
	default boolean hasMultiple() {
		return getInfos() != null;
	}
	
	/**
	 * 获取存储的信息，根据规定，如果需要返回多个信息，此方法应该返回列表中的第一个信息，
	 * 返回多个信息需要使用getInfos()方法
	 */
	T getInfo();
	
	/**
	 * 返回存储的所有信息，如果没有重写，默认返回null
	 */
	default List<T> getInfos() {
		return null;
	}
	
	/**
	 * 存入信息，虽然方法名称为add，但是不一定为添加信息，
	 * 可能为覆盖原有信息，具体操作取决于实现
	 */
	void add(T info);
	
	/** 删除一个信息 */
	void delete(T info);
	
	/** 向buf中写入信息 */
	void writeTo(ByteBuf buf);
	
	/** 从buf中读取信息 */
	void readFrom(ByteBuf buf);
	
}
