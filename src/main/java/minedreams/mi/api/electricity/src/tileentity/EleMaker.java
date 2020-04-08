package minedreams.mi.api.electricity.src.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minedreams.mi.api.electricity.src.info.EnumVoltage;
import minedreams.mi.api.electricity.info.OutPutResult;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.register.te.AutoTileEntity;
import minedreams.mi.utils.BlockPosUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

/**
 * 电力制造者(发电机类)的父级TE
 * @author EmptyDremas
 * @version V1.0
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY_MAKER")
public abstract class EleMaker extends Electricity implements ITickable {

	/** 电能储备 */
	protected int meBox = 0;
	/** 最大电能储备 */
	protected int meBoxMax = 10000;
	/** 最小输出电压 */
	protected IVoltage voltage_min = EnumVoltage.ORDINARY;
	/** 最大输出电压 */
	protected IVoltage voltage_max = EnumVoltage.HIGHER;
	/** 存储该发电机周围连接的方块 */
	protected final Map<EnumFacing, Electricity> LINKS = new HashMap<>(6);
	/** 最大电能瞬时输出 */
	private int outputMax = meBoxMax;
	/** 当前已输出电能 */
	private int output = 0;
	/** 连接指定方块 */
	public void link(Electricity te) {
		link(te, BlockPosUtil.whatFacing(pos, te.getPos()));
	}
	/** 连接指定方块 */
	public void link(Electricity te, EnumFacing facing) {
		LINKS.put(facing, te);
	}
	
	@Override
	public void update() {
		if (meBox < meBoxMax) {
			input(meBoxMax - meBox);
		}
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
	 * 输出电力. 此方法仅负责向外输出电力，发电等功能需放入{@link #input(int)}方法中，
	 * 在方法运行时，若返回结果为{@link OutPutResult#NOT_ENOUGH}，用户应将ee中的能量需求数据更新
	 * @param energy 需要的电力
	 * @param voltage 需要的电压
	 * @param isTrue 当isTrue为false时该方法不修改实际数据
	 * @return OutPutResult 返回输出情况
	 */
	public OutPutResult output(int energy, IVoltage voltage, boolean isTrue) {
		if (isTrue) {
			if (!(voltage.getVoltage() >= voltage_min.getVoltage() &&
					      voltage.getVoltage() <= voltage_max.getVoltage())) return OutPutResult.FAILURE;
			int out = getOutputMax();
			if (out >= energy) {
				meBox -= energy;
				output += energy;
				return OutPutResult.YES;
			} else if (out > 0) {
				meBox = 0;
				output = outputMax;
				return OutPutResult.NOT_ENOUGH;
			} else {
				return OutPutResult.FAILURE;
			}
		} else {
			if (!(voltage.getVoltage() >= voltage_min.getVoltage() &&
					      voltage.getVoltage() <= voltage_max.getVoltage())) return OutPutResult.FAILURE;
			int out = getOutputMax();
			if (out >= energy) {
				return OutPutResult.YES;
			} else if (out > 0) {
				return OutPutResult.NOT_ENOUGH;
			} else {
				return OutPutResult.FAILURE;
			}
		}
	}
	
	/**
	 * 请求发电. 该方法中需要处理UI界面中的物品更新、电能储备量更新等任务，
	 * 在附近有设备请求电能时，首先调用{@link #output(int, IVoltage, boolean)}方法，
	 * 若返回{@link OutPutResult#NOT_ENOUGH}或{@link OutPutResult#FAILURE}
	 * 时会尝试调用该方法
	 *
	 * @param ee 需求量
	 */
	abstract public void input(int ee);
	
	/**
	 * 检查需要输出的电压是否可以输出，当电压不满足但是可以输出时同样返回false
	 */
	public final boolean checkOutput(IVoltage voltage) {
		return voltage.getVoltage() >= getVoltage_min().getVoltage() &&
				       voltage.getVoltage() <= getVoltage_max().getVoltage();
	}
	
	/** 获取最小输出电压 */
	public final IVoltage getVoltage_min() { return voltage_min; }
	/**
	 * 设置最小输出电压
	 * @throws NullPointerException 如果 voltage_min == null
	 */
	public final void setVoltage_min(IVoltage voltage_min) {
		WaitList.checkNull(voltage_min, "voltage_min");
		this.voltage_min = voltage_min;
	}
	/** 获取最大输出电压 */
	public final IVoltage getVoltage_max() {return voltage_max; }
	/**
	 * 设置最大输出电压
	 * @throws NullPointerException 如果 voltage_min == null
	 */
	public final void setVoltage_max(IVoltage voltage_max) {
		WaitList.checkNull(voltage_max, "voltage_max");
		this.voltage_max = voltage_max;
	}
	public final void setMeBoxMax(int max) { meBoxMax = max; }
	public final int getMeBoxMax() { return meBoxMax; }
	/** 获取已储备电能值 */
	public final int getMeBox() { return meBox; }
	/** 获取当前可输出最大值 */
	public final int getOutputMax() { return Math.min(outputMax - output, meBox - output); }
	/** 设置可输出最大值 */
	public final void setOutputMax(int max) { outputMax = max; }
	/** 获取已输出的电能 */
	public final int getOutput() { return output; }
	/** 设置当前已储存的电能 */
	public final void setMeBox(int me) { meBox = me; }
	
}
