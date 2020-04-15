package xyz.emptydreams.mi.api.electricity.src.info;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import xyz.emptydreams.mi.api.electricity.EleWorker;
import xyz.emptydreams.mi.api.electricity.info.EleLineCache;
import xyz.emptydreams.mi.api.electricity.info.PathInfo;
import xyz.emptydreams.mi.api.electricity.info.UseInfo;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleTransfer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcCable;
import xyz.emptydreams.mi.api.net.WaitList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * <p>存储一条电缆线路的缓存信息. 该类不支持离线存储数据，
 * 所有数据离线清除，载入方块是重新计算所有数据。</p>
 * 在线路合并或拆分时会导致数据重置（除发电机数量外）。
 *
 * @author EmptyDreams
 * @version V2.1
 */
@SuppressWarnings("unused")
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
	 * 获取路径损耗的能量
	 * @param start 起点
	 * @param user 需求电能的方块
	 * @param inputer 需求电能的方块的托管
	 * @return 若返回null表明没有可用缓存
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
	 * 写入缓存数据
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
		AtomicReference<UseInfo> realUseInfo = new AtomicReference<>();
		AtomicReference<TileEntity> realOut = new AtomicReference<>();
		AtomicReference<IEleOutputer> realOutper = new AtomicReference<>();
		
		IEleTransfer transfer = EleWorker.getTransfer(start);
		int energy = inputer.getEnergy(user);
		IVoltage voltage = inputer.getVoltage(user);
		start.forEach(prev == null ? null : prev.getPos(), (it, isEnd, next) -> {
			info.getPath().add(it);
			if (isEnd) {
				for (Map.Entry<TileEntity, IEleOutputer> entry :
						transfer.getOutputerAround(it).entrySet()) {
					if (entry.getKey() == user) continue;
					UseInfo useInfo = entry.getValue().output(
							entry.getKey(), Integer.MAX_VALUE, voltage, true);
					if (useInfo.getEnergy() >= energy) {
						info.setEnergy(energy)
								.setOuter(entry.getKey())
								.setOutputer(entry.getValue())
								.setVoltage(useInfo.getVoltage());
						return false;
					}
					int k = inputer.getEnergy(user, useInfo.getEnergy());
					if (k > 0 && (realUseInfo.get() == null ||
							              realUseInfo.get().getEnergy() < k)) {
						realUseInfo.set(useInfo.setEnergy(k));
						realOut.set(entry.getKey());
						realOutper.set(entry.getValue());
					}
				}
				if (next != null) {
					info.merge(EleWorker.getTransfer(next).findPath(next, user, inputer));
				}
				return false;
			} else {
				for (Map.Entry<TileEntity, IEleOutputer> entry :
						transfer.getOutputerAround(it).entrySet()) {
					if (entry.getKey() == user) continue;
					UseInfo useInfo = entry.getValue().output(
							entry.getKey(), Integer.MAX_VALUE, voltage, true);
					if (useInfo.getEnergy() >= energy) {
						info.setEnergy(energy)
								.setOuter(entry.getKey())
								.setOutputer(entry.getValue())
								.setVoltage(useInfo.getVoltage());
						return false;
					}
					int k = inputer.getEnergy(user, useInfo.getEnergy());
					if (k > 0 && (realUseInfo.get() == null ||
							              realUseInfo.get().getEnergy() < k)) {
						realUseInfo.set(useInfo.setEnergy(k));
						realOut.set(entry.getKey());
						realOutper.set(entry.getValue());
					}
				}
				return true;
			}
		});
		if (info.getOuter() == null && realUseInfo.get() != null) {
			info.setEnergy(realUseInfo.get().getEnergy())
					.setOuter(realOut.get())
					.setOutputer(realOutper.get())
					.setVoltage(realUseInfo.get().getVoltage());
		}
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
