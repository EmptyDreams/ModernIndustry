package xyz.emptydreams.mi.data.info;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import xyz.emptydreams.mi.api.electricity.EleWorker;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.info.EleLineCache;
import xyz.emptydreams.mi.api.electricity.info.PathInfo;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.utils.wrapper.Wrapper;
import xyz.emptydreams.mi.blocks.te.EleSrcCable;

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
		Iterator<PathInfo> it = CACHE.iterator();
		PathInfo info;
		while(it.hasNext()) {
			info = it.next();
			if (!info.isAvailable()) {
				it.remove();
				continue;
			}
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
		if (start.getLinkAmount() <= 1) {
			PathInfo info = calculateHelper(start, null, user, inputer);
			if (info.getOuter() == null) return null;
			info.calculateLossEnergy();
			return info;
		} else {
			PathInfo info0 = calculateHelper(start, start.getPrev(), user, inputer);
			PathInfo info1 = calculateHelper(start, start.getNext(), user, inputer);
			
			int k = info0.compareTo(info1);
			switch (k) {
				case -1: return info0;
				case  1: return info1;
				case  0:
					if (info0.getOuter() == null) return null;
					return info0;
				default:
					throw new NoSuchElementException("内部计算错误，PathInfo#compareTo(PathInfo)返回了0,-1,1之外的值");
			}
		}
	}
	
	/**
	 * 计算指定导线所在线路的{@link PathInfo}
	 * @param start 起点
	 * @param prev 上一根电线
	 * @param user 用电器的TE
	 * @param inputer 用电器的托管
	 * @return 计算结果
	 */
	private static PathInfo calculateHelper(EleSrcCable start, TileEntity prev,
	                                    TileEntity user, IEleInputer inputer) {
		PathInfo info = new PathInfo();
		info.setUser(user).setInputer(inputer);
		Wrapper<EleEnergy> realUseInfo = new Wrapper<>();
		Wrapper<TileEntity> realOut = new Wrapper<>();
		Wrapper<IEleOutputer> realOutper = new Wrapper<>();
		
		int energy = inputer.getEnergy(user);
		IVoltage voltage = inputer.getVoltage(user, EnumVoltage.ORDINARY);
		start.forEach(prev == null ? null : prev.getPos(), (it, isEnd, next) -> {
			info.getPath().add(it);
			return onceLoop(it, info, inputer, user, energy, voltage, realUseInfo, realOut, realOutper, next, isEnd);
		});
		if (info.getOuter() == null && realUseInfo.get() != null) {
			info.setOuter(realOut.get())
				.setVoltage(realUseInfo.get().getVoltage());
		}
		return info;
	}
	
	/**
	 * 一次循环中所需要做的事情
	 * @param it 当前导线
	 * @param info 已有的线路信息
	 * @param inputer 用电器的托管
	 * @param user 用电器的TE
	 * @param energy 需要的能量
	 * @param voltage 需要的电压
	 * @param realUseInfo 能量信息
	 * @param realOut  计算得到的发电机的TE
	 * @param realOutper 计算得到的发电机的托管
	 * @param next 下一根导线
	 * @param isEnd 是否是最后一根
	 * @return 是否继续循环，返回false表示直接中断
	 */
	private static boolean onceLoop(TileEntity it, PathInfo info,
	                                IEleInputer inputer, TileEntity user, int energy, IVoltage voltage,
	                                Wrapper<EleEnergy> realUseInfo,
	                                Wrapper<TileEntity> realOut,
	                                Wrapper<IEleOutputer> realOutper,
	                                TileEntity next, boolean isEnd) {
		for (Map.Entry<TileEntity, IEleOutputer> entry :
				EleWorker.getTransfer(it).getOutputerAround(it).entrySet()) {
			if (entry.getKey() == user) continue;
			EleEnergy useInfo = entry.getValue().output(
					entry.getKey(), Integer.MAX_VALUE, voltage, true);
			if (useInfo.getEnergy() >= energy) {
				info.setOuter(entry.getKey())
						.setVoltage(useInfo.getVoltage());
				return false;
			}
			if (useInfo.getEnergy() > 0) {
				int k = inputer.getEnergy(user, useInfo.getEnergy());
				if (k > 0 && (realUseInfo.get() == null ||
						              realUseInfo.get().getEnergy() < k)) {
					useInfo.setEnergy(k);
					realUseInfo.set(useInfo);
					realOut.set(entry.getKey());
					realOutper.set(entry.getValue());
				}
			}
		}
		if (isEnd) {
			if (next != null) info.merge(EleWorker.getTransfer(next).findPath(next, user, inputer));
			return false;
		}
		return true;
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
