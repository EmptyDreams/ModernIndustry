package minedreams.mi.api.craftguide;

import java.util.Objects;

import net.minecraft.item.ItemStack;

/**
 * 通用合成表<br>
 * 主要用于存储机器的合成表，也可以用来存储其它合成表
 * @author EmptyDremas
 * @version V1.0
 */
public final class CraftGuide {

	/** 存储需要的合称为物品 */
	private final CraftGuideItems items = new CraftGuideItems();
	/** 存储输出物品 */
	private final CraftGuideItems outs = new CraftGuideItems();
	/** 存储工作时间 */
	private final int WORK_TIME;
	
	/** 缺省构造函数 */
	public CraftGuide() {
		this(0);
	}
	
	/**
	 * @param time 合成物品需要的时间，不需要时间为0
	 */
	public CraftGuide(int time) {
		WORK_TIME = time;
	}
	
	public CraftGuideItems getOuts() {
		return outs;
	}
	
	public CraftGuideItems getItems() {
		return items;
	}
	
	/** 复制指定合成表的产品，不覆盖原有产品 */
	public CraftGuide addProduct(CraftGuide cg) {
		if (cg == null) return this;
		items.add(cg.items);
		return this;
	}
	
	/** 添加一个产物，如果stack为null则不做任何事情 */
	public CraftGuide addProduct(ItemStack stack) {
		if (stack == null) return this;
		outs.add(stack.getItem(), stack.getCount());
		return this;
	}
	
	/** 添加一个原料，如果stack为null则不做任何事情 */
	public CraftGuide addMeterial(ItemStack stack) {
		if (stack == null) return this;
		items.add(stack.getItem(), stack.getCount());
		return this;
	}
	
	/**
	 * 获取需要的工作时间
	 */
	public int getTime() {
		return WORK_TIME;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CraftGuide) {
			CraftGuide c = (CraftGuide) obj;
			return (WORK_TIME == c.WORK_TIME) &&  c.outs.equals(outs) && c.items.equals(items);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return items.toString() + outs.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(items, outs, WORK_TIME);
	}
	
}
