package top.kmar.mi.api.electricity.info;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.kmar.mi.api.electricity.EleWorker;
import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.electricity.interfaces.IEleOutputer;

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
	private int lossEnergy = -1;
	/** 实际提供的电流 */
	private final EleEnergy energy;
	/** 路径 */
	private final List<TileEntity> path;
	/** 输出电能的方块 */
	private final BlockPos outer;
	/** 输出电能方块的托管 */
	private final IEleOutputer outputer;
	/** 终点 */
	private final BlockPos user;
	/** 托管 */
	private final IEleInputer inputer;
	/** 所在世界 */
	private final World world;
	
	public PathInfo(EleEnergy energy, List<? extends TileEntity> path,
	                TileEntity outer, TileEntity user) {
		this.energy = energy;
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
		EleEnergy real = inputer.useEnergy(getUser(), getEnergy());
		TileEntity transfer;
		for (TileEntity tileEntity : path) {
			transfer = tileEntity;
			//noinspection ConstantConditions
			EleWorker.getTransfer(transfer).transfer(transfer, real);
		}
		return real;
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
		return inputer.getEnergyDemand(getUser());
	}
	
	/** 获取线路需要的能量（包括线缆消耗的能量） */
	public EleEnergy getEnergy() {
		int energy = getMachineEnergy();
		if (energy <= 0) return new EleEnergy(0, EleEnergy.ZERO);
		calculateLossEnergy();
		return outputer.output(getOuter(), energy + lossEnergy, false);
	}
	
	/** 获取线路电压 */
	@Nonnull
	public int getVoltage() {
		return energy.getVoltage();
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
	
	/** 获取用电器方块对应的Inputer */
	@SuppressWarnings("unused")
	public IEleInputer getInputer() {
		return inputer;
	}
	
	/** 计算线路能量损耗 */
	public void calculateLossEnergy() {
		if (lossEnergy == -1) {
			int ans = 0;
			for (TileEntity value : path) {
				//noinspection ConstantConditions
				ans += EleWorker.getTransfer(value).getEnergyLoss(value, energy);
			}
			lossEnergy = ans;
		}
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that) return true;
		if (!(that instanceof PathInfo)) return false;
		
		PathInfo pathInfo = (PathInfo) that;
		
		if (lossEnergy != pathInfo.lossEnergy) return false;
		if (energy != pathInfo.energy) return false;
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
		result = 31 * result + energy.hashCode();
		result = 31 * result + outer.hashCode();
		result = 31 * result + outputer.hashCode();
		result = 31 * result + user.hashCode();
		result = 31 * result + inputer.hashCode();
		return result;
	}
	
	@Override
	public int compareTo(@Nonnull PathInfo that) {
		if (that.outputer == null) {
			if (outputer == null) return 0;
			return -1;
		}
		if (outputer == null) return 1;
		if (!user.equals(that.user)) return 0;
		int result = Integer.compare(that.lossEnergy, lossEnergy);
		if (result == 0) result = energy.compareTo(that.energy);
		return result;
	}
	
}