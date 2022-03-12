package top.kmar.mi.data.info;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import top.kmar.mi.content.tileentity.EleSrcCable;
import top.kmar.mi.api.electricity.EleWorker;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.PathInfo;
import top.kmar.mi.api.electricity.info.VoltageRange;
import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.electricity.interfaces.IEleOutputer;
import top.kmar.mi.api.electricity.interfaces.IVoltage;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.utils.container.DoubleWrapper;
import top.kmar.mi.api.utils.container.Wrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 线路的缓存信息
 * @author EmptyDreams
 */
public class CableCache {
	
	/**
	 * 重新计算整条线路的缓存
	 * @param cable 当前电线
	 */
	public static void calculate(EleSrcCable cable) {
		CableCache cache = new CableCache();
		cable.forEachAll((it, isEnd, next) -> {
			BlockPos pos = it.getPos();
			WorldUtil.forEachAroundTE(cable.getWorld(), it.getPos(), (te, facing) -> {
				if (EleWorker.isOutputer(te)) {
					cache.addOutputer(pos, te.getPos());
				}
			});
			it.setCache(cache);
			return true;
		});
	}
	
	/**
	 * 存储发电机列表<br>
	 * key -> 发电机， value -> 连接的电线
	 */
	private final Map<BlockPos, List<BlockPos>> outputers = new HashMap<>();
	/** 存储缓存信息 */
	private final List<Data> datas = new LinkedList<>();
	
	/** 获取线路中发电机数量 */
	public int getOutputerAmount() {
		return outputers.size();
	}
	
	/**
	 * 移除一个发电机
	 * @param cable 发电机连接的电线
	 * @param outer 发电机的坐标
	 */
	public void removeOuter(EleSrcCable cable, BlockPos outer) {
		List<BlockPos> wire = outputers.getOrDefault(outer, null);
		if (wire == null) return;
		if (wire.size() == 1) outputers.remove(outer);
		else wire.remove(cable.getPos());
	}
	
	/**
	 * 添加一个发电机
	 * @param cable 发电机连接的电线
	 * @param outer 发电机
	 */
	public void addOutputer(BlockPos cable, BlockPos outer) {
		List<BlockPos> wire = outputers.computeIfAbsent(outer, pos -> new LinkedList<>());
		wire.add(cable);
	}
	
	/** 合并两个缓存 */
	public void merge(CableCache cache) {
		if (this == cache) return;
		for (Map.Entry<BlockPos, List<BlockPos>> entry : cache.outputers.entrySet()) {
			BlockPos key = entry.getKey();
			List<BlockPos> value = entry.getValue();
			for (BlockPos pos : value) {
				this.addOutputer(pos, key);
			}
		}
		datas.addAll(cache.datas);
	}
	
	/** 最大能量值 */
	private static final Data MAX_DATA = new Data(
					null, null, Double.MAX_VALUE, null, null);
	
	/**
	 * 读取或计算缓存信息，当没有缓存信息时自动计算
	 * @param start 起点
	 * @param user 用电器的TE
	 * @param inputer 用电器的托管
	 * @return 计算结果，当线路中没有可用的发电机时返回null
	 */
	@Nullable
	public PathInfo calculate(EleSrcCable start, TileEntity user, IEleInputer inputer) {
		if (getOutputerAmount() <= 0) return null;
		int energy = inputer.getEnergy(user);
		VoltageRange voltage = inputer.getVoltageRange(user);
		Data min = MAX_DATA;
		TileEntity realOutputer = null;
		for (Map.Entry<BlockPos, List<BlockPos>> entry : outputers.entrySet()) {
			TileEntity outputer = start.getWorld().getTileEntity(entry.getKey());
			IEleOutputer ele = EleWorker.getOutputer(outputer);
			@SuppressWarnings("ConstantConditions")
			EleEnergy eleEnergy = ele.output(outputer, energy, voltage, true);
			if (eleEnergy.getEnergy() <= 0) continue;
			
			Data real = MAX_DATA;
			for (BlockPos link : entry.getValue()) {
				Data data = readInfo(start, link, eleEnergy);
				if (data.energy <= real.energy) real = data;
			}
			if (real.energy <= min.energy) {
				min = real;
				realOutputer = outputer;
			}
		}
		
		if (realOutputer == null) return null;
		int loss = (min.energy % 1 == 0) ? (int) min.energy : ((int) min.energy) + 1;
		return new PathInfo(loss, min.voltage, min.path, realOutputer, user);
	}
	
