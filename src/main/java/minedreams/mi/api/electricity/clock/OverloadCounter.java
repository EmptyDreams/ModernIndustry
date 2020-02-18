package minedreams.mi.api.electricity.clock;

import java.util.*;


/**
 * 每一个自{@link minedreams.mi.api.electricity.ElectricityTransfer}和
 * {@link minedreams.mi.api.electricity.ElectricityUser}派生的类都含有该计数器，
 * 该计数器用来记录电子设备超载的时长，单位为tick
 *
 * @author EmptyDreams
 * @version V1.0
 */
public abstract class OverloadCounter {

	private int time = 0;
	
	/** 清零 */
	public void clean() {
		time = 0;
	}
	
	/**
	 * 获取当前计数
	 */
	public int getTime() {
		return time;
	}
	
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
