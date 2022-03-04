package top.kmar.mi.api.utils.container;

/**
 * 用于盛放boolean的容器
 * @author EmptyDreams
 */
public final class BooleanWrapper {
	
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
	
	/** 获取 */
	public boolean get() { return value; }
	/** 设置 */
	public void set(boolean v) { value = v; }
	/** 非 */
	public boolean not() { return !value; }
	/** 设置并获取 */
	public boolean setAndGet(boolean v) { return (value = v); }
	/** 获取并设置 */
	public boolean getAndSet(boolean v) {
		boolean c = value;
		value = v;
		return c;
	}
	/** 非并设置 */
	public void notAndSet() {  value = !value; }
	/** 转化为int */
	public int intValue() { return get() ? 1 : 0; }
	/** 装箱 */
	public Wrapper<Boolean> box() {
		return new Wrapper<>(get());
	}
	
	@Override
	public String toString() {
		return Boolean.toString(get());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return value == ((BooleanWrapper) o).value;
	}
	
	@Override
	public int hashCode() {
		return intValue();
	}
}