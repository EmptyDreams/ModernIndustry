package xyz.emptydreams.mi.api.electricity.info;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import xyz.emptydreams.mi.api.electricity.EleWorker;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
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
public class PathInfo implements Comparable<PathInfo> {
	
	/** 运输过程损耗的能量 */
	private int lossEnergy;
	/** 实际提供的电能 */
	private int energy;
	/** 实际提供的电压 */
	private IVoltage voltage;
	/** 路径 */
	private List<TileEntity> path = new ArrayList<>();
	/** 输出电能的方块 */
	private TileEntity outer;
	/** 输出电能方块的托管 */
	private IEleOutputer outputer;
	/** 终点 */
	private TileEntity user;
	/** 托管 */
	private IEleInputer inputer;
	
	public PathInfo() { }
	
	@SuppressWarnings({"unchecked", "unused"})
	public PathInfo(int lossEnergy, int energy, IVoltage voltage, List<? extends TileEntity> path,
	                TileEntity outer, IEleOutputer outputer, TileEntity user, IEleInputer inputer) {
		this.lossEnergy = lossEnergy;
		this.energy = energy;
		this.voltage = voltage;
		this.path = (List<TileEntity>) path;
		this.outer = outer;
		this.outputer = outputer;
		this.user = user;
		this.inputer = inputer;
	}
	
	/**
	 * 运行缓存中的机器
	 * @return 返回用电详单
	 */
	public final UseInfo invoke() {
		UseInfo real = outputer.output(outer, energy + lossEnergy, voltage, false);
		TileEntity transfer;
		for (TileEntity tileEntity : path) {
			transfer = tileEntity;
			EleWorker.getTransfer(transfer).transfer(transfer, real.getEnergy(), real.getVoltage(), null);
		}
		return real;
	}
	
	/**
	 * 将目标信息合并到当前信息中
	 * @param info 目标信息
	 * @return 是否合并成功，当两者信息都有效时无法自动合并
	 */
	@SuppressWarnings("UnusedReturnValue")
	public boolean merge(PathInfo info) {
		lossEnergy += info.lossEnergy;
		path.addAll(info.path);
		if (outer == null) {
			outer = info.outer;
			outputer = info.outputer;
			voltage = info.voltage;
			energy = info.energy;
			return true;
		} else {
			return info.outer == null;
		}
	}
	
	public TileEntity getStart() { return path.get(0); }
	
	public TileEntity getEnd() { return path.get(path.size() - 1); }
	
	public int getLossEnergy() {
		return lossEnergy;
	}
	
	@SuppressWarnings("unused")
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
	
	public TileEntity getOuter() {
		return outer;
	}
	
	public PathInfo setOuter(TileEntity outer) {
		this.outer = outer;
		return this;
	}
	
	@SuppressWarnings("unused")
	public IEleOutputer getOutputer() {
		return outputer;
	}
	
	public PathInfo setOutputer(IEleOutputer outputer) {
		this.outputer = outputer;
		return this;
	}
	
	public TileEntity getUser() {
		return user;
	}
	
	public PathInfo setUser(TileEntity user) {
		this.user = user;
		return this;
	}
	
	@SuppressWarnings("unused")
	public IEleInputer getInputer() {
		return inputer;
	}
	
	@SuppressWarnings("UnusedReturnValue")
	public PathInfo setInputer(IEleInputer inputer) {
		this.inputer = inputer;
		return this;
	}
	
	/**
	 * 重新计算线路
	 */
	public void calculateLossEnergy() {
		if (lossEnergy <= 0) {
			for (TileEntity entity : path)
				lossEnergy += EleWorker.getTransfer(entity).getEnergyLoss(entity, energy, voltage);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PathInfo)) return false;
		
		PathInfo pathInfo = (PathInfo) o;
		
		if (lossEnergy != pathInfo.lossEnergy) return false;
		if (energy != pathInfo.energy) return false;
		if (!voltage.equals(pathInfo.voltage)) return false;
		if (!outer.equals(pathInfo.outer)) return false;
		if (!outputer.equals(pathInfo.outputer)) return false;
		if (!user.equals(pathInfo.user)) return false;
		if (!getStart().equals(pathInfo.getStart())) {
			if (!getStart().equals(pathInfo.getEnd())) return false;
		}
		if (!getEnd().equals(pathInfo.getEnd())) {
			if (!getEnd().equals(pathInfo.getStart())) return false;
		}
		return inputer.equals(pathInfo.inputer);
	}
	
	@Override
	public int hashCode() {
		int result = lossEnergy;
		result = 31 * result + energy;
		result = 31 * result + voltage.hashCode();
		result = 31 * result + outer.hashCode();
		result = 31 * result + outputer.hashCode();
		result = 31 * result + user.hashCode();
		result = 31 * result + inputer.hashCode();
		return result;
	}
	
	@Override
	public int compareTo(@Nonnull PathInfo o) {
		if (!user.equals(o.user)) return 0;
		if (outputer.isAllowable(outer, inputer.getVoltage(user))) {
			if (o.outputer.isAllowable(o.outer, o.inputer.getVoltage(user))) {
				return Integer.compare(getLossEnergy(), o.getLossEnergy());
			} else {
				return -1;
			}
		} else if (o.outputer.isAllowable(o.outer, o.inputer.getVoltage(user))) {
			return 1;
		} else {
			return Integer.compare(getLossEnergy(), o.getLossEnergy());
		}
	}
}
