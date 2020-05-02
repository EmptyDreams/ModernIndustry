package xyz.emptydreams.mi.api.electricity.clock;

import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcCable;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcUser;

/**
 * 每一个自{@link EleSrcCable}和
 * {@link EleSrcUser}派生的类都含有该计数器，
 * 该计数器用来记录电子设备超载的时长，单位为tick
 *
 * @author EmptyDreams
 * @version V1.0
 */
public abstract class OverloadCounter {

	private int time = 0;
	private int maxTime;
	
	public OverloadCounter() { this(0); }
	public OverloadCounter(int maxTime) { this.maxTime = maxTime; }
	
	/** 清零 */
	public void clean() {
		time = 0;
	}
	
	/** 获取当前计数 */
	public int getTime() {
		return time;
	}
	/** 获取最大计数 */
	public int getMaxTime() { return maxTime; }
	/** 设置最大计数 */
	public void setMaxTime(int maxTime) { this.maxTime = maxTime; }
	@SuppressWarnings("unused")
	protected void setTime(int time) { this.time = time; }
	
	/**
	 * 计数增加指定量
	 * @throws IllegalArgumentException 如果 amount < 0
	 */
	public void plus(int amount) {
		if (amount < 0) throw new IllegalArgumentException("amount 应该大于等于 0，而此时为：" + amount);
		time += amount;
	}
	
	/**
	 * 计数增加1
	 */
	public void plus() {
		plus(1);
	}
	
	/**
	 * 当过载时执行此操作
	 */
	abstract public void overload();
	
}
