package xyz.emptydreams.mi.api.utils.container;

/**
 * 存储double的容器
 * @author EmptyDreams
 */
public final class DoubleWrapper {
	
	private double value;
	
	/** 创建一个包含0的容器 */
	public DoubleWrapper() {
		this(0d);
	}
	
	/** 创建一个指定值的容器 */
	public DoubleWrapper(double v) {
		this.value = v;
	}
	
	/** 获取 */
	public double get() {
		return value;
	}
	/** 设置 */
	public void set(double v) {
		value = v;
	}
	/** 加 */
	public void add(double plus) {
		value += plus;
	}
	/** 设置并获取 */
	public double setAndGet(double v) {
		return (value = v);
	}
	/** 获取并设置 */
	public double getAndSet(double v) {
		double c = value;
		value = v;
		return c;
	}
	/** 加并获取 */
	public double addAndGet(double plus) {
		return (value += plus);
	}
	/** 获取并加 */
	public double getAndAdd(double plus) {
		double c = value;
		value += plus;
		return c;
	}
	/** 转化为int */
	public int intValue() {
		return (int) get();
	}
	/** 装箱 */
	public Wrapper<Double> box() {
		return new Wrapper<>(get());
	}
	
	@Override
	public String toString() {
		return Double.toString(get());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return Double.compare(((DoubleWrapper) o).value, value) == 0;
	}
	
	@Override
	public int hashCode() {
		long temp = Double.doubleToLongBits(value);
		return (int) (temp ^ (temp >>> 32));
	}
}
