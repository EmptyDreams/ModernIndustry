package xyz.emptydreams.mi.api.utils.wrapper;

/**
 * 用于盛放int的容器
 * @author EmptyDreams
 * @version V1.0
 */
public final class IntWrapper {
	
	private int value;
	
	/**
	 * 创建一个包含指定int的容器
	 */
	public IntWrapper(int v) {
		value = v;
	}
	
	/**
	 * 创建一个包含0的容器
	 */
	public IntWrapper() { this(0); }
	
	public int get() { return value; }
	
	public void set(int v) { value = v; }
	
	/** 将容器中的数目加上指定值 */
	public void plus(int k) { value += k; }
	
}
