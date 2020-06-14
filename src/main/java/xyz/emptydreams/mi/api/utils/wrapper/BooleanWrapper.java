package xyz.emptydreams.mi.api.utils.wrapper;

/**
 * 用于盛放boolean的容器
 * @author EmptyDreams
 * @version V1.0
 */
public class BooleanWrapper {
	
	private boolean value;
	
	/**
	 * 创建一个包含指定boolean的容器
	 */
	public BooleanWrapper(boolean v) {
		value = v;
	}
	
	/**
	 * 创建一个包含false的容器
	 */
	public BooleanWrapper() { this(false); }
	
	public boolean get() { return value; }
	
	public void set(boolean v) { value = v; }
	
}
