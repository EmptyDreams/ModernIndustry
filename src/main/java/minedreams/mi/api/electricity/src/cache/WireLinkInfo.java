package minedreams.mi.api.electricity.src.cache;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import minedreams.mi.api.electricity.EleWorker;
import minedreams.mi.api.electricity.info.EleLineCache;
import minedreams.mi.api.electricity.info.PathInfo;
import minedreams.mi.api.electricity.info.UseInfo;
import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IEleOutputer;
import minedreams.mi.api.electricity.interfaces.IEleTransfer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import minedreams.mi.api.electricity.src.tileentity.EleSrcCable;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.tools.Tools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * <p>存储一条电缆线路的缓存信息. 该类只支持离线存储部分数据，
 * 所有数据在游戏退出后大部分清除，但在运行时不会从内存中删除。
 * 在线路合并或拆分时会导致数据重置（除发电机数量外）。
 *
 * @author EmptyDreams
 * @version V2.1
 */
public final class WireLinkInfo extends EleLineCache {
	
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
	@Override
	public int getOutputerAmount() {
		return makerAmount;
	}
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
	
	public void writeToNBT(@NotNull NBTTagCompound compound, @NotNull EleSrcCable et) {
		compound.setInteger("cache_makers_size", makerAmount);
		AtomicInteger i = new AtomicInteger(0);
		et.forEachAll((it, isEnd, next) -> {
			Tools.writeBlockPos(compound, it.getPos(), "cache_transfer_" + i.get());
			i.set(i.get() + 1);
			return true;
		});
		compound.setInteger("cache_transfer_size", i.get());
	}
	
	private List<BlockPos> ets = NON;
	
	public void readFromNBT(@NotNull NBTTagCompound compound) {
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
		ets.forEach(it -> ((EleSrcCable) world.getTileEntity(it)).setCache(this));
		ets = null;
	}
	
	/**
	 * 获取路径损耗的能量，当{@link #canUse()}返回false时无法读取缓存数据只能重新计算，
	 * 若没有读取到缓存会计算后写入缓存并返回计算结果
	 * @param start 起点
	 * @param user 需求电能的方块
	 * @param inputer 需求电能的方块的托管
	 * @return 若返回null表明没有可用路径
	 */
	@Nullable
	@Override
	public PathInfo readInfo(TileEntity start, TileEntity user, IEleInputer inputer) {
		for (PathInfo info : CACHE) {
			if (info.getStart().equals(start) || info.getEnd().equals(start)) {
				if (info.getUser().equals(user)) {
					return info;
				}
			}
		}
		return null;
	}
	
	/**
	 * 写入缓存数据，当{@link #canUse()}返回false时不会真正写入数据
	 * @param info 线路信息
	 * @throws NullPointerException 如果info == null
	 */
	@Override
	public void writeInfo(PathInfo info) {
		WaitList.checkNull(info, "info");
		CACHE.add(info);
	}
	
	/**
	 * 计算线路信息，不尝试读取缓存
	 * @param start 起点
	 * @param user 请求电能的方块
	 * @param inputer 托管
	 * @return 返回null表示无可用线路
	 */
	public static PathInfo calculate(EleSrcCable start, TileEntity user, IEleInputer inputer) {
		if (start.getLinkAmount() == 1) {
			PathInfo info = new PathInfo();
			calculateHelper(start, null, user, inputer, info);
			if (info.getOuter() == null) return null;
			info.calculateLossEnergy();
			return info;
		} else {
			PathInfo info0 = new PathInfo(), info1 = new PathInfo();
			calculateHelper(start, start.getPrev(), user, inputer, info0);
			calculateHelper(start, start.getNext(), user, inputer, info1);
			
			if (info0.getOuter() == null) {
				if (info1.getOuter() == null) return null;
				return info1;
			} else if (info1.getOuter() == null) return info0;
			
			int needEnergy = inputer.getEnergy(user);
			if (info0.getEnergy() < needEnergy) {
				if (info1.getEnergy() < needEnergy) {
					if (info0.getEnergy() < info1.getEnergy()) {
						info0.calculateLossEnergy();
						return info0;
					} else {
						info1.calculateLossEnergy();
						return info1;
					}
				} else {
					info1.calculateLossEnergy();
					return info1;
				}
			} else if (info1.getEnergy() < needEnergy) {
				info0.calculateLossEnergy();
				return info0;
			} else {
				info0.calculateLossEnergy();
				info1.calculateLossEnergy();
				if (info0.getLossEnergy() < info1.getLossEnergy()) return info0;
				return info1;
			}
		}
	}
	
	private static void calculateHelper(EleSrcCable start, TileEntity prev,
	                                    TileEntity user, IEleInputer inputer, PathInfo info) {
		info.setUser(user).setInputer(inputer);
		
		IEleTransfer transfer = EleWorker.getTransfer(start);
		int energy = inputer.getEnergy(user);
		int minEnergy = inputer.getEnergyMin(user);
		IVoltage voltage = inputer.getVoltage(user);
		start.forEach(prev, (it, isEnd, next) -> {
			info.getPath().add(it);
			if (isEnd) {
				for (Map.Entry<TileEntity, IEleOutputer> entry : transfer.getOutputerAround(it).entrySet()) {
					UseInfo useInfo = entry.getValue().output(entry.getKey(), energy, voltage, true);
					if (useInfo.getEnergy() >= energy) {
						info.setEnergy(energy)
								.setOuter(entry.getKey())
								.setOutputer(entry.getValue())
								.setVoltage(useInfo.getVoltage());
						return false;
					} else if (useInfo.getEnergy() >= minEnergy) {
						if (info.getEnergy() == 0 || info.getEnergy() < useInfo.getEnergy()) {
							info.setEnergy(minEnergy)
									.setOuter(entry.getKey())
									.setOutputer(entry.getValue())
									.setVoltage(useInfo.getVoltage());
						}
					}
				}
				if (next != null) {
					info.merge(EleWorker.getTransfer(next).findPath(next, user, inputer));
				}
				return false;
			} else {
				for (Map.Entry<TileEntity, IEleOutputer> entry : transfer.getOutputerAround(it).entrySet()) {
					UseInfo useInfo = entry.getValue().output(entry.getKey(), energy, voltage, true);
					if (useInfo.getEnergy() >= energy) {
						info.setEnergy(energy)
							.setOuter(entry.getKey())
							.setOutputer(entry.getValue())
							.setVoltage(useInfo.getVoltage());
						return false;
					} else if (useInfo.getEnergy() >= minEnergy) {
						if (info.getEnergy() == 0 || info.getEnergy() < useInfo.getEnergy()) {
							info.setEnergy(minEnergy)
									.setOuter(entry.getKey())
									.setOutputer(entry.getValue())
									.setVoltage(useInfo.getVoltage());
						}
					}
				}
				return true;
			}
		});
	}
	
	/**
	 * 重新计算线路缓存信息
	 * @param transfer 线路中任意一根电线
	 */
	public static void calculateCache(EleSrcCable transfer) {
		WireLinkInfo linkInfo = new WireLinkInfo();
		transfer.forEachAll((it, isEnd, next) -> {
			linkInfo.plusMakerAmount(EleWorker.getTransfer(it).getOutputerAround(it).size());
			it.setCache(linkInfo);
			return true;
		});
	}
	
	private final Set<PathInfo> CACHE = new HashSet<>();
	
}
