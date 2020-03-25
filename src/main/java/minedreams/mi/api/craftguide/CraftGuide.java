package minedreams.mi.api.craftguide;

import java.util.HashSet;
import java.util.Set;

/**
 * 通用合成表
 * @param <E> 产物类型
 * @author EmptyDreams
 * @version V1.0
 */
public class CraftGuide<T, E> {
	
	private Set<GuideList<T, E>> guides;
	
	public CraftGuide(int size) {
		guides = new HashSet<>(size);
	}
	
	public CraftGuide() { this(10); }
	
	/**
	 * 判断列表中是否包含指定合成表
	 * @param list 指定合成表
	 */
	public boolean contains(GuideList<T, E> list) {
		return guides.contains(list);
	}
	
	public boolean contains(T item) {
		for (GuideList<T, E> guide : guides) {
			if (guide.contains(item)) return true;
		}
		return false;
	}
	
	/**
	 * 获取指定合成表的产物.<br>
	 * 输入的合成表的产物不影响运算结果，这个方法的存在意义就是
	 * 当用户只知道原料时通过该方法获取产物
	 * @param list 合成表
	 * @return 若没有找到注册的合成表则返回null，否则返回产物
	 */
	public E get(GuideList<T, E> list) {
		for (GuideList<T, E> guide : guides) {
			if (guide.equals(list)) return guide.get();
		}
		return null;
	}
	
	/**
	 * 向列表注册一个合成表
	 * @param list 合成表
	 * @return 是否注册成功
	 */
	public boolean register(GuideList<T, E> list) {
		return guides.add(list);
	}
	
	/**
	 * 取消指定合成表的注册
	 * @param list 合成表
	 * @return 是否取消成功
	 */
	public boolean unregister(GuideList<T, E> list) {
		return guides.remove(list);
	}
	
}
