package minedreams.mi.api.electricity.info;

import java.util.Collection;
import java.util.List;

import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IEleOutputer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import net.minecraft.tileentity.TileEntity;

/**
 * 存储线路计算信息.<br>
 *
 * <pre><b> 内部数据条约：
 *  1.当{@code output != null} 时所有数据必须都不为null
 *  2.当{@code output != null} 时所有数据必须正确
 *  3.当{@code output == null} 时不要求其它数据格式、内容正确</pre>
 * </b>
 * @author EmptyDreams
 * @version V1.0
 */
public class PathInfo {
	
	/** 运输过程损耗的能量 */
	private int lossEnergy;
	/** 实际提供的电能 */
	private int energy;
	/** 实际提供的电压 */
	private IVoltage voltage;
	/** 路径 */
	private List<TileEntity> path;
	/** 输出电能的方块 */
	private TileEntity output;
	/** 输出电能方块的托管 */
	private IEleOutputer outputer;
	
	public PathInfo() { }
	
	public PathInfo(int lossEnergy, int energy, IVoltage voltage, List<? extends TileEntity> path,
	                TileEntity output, IEleOutputer outputer) {
		this.lossEnergy = lossEnergy;
		this.energy = energy;
		this.voltage = voltage;
		this.path = (List<TileEntity>) path;
		this.output = output;
		this.outputer = outputer;
	}
	
	/**
	 * 运行缓存中的机器
	 * @param user 需要电能的方块
	 * @param inputer 输入电能的托管
	 * @return 返回用电详单
	 */
	public final UseOfInfo invoke(TileEntity user, IEleInputer inputer) {
		UseOfInfo real = outputer.output(output, energy + lossEnergy, voltage, false);
		inputer.input(user, Math.min(real.getEnergy(), energy), real.getVoltage());
		return real;
	}
	
	/**
	 * 将目标信息合并到当前信息中
	 * @param info 目标信息
	 * @return 是否合并成功，当两者信息都有效时无法自动合并
	 */
	public boolean merge(PathInfo info) {
		lossEnergy += info.lossEnergy;
		path.addAll(info.path);
		if (output == null) {
			output = info.output;
			outputer = info.outputer;
			voltage = info.voltage;
			energy = info.energy;
			return true;
		} else {
			return info.output == null;
		}
	}
	
	public TileEntity getStart() { return path.get(0); }
	
	public TileEntity getEnd() { return path.get(path.size() - 1); }
	
	public int getLossEnergy() {
		return lossEnergy;
	}
	
	public PathInfo setLossEnergy(int lossEnergy) {
		this.lossEnergy = lossEnergy;
		return this;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public PathInfo setEnergy(int energy) {
		this.energy = energy;
		return this;
	}
	
	public IVoltage getVoltage() {
		return voltage;
	}
	
	public PathInfo setVoltage(IVoltage voltage) {
		this.voltage = voltage;
		return this;
	}
	
	public List<TileEntity> getPath() {
		return path;
	}
	
	public TileEntity getOutput() {
		return output;
	}
	
	public PathInfo setOutput(TileEntity output) {
		this.output = output;
		return this;
	}
	
	public IEleOutputer getOutputer() {
		return outputer;
	}
	
	public PathInfo setOutputer(IEleOutputer outputer) {
		this.outputer = outputer;
		return this;
	}
}
