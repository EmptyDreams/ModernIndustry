package minedreams.mi.api.electricity;

import minedreams.mi.blocks.te.AutoTileEntity;

/**
 * 所有电力设备的父级TE，其中包含了最为基础的方法和循环接口
 * @author EmptyDremas
 * @version V1.0
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY_USER")
public abstract class ElectricityUser extends Electricity {
	
	/**
	 * 使用电能，该方法在用户设置取电标志后调用。该方法用来处理电器运行时的操作，
	 * 例如：计算工作进度；更新数据等操作。
	 * <b><pre>
	 * 该方法在设置标志后在一下情况不一定被调用：
	 *    1.电力供给不足；
	 *    2.电器运行前因电力供给错误而损坏；
	 *    3.用户(或其他用户)手动跳过了该电器的运行
	 *    4.TE中存储的world对象为null
	 *    5.TE中其它与方块相关的信息错误</pre>
	 *
	 * @param energy 实际输入电能
	 * @param voltage 实际输入电压
	 *
	 * @return boolean 电力是否被消耗，若返回false则表示电器没有消耗电力，将返回电力损耗
	 */
	public abstract boolean useElectricity(int energy, int voltage);
	
	/**
	 * 判断某个电压是否可以在电器上运行
	 */
	public abstract boolean canUse(int voltage);
	
	/**
	 * 判断某个电压及电能是否可以在电器上运行
	 */
	public abstract boolean canUse(int energy, int voltage);
	
	public boolean needEle = false;
	
	/**
	 * 设置取电标记
	 */
	public final void markEle() {
		needEle = true;
	}
	
}
