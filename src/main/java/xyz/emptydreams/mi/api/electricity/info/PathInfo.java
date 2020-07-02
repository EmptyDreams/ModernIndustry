package xyz.emptydreams.mi.api.electricity.info;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.electricity.EleWorker;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.data.info.EnumVoltage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
	private int lossEnergy = -1;
	/** 实际提供的电压 */
	private IVoltage voltage;
	/** 路径 */
	private List<TileEntity> path = new ArrayList<>();
	/** 输出电能的方块 */
	private BlockPos outer;
	/** 输出电能方块的托管 */
	private IEleOutputer outputer;
	/** 终点 */
	private BlockPos user;
	/** 托管 */
	private IEleInputer inputer;
	/** 所在世界 */
	private World world;
	
	public PathInfo() { }
	
	@SuppressWarnings("unchecked")
	public PathInfo(int lossEnergy, IVoltage voltage, List<? extends TileEntity> path,
	                TileEntity outer, IEleOutputer outputer, TileEntity user, IEleInputer inputer) {
		this.lossEnergy = lossEnergy;
		this.voltage = voltage;
		this.path = (List<TileEntity>) path;
		this.outer = outer.getPos();
		this.outputer = outputer;
		this.user = user.getPos();
		this.inputer = inputer;
		this.world = user.getWorld();
	}
	
	/**
	 * 运行缓存中的机器
	 * @return 返回用电详单
	 */
	public final EleEnergy invoke() {
		int energy = getEnergy();
		if (energy <= 0) return new EleEnergy(0, EnumVoltage.NON);
		EleEnergy real = outputer.output(getOuter(), energy + lossEnergy, voltage, false);
		if (real.getEnergy() <= 0 || real.getVoltage().getVoltage() <= 0) return real;
		int e = inputer.useEnergy(getUser(), real.getEnergy(), real.getVoltage());
		if (e <= 0) {
			outputer.fallback(getOuter(), energy + lossEnergy);
			return new EleEnergy(0, EnumVoltage.NON);
		}
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
	
	public int getEnergy() {
		return Math.min(inputer.getEnergy(getUser()),
				outputer.output(getOuter(), Integer.MAX_VALUE, EnumVoltage.D, true).getEnergy());
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
		return outer == null ? null : world.getTileEntity(outer);
	}
	
	public PathInfo setOuter(TileEntity outer) {
		this.outer = outer.getPos();
		outputer = EleWorker.getOutputer(outer);
		world = outer.getWorld();
		return this;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
	
	public TileEntity getUser() {
		return user == null ? null : world.getTileEntity(user);
	}
	
	public PathInfo setUser(TileEntity user) {
		this.user = user.getPos();
		return this;
	}
	
	public IEleInputer getInputer() {
		return inputer;
	}
	
	public void setInputer(IEleInputer inputer) {
		this.inputer = inputer;
	}
	
	/** 计算线路能量损耗 */
	public void calculateLossEnergy() {
		if (lossEnergy == -1) {
			int energy = getEnergy();
			double result = path.stream().mapToDouble(it ->
					EleWorker.getTransfer(it).getEnergyLoss(it, energy, voltage)).sum();
			if (result > Integer.MAX_VALUE)
				throw new IllegalArgumentException("线缆电能损耗量超过极限值[" + result + "]");
			if (result % 1 != 0) lossEnergy = ((int) result) + 1;
			else lossEnergy = (int) result;
			MISysInfo.print(lossEnergy);
		}
	}

	/** 判断数据是否可用 */
	public boolean isAvailable() {
		TileEntity te = getOuter();
		return te != null && EleWorker.isOutputer(te);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PathInfo)) return false;
		
		PathInfo pathInfo = (PathInfo) o;
		
		if (lossEnergy != pathInfo.lossEnergy) return false;
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
		result = 31 * result + voltage.hashCode();
		result = 31 * result + outer.hashCode();
		result = 31 * result + outputer.hashCode();
		result = 31 * result + user.hashCode();
		result = 31 * result + inputer.hashCode();
		return result;
	}
	
	@Override
	public int compareTo(@Nonnull PathInfo o) {
		if (o.outputer == null) {
			if (outputer == null) return 0;
			return -1;
		}
		if (outputer == null) return 1;
		if (!user.equals(o.user)) return 0;
		int i = Integer.compare(o.lossEnergy, lossEnergy);
		if (i == 0) {
			i = voltage.compareTo(o.voltage);
		}
		return i;
	}
}
