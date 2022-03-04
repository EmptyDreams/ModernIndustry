package top.kmar.mi.api.electricity.info;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.api.electricity.EleWorker;
import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.electricity.interfaces.IEleOutputer;
import top.kmar.mi.api.electricity.interfaces.IVoltage;
import top.kmar.mi.data.info.EnumVoltage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 */
public class PathInfo implements Comparable<PathInfo> {
	
	/** 运输过程损耗的能量 */
	private int lossEnergy;
	/** 实际提供的电压 */
	private IVoltage voltage;
	/** 路径 */
	private final List<TileEntity> path;
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
	
	public PathInfo(int lossEnergy, IVoltage voltage, List<? extends TileEntity> path,
	                TileEntity outer, TileEntity user) {
		this.lossEnergy = lossEnergy;
		this.voltage = voltage;
		//noinspection unchecked
		this.path = (List<TileEntity>) path;
		this.outer = outer.getPos();
		this.outputer = EleWorker.getOutputer(outer);
		this.user = user.getPos();
		this.inputer = EleWorker.getInputer(user);
		this.world = outer.getWorld();
	}
	
	/**
	 * 运行缓存中的机器
	 * @return 返回用电详单
	 */
	public final EleEnergy invoke() {
		EleEnergy real = getEnergy();
		if (real.getEnergy() <= 0 || real.getVoltage().getVoltage() <= 0) return real;
		int e = inputer.useEnergy(getUser(), real.getEnergy(), real.getVoltage());
		if (e <= 0) throw new IllegalArgumentException("线路数据计算错误：energy=" + e);
		TileEntity transfer;
		for (TileEntity tileEntity : path) {
			transfer = tileEntity;
			//noinspection ConstantConditions
			EleWorker.getTransfer(transfer).transfer(transfer, real.getEnergy(), real.getVoltage(), null);
		}
		return real;
	}
	
	/**
	 * 将目标信息合并到当前信息中
	 * @param info 目标信息
	 * @return 是否合并成功，当两者信息都有效时无法自动合并
	 */
	@SuppressWarnings("unused")
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
	
	/** 获取线路起点的线缆方块的TE */
	@Nonnull
	public TileEntity getStart() { return path.get(0); }
	
	/** 获取线路终点的线缆方块的TE */
	@Nonnull
	public TileEntity getEnd() { return path.get(path.size() - 1); }
	
	/** 获取线缆消耗的能量 */
	public int getLossEnergy() {
		return lossEnergy;
	}
	
	/** 获取机器需要的能量（不包括线缆消耗的能量） */
	public int getMachineEnergy() {
		TileEntity user = getUser();
		return Math.min(inputer.getEnergy(user),
				outputer.output(getOuter(), Integer.MAX_VALUE,
						inputer.getVoltageRange(user), true).getEnergy());
	}
	
	/** 获取线路需要的能量（包括线缆消耗的能量） */
	public EleEnergy getEnergy() {
		int energy = getMachineEnergy();
		if (energy <= 0) return new EleEnergy(0, EnumVoltage.NON);
		calculateLossEnergy();
		return outputer.output(getOuter(), energy + lossEnergy,
				VoltageRange.instance(voltage), false);
	}
	
	/** 获取线路电压 */
	@Nonnull
	public IVoltage getVoltage() {
		return voltage;
	}
	
	/** 设置线路电压 */
	@Nonnull
	public PathInfo setVoltage(IVoltage voltage) {
		this.voltage = voltage;
		return this;
	}
	
	/**
	 * 获取线路内容.<br>
	 *     <b>返回的内容没有经过保护性拷贝，切勿修改！</b>
	 */
	@Nonnull
	public List<TileEntity> getPath() {
		return path;
	}
	
	/**
	 * 获取发电机方块的TE
	 * @return 若没有发电机方块则返回null
	 */
	@Nullable
	public TileEntity getOuter() {
		return outer == null ? null : world.getTileEntity(outer);
	}
	
	/**
	 * 设置发电机方块.
	 * 修改时会一同修改outputer和world，不需要手动调用
	 * {@link #setWorld(World)}
	 * @param outer 发电机方块的TE
	 * @return 当前对象
	 */
	@SuppressWarnings("unused")
	@Nonnull
	public PathInfo setOuter(TileEntity outer) {
		this.outer = outer.getPos();
		outputer = EleWorker.getOutputer(outer);
		world = outer.getWorld();
		return this;
	}
	
	/** 设置缓存对应的世界 */
	public void setWorld(World world) {
		this.world = world;
	}
	
	/** 获取缓存对应的世界 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * 获取用电器方块的TE
	 * @return 若没有用电器方块则返回null
	 */
	@Nullable
	public TileEntity getUser() {
		return user == null ? null : world.getTileEntity(user);
	}
	
	/** 设置用电器方块的TE */
	@Nonnull
	public PathInfo setUser(TileEntity user) {
		this.user = user.getPos();
		this.inputer = EleWorker.getInputer(user);
		return this;
	}
	
	/** 获取用电器方块对应的Inputer */
	@SuppressWarnings("unused")
	public IEleInputer getInputer() {
		return inputer;
	}
	
	/** 计算线路能量损耗 */
	public void calculateLossEnergy() {
		if (lossEnergy == -1) {
			int energy = getMachineEnergy();
			@SuppressWarnings("ConstantConditions")
			double result = path.stream().mapToDouble(it ->
					EleWorker.getTransfer(it).getEnergyLoss(it, energy, voltage)).sum();
			if (result > Integer.MAX_VALUE)
				throw new IllegalArgumentException("线缆电能损耗量超过极限值[" + result + "]");
			if (result % 1 != 0) lossEnergy = ((int) result) + 1;
			else lossEnergy = (int) result;
		}
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