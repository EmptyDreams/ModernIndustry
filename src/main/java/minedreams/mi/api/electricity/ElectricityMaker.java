package minedreams.mi.api.electricity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minedreams.mi.api.electricity.info.ElectricityEnergy;
import minedreams.mi.blocks.te.AutoTileEntity;
import minedreams.mi.tools.Tools;
import net.minecraft.util.EnumFacing;

/**
 * 电力制造者(发电机类)的父级TE
 * @author EmptyDremas
 * @version V1.0
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY_MAKER")
public abstract class ElectricityMaker extends Electricity implements IEleOutput {

	/** 电能储备 */
	protected int meBox = 0;
	/** 最大电能储备 */
	protected int meBoxMax = 10000;
	/** 最小输出电压 */
	protected int voltage_min = 90;
	/** 最大输出电压 */
	protected int voltage_max = 100;
	/** 存储该发电机周围连接的方块 */
	protected final Map<EnumFacing, Electricity> LINKS = new HashMap<>(6);
	
	/** 连接指定方块 */
	public void link(Electricity te) {
		link(te, Tools.whatFacing(pos, te.getPos()));
	}
	
	/** 连接指定方块 */
	public void link(Electricity te, EnumFacing facing) {
		LINKS.put(facing, te);
	}
	
	/**
	 * 获取发电机连接的方块列表，列表中不会包含空指针，
	 * 用户可以随意更改列表，修改列表不会影响到内部数据
	 */
	public List<Electricity> getLinks() {
		List<Electricity> list = new ArrayList<>(LINKS.size());
		for (Electricity e : LINKS.values())
			if (e != null) list.add(e);
		return list;
	}
	
	/**
	 * 输出电力，此方法仅负责向外输出电力，发电等功能需放入{@link #input(int)}方法中，
	 * 在方法运行时，若返回结果为{@link OutPutResult#NOT_ENOUGH}，用户应将ee中的能量需求数据更新
	 * @param e 需要的电力
	 * @param isTrue 当isTrue为假时该方法不修改实际数据
	 * @return OutPutResult 返回输出情况
	 */
	public OutPutResult output(Energy e, boolean isTrue) {
		if (isTrue) {
			if (!ElectricityEnergy.isEquals(e.voltage, voltage_min, voltage_max)) return OutPutResult.FAILURE;
			if (me <= 0) return OutPutResult.FAILURE;
			if (me >= e.me) {
				me -= e.me;
				e.me = 0;
				return OutPutResult.YES;
			} else {
				e.me -= me;
				me = 0;
				return OutPutResult.NOT_ENOUGH;
			}
		} else {
			if (!ElectricityEnergy.isEquals(e.voltage, voltage_min, voltage_max)) return OutPutResult.FAILURE;
			if (me <= 0) return OutPutResult.FAILURE;
			if (me >= e.me) {
				return OutPutResult.YES;
			} else {
				return OutPutResult.NOT_ENOUGH;
			}
		}
	}
	
	/** 获取已储备电能值 */
	public int getMeBox() { return meBox; }
	
	/**
	 * 请求发电，该方法中需要处理UI界面中的物品更新、电能储备量更新等任务，
	 * 在附近有设备请求电能时，首先调用{@link #output(Energy, boolean)}方法，
	 * 若返回{@link OutPutResult#NOT_ENOUGH}或{@link OutPutResult#FAILURE}
	 * 时会尝试调用该方法
	 * @param ee 需求量
	 * @return 是否发电成功
	 */
	abstract public boolean input(int ee);
	
	/** 检查需要输出的电能是否可以输出 */
	public boolean checkOutput(ElectricityTransfer.EETransfer ee) {
		return ElectricityEnergy.isEquals(ee.VOLTAGE, getMinVoltage(), getMaxVoltage());
	}
	
	/** 获取最小输出电压 */
	public int getVoltage_min() { return voltage_min; }
	/** 设置最小输出电压 */
	public void setVoltage_min(int voltage_min) { this.voltage_min = voltage_min; }
	/** 获取最大输出电压 */
	public int getVoltage_max() { return voltage_max; }
	/** 设置最大输出电压 */
	public void setVoltage_max(int voltage_max) { this.voltage_max = voltage_max; }
	
	public static class Energy {
		public double me;
		public int voltage;
	}
	
	/**
	 * 保存电力输出信息
	 */
	public enum OutPutResult {
		
		/** 完全成功，所需电力完全输出 */
		YES,
		/** 完全失败，没有任何电力输出 */
		FAILURE,
		/** 部分失败，只输出了部分电力 */
		NOT_ENOUGH
		
	}
	
}
