package top.kmar.mi.api.utils.container;

import javax.annotation.Nullable;

/**
 * 用于盛放对象引用的容器
 * @author EmptyDreams
 */
public final class Wrapper<T> {
	
	private T object;
	
	/**
	 * 创建一个包含指定对象的容器
	 * @param o 指定对象
	 */
	public Wrapper(T o) {
		object = o;
	}
	
	/**
	 * 创建一个包含null的容器
	 */
	public Wrapper() { this(null); }
	
	@Nullable
	public T get() { return object; }
	
	public void set(T o) { object = o; }
	
}