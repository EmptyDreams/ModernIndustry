package minedreams.mi.api.craftguide;

/**
 * 单个合成表
 * @param <T> 用来表示表格内存储的元素类型
 * @param <E> 用来表示产物的元素类型
 * @author EmptyDreams
 * @version V1.0
 */
abstract public class GuideList<T, E> {
	
	/**
	 * 判断表格中是否包含该元素
	 * @param item 指定元素
	 * @return true表示包含
	 */
	abstract public boolean contains(T item);
	
	/**
	 * 判断表格中是否包含指定迭代器中的所有元素
	 * @param items 迭代器
	 * @return true表示全部包含
	 */
	public boolean contains(Iterable<T> items) {
		for (T item : items) {
			if (!contains(item)) return false;
		}
		return true;
	}
	
	/**
	 * 添加一个元素到合成表
	 * @param x X轴坐标，由0开始
	 * @param y Y轴坐标，由0开始
	 * @param item 物品
	 * @return 是否添加成功
	 */
	public boolean add(int x, int y, T item) { return false; }
	
	/**
	 * 移除指定位置的元素.<br>
	 * 用户也可以选择不支持移除功能，只要不进行任何操作即可。
	 * @param x X轴坐标，由0开始
	 * @param y Y轴坐标，由0开始
	 * @return 被移除的元素，若移除失败或指定位置没有元素则返回null
	 * @throws IndexOutOfBoundsException 如果坐标超出范围
	 */
	abstract public T remove(int x, int y);
	
	/**
	 * 移除指定元素.<br>
	 * 用户也可以选择不支持移除功能，只要不进行任何操作即可。<br>
	 * 当表格内有重复的元素时，依据max确定移除多少元素
	 * @param item 指定元素
	 * @param max 移除元素数量，当max == 0时不发生任何事情
	 * @return 是否移除成功，若移除失败或指定位置没有元素则返回false
	 * @throws IllegalArgumentException 若输入的max < 0
	 */
	abstract public boolean remove(T item, int max);
	
	/** 获取产物 */
	abstract public E get();
	
	/**
	 * 设置产物
	 * @param e 指定产物
	 * @return 是否设置成功
	 */
	abstract public boolean set(E e);
	
	/** 判断合成表是否有序 */
	abstract public boolean isOrderly();
	
	@Override
	abstract public String toString();
	
	/** 产物不参与hashCode的计算 */
	@Override
	abstract public int hashCode();
	
	/** 判断是否相等时产物不应该参与计算 */
	@Override
	abstract public boolean equals(Object o);
	
}