	/**
	 * 读取或计算缓存信息，当没有缓存信息时自动计算
	 * @param start 起点
	 * @param end 终点
	 * @param energy 需要的电脑
	 * @return 缓存信息
	 */
	@Nonnull
	public Data readInfo(EleSrcCable start, BlockPos end, EleEnergy energy) {
		for (Data data : datas) {
			if (data.start.equals(start.getPos())) {
				if (data.voltage.equals(energy.getVoltage()) && data.end.equals(end)) return data;
			} else if (data.end.equals(end) &&
					data.voltage.equals(energy.getVoltage()) && data.start.equals(end)) {
				return data;
			}
		}
		Wrapper<List<EleSrcCable>> path = new Wrapper<>();
		double loss = getLoss(start, end, energy, path);
		if (loss <= 0)
			 throw new IllegalArgumentException("传入的起点和终点不在一条线路内");
		Data data = new Data(start.getPos(), end, loss, energy.getVoltage(), path.getNullable());
		datas.add(data);
		return data;
	}
	
	/**
	 * 计算线路损耗
	 * @param start 起点
	 * @param end 终点
	 * @param energy 传输的能量
	 * @return 损耗的能量
	 */
	private double getLoss(EleSrcCable start, BlockPos end, EleEnergy energy, Wrapper<List<EleSrcCable>> path) {
		DoubleWrapper loss = new DoubleWrapper();
		if (start.getLinkAmount() <= 1) {
			List<EleSrcCable> real = new LinkedList<>();
			start.forEachAll((now, isEnd, next) -> {
				real.add(now);
				loss.add(now.getLoss(energy));
				return !now.getPos().equals(end);
			});
			path.set(real);
		} else {
			List<EleSrcCable> p1 = new LinkedList<>();
			List<EleSrcCable> p2 = new LinkedList<>();
			double loss1 = getFacingLoss(start, end, start.getNextPos(), energy, p1);
			double loss2 = getFacingLoss(start, end, start.getPrevPos(), energy, p2);
			if (loss1 < 0) {
				loss.set(loss2);
				path.set(p2);
			} else if (loss2 < 0 || loss1 < loss2) {
				loss.set(loss1);
				path.set(p1);
			} else {
				loss.set(loss2);
				path.set(p2);
			}
		}
		return loss.get();
	}
	
	private double getFacingLoss(EleSrcCable start, BlockPos end,
	                             BlockPos prev, EleEnergy energy, List<EleSrcCable> path) {
		DoubleWrapper loss2 = new DoubleWrapper();
		start.forEach(prev, (now, isEnd, next) -> {
			path.add(now);
			loss2.add(-now.getLoss(energy));
			if (end.equals(now.getPos())) {
				loss2.set(-loss2.get());
				return false;
			}
			return true;
		});
		return loss2.get();
	}
	
	/** 缓存信息 */
	private static final class Data {
		/** 起点 */
		final BlockPos start;
		/** 终点 */
		final BlockPos end;
		/** 损耗 */
		final double energy;
		/** 电压 */
		final IVoltage voltage;
		/** 线路 */
		final List<EleSrcCable> path;
		
		Data(BlockPos start, BlockPos end, double energy, IVoltage voltage, List<EleSrcCable> path) {
			this.start = start;
			this.end = end;
			this.energy = energy;
			this.voltage = voltage;
			this.path = path;
		}
		
	}
	
}