package minedreams.mi.api.craftguide;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.item.Item;

/**
 * 通用合成表的管理器，用于存储、管理合成表
 * @author EmptyDremas
 * @version V1.0
 */
public final class CraftGuideManager {

	/** 压缩机合成表 */
	public static final CraftGuideManager Compressor = new CraftGuideManager();
	
	private final ArrayList<CraftGuide> LISTS;
	
	/**
	 * 使用默认大小：10
	 * @see #CraftGuideManager(int)
	 */
	public CraftGuideManager() {
		this(10);
	}
	
	/**
	 * @param capacity 合成表初始数量，虽然大小仍然可以扩充，但是如果已知数量建议规定大小，
	 * 						这样做可以节省内存以及时间，原理请查看ArrayList的存储机制
	 */
	public CraftGuideManager(int capacity) {
		LISTS = new ArrayList<>(capacity);
	}
	
	/** @see java.util.ArrayList#forEach(Consumer) */
	public void forEach(Consumer<? super CraftGuide> action) {
		LISTS.forEach(action);
	}
	
	/** @see java.util.ArrayList#size() */
	public int size() {
		return LISTS.size();
	}
	
	/** 根据下标获取合成表 */
	public CraftGuide get(int index) {
		return LISTS.get(index);
	}
	
	/** 判断是否含有某个合成表 */
	public boolean contains(CraftGuide cg) {
		return LISTS.contains(cg);
	}
	
	/** @see #delete(int) */
	public CraftGuideManager remove(int index) {
		return delete(index);
	}
	
	/** 根据下标删除合成表 */
	public CraftGuideManager delete(int index) {
		LISTS.remove(index);
		return this;
	}
	
	/** @see #delete(CraftGuide) */
	public CraftGuideManager remove(CraftGuide cg) {
		return delete(cg);
	}
	
	/**
	 * 删除一个合成表
	 * @param cg 要删除的合成表
	 */
	public CraftGuideManager delete(CraftGuide cg) {
		LISTS.remove(cg);
		return this;
	}
	
	/**
	 * 通过原料获取合成表，忽略工作时间
	 * @param in 原料
	 * @return 如果原料不存在则返回null
	 */
	public CraftGuide getCraftGuide(CraftGuideItems in) {
		if (in == null) return null;
		for (CraftGuide cg : LISTS) {
			if (cg.getItems().equals(in)) {
				return cg;
			}
		}
		return null;
	}
	
	/**
	 * 查询原料中是否含有某个物品
	 */
	public boolean findMeterial(Item item) {
		if (item == null) return false;
		for (CraftGuide cg : LISTS) {
			if (cg.getItems().hasItem(item))
				return true;
		}
		return false;
	}
	
	/** 查找原料是否在合成列表中，忽略工作时间和产物 */
	public boolean findMeterial(CraftGuideItems in) {
		return getCraftGuide(in) != null;
	}
	
	/** 查找合成表是否在列表中，忽略工作时间 */
	public boolean find(CraftGuide cg) {
		if (cg == null) return false;
		for (CraftGuide cgg : LISTS) {
			if (cgg.getItems().equals(cg.getItems()) && cgg.getOuts().equals(cg.getOuts()))
					return true;
		}
		return false;
	}
	
	/**
	 * 添加一个合成表
	 * @param cg 要添加的合成表，如果cg为null则不会做任何事情
	 */
	public CraftGuideManager add(CraftGuide cg) {
		if (cg == null) return this;
		LISTS.add(cg);
		return this;
	}
	
}
