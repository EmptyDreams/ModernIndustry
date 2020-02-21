package minedreams.mi.api.electricity.cache;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.info.EnumVoltage;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.tools.Tools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * <p>存储一条电缆线路的缓存信息. 该类只支持离线存储部分数据，
 * 所有数据在游戏退出后大部分清除，但在运行时不会从内存中删除。
 * 在线路合并或拆分时会导致数据重置（除发电机数量外）。
 *
 * @author EmptyDreams
 * @version V2.0
 */
public final class WireLinkInfo {
	
	private static final List<BlockPos> NON = new ArrayList<>(0);
	
	/** 含有发电机的数量 */
	private int makerAmount = 0;
	/** 是否需要保存 */
	private boolean isNeedSave = true;
	
	public void setIsNeedSave(boolean isNeedSave) { this.isNeedSave = isNeedSave; }
	public boolean isNeedSave() { return isNeedSave; }
	
	/** 向缓存写入发电机数量 */
	public void setMakerAmount(int makerAmount) {
		this.makerAmount = makerAmount;
	}
	/** 读取发电机数量 */
	public int readMakerAmount() { return makerAmount; }
	/** 判断电路是否含有发电机 */
	public boolean hasMaker() { return makerAmount != 0; }
	/** 增加发电机数量 */
	public void plusMakerAmount(int amount) { makerAmount += amount; }
	
	/**
	 * 是否可用
	 * 当{@link #updateInfo(World)}方法未调用时不能保证
	 * 同一线路使用同一个该类对象，所以通过该方法判定是否可以使用缓存
	 */
	public boolean canUse() { return ets == null; }
	
	public void writeToNBT(NBTTagCompound compound, ElectricityTransfer et) {
		compound.setInteger("cache_makers_size", makerAmount);
		AtomicInteger i = new AtomicInteger(0);
		et.forEachAll(it -> {
			Tools.writeBlockPos(compound, it.getPos(), "cache_transfer_" + i.get());
			i.set(i.get() + 1);
			return true;
		});
		compound.setInteger("cache_transfer_size", i.get());
	}
	
	private List<BlockPos> ets = NON;
	
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("cache_makers_size")) {
			ets = new ArrayList<>();
			makerAmount = compound.getInteger("cache_makers_size");
			int size = compound.getInteger("cache_transfer_size");
			ets = new ArrayList<>(size);
			for (int i = 0; i < size; ++i) {
				ets.add(Tools.readBlockPos(compound, "cache_transfer_" + i));
			}
		}
	}
	
	/**
	 * 更新内部信息，在更新内部信息前线路中的电线不共享同一个缓存对象
	 * @param world 所在世界
	 */
	public void updateInfo(World world) {
		if (ets == null) return;
		ets.forEach(it -> ((ElectricityTransfer) world.getTileEntity(it)).setCache(this));
		ets = null;
	}
	
	/**
	 * 写入缓存数据，当{@link #canUse()}返回false时不会真正写入数据
	 * @param start 起点
	 * @param end 终点
	 * @param energy 损耗能量
	 * @param voltage 电压
	 *
	 * @throws NullPointerException 如果start,end,voltage中任意一值为null
	 * @throws IllegalArgumentException 如果energy < 0
	 */
	public void writeInfo(ElectricityTransfer start, ElectricityTransfer end, int energy, EnumVoltage voltage) {
		if (!canUse()) return;
		WaitList.checkNull(start, "start");
		WaitList.checkNull(end, "end");
		WaitList.checkNull(voltage, "voltage");
		if (energy < 0) throw new IllegalArgumentException("energy[" + energy + "] < 0");
		
		CACHE.add(new Information(start, end, energy, voltage));
	}
	
	/**
	 * 获取路径损耗的能量，当{@link #canUse()}返回false时无法读取缓存数据只能重新计算，
	 * 若没有读取到缓存会计算后写入缓存并返回计算结果
	 * @param start 起点
	 * @param end 终点
	 * @param voltage 电压
	 * @return 损耗的能量，返回-1表示线路是不连接的
	 */
	public int readInfo(ElectricityTransfer start, ElectricityTransfer end, EnumVoltage voltage) {
		for (Information information : CACHE) {
			if (information.voltage == voltage &&
				information.start.equals(start) &&
				information.end.equals(end)) return information.energy;
		}
		int c = calculateLoss(start, end, voltage);
		if (c == -1) return -1;
		writeInfo(start, end, c, voltage);
		return c;
	}
	
	/**
	 * 计算线路损耗电能
	 * @param start 起点
	 * @param end 终点
	 * @param voltage 电压
	 * @return 损耗电能值，返回-1表示计算失败
	 */
	private static int calculateLoss(ElectricityTransfer start, ElectricityTransfer end, EnumVoltage voltage) {
		AtomicInteger loss = new AtomicInteger(0);
		AtomicBoolean has = new AtomicBoolean(false);
		if (start.getLinkAmount() == 1) {
			start.forEach(null, it -> {
				loss.set(loss.get() + it.getLoss(voltage));
				if (it.equals(end)) {
					has.set(true);
					return false;
				}
				return true;
			});
		} else {
			AtomicInteger cache0 = new AtomicInteger(0);
			AtomicBoolean cache1 = new AtomicBoolean(false);
			start.forEach(start.getPrev(), it -> {
				loss.set(loss.get() + it.getLoss(voltage));
				if (it.equals(end)) {
					has.set(true);
					return false;
				}
				return true;
			});
			start.forEach(start.getNext(), it -> {
				cache0.set(cache0.get() + it.getLoss(voltage));
				if (it.equals(end)) {
					cache1.set(true);
					return false;
				}
				return true;
			});
			if (cache1.get()) {
				if (has.get()) {
					if (cache0.get() < loss.get()) {
						loss.set(cache0.get());
					}
				} else {
					loss.set(cache0.get());
					has.set(true);
				}
			}
		}
		if (has.get()) return loss.get();
		else return -1;
	}
	
	/**
	 * 重新计算线路缓存信息
	 * @param transfer 线路中任意一根电线
	 */
	public static void calculateCache(ElectricityTransfer transfer) {
		WireLinkInfo linkInfo = new WireLinkInfo();
		transfer.forEachAll(it -> {
			linkInfo.plusMakerAmount(it.getLinkMaker().size());
			it.setCache(linkInfo);
			return true;
		});
	}
	
	private final Set<Information> CACHE = new HashSet<>();
	
	public static final class Information implements Comparable<Information> {
		
		final ElectricityTransfer start, end;
		final int energy;
		final EnumVoltage voltage;
		
		public Information(ElectricityTransfer start, ElectricityTransfer end, int energy, EnumVoltage voltage) {
			this.start = start;
			this.end = end;
			this.energy = energy;
			this.voltage = voltage;
		}
		
		public ElectricityTransfer getStart() {
			return start;
		}
		public ElectricityTransfer getEnd() {
			return end;
		}
		public int getEnergy() {
			return energy;
		}
		public EnumVoltage getVoltage() {
			return voltage;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			
			Information that = (Information) o;
			
			if (energy != that.energy) return false;
			if (!start.equals(that.start)) return false;
			if (!end.equals(that.end)) return false;
			return voltage == that.voltage;
		}
		
		@Override
		public int hashCode() {
			int result = start.hashCode();
			result = 31 * result + end.hashCode();
			result = 31 * result + energy;
			result = 31 * result + voltage.hashCode();
			return result;
		}
		
		@Override
		public int compareTo(Information o) {
			return Integer.compare(energy, o.energy);
		}
	}
	
}
