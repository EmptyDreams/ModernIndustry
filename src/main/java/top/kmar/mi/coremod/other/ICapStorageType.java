package top.kmar.mi.coremod.other;

/**
 * 用于获取Cap中的Storage支持的类型
 * @author EmptyDreams
 */
public interface ICapStorageType {
	
	/**
	 * 获取Cap中的Storage支持的类型
	 * @return
	 */
	Class<?> getStorageType();
	
	/**
	 * 设置Cap中Storage支持的类型
	 * @throws IllegalStateException 若重复调用该方法
	 */
	void setStorageType(Class<?> type);
	
}