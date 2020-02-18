package minedreams.mi.api.electricity.cache;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import minedreams.mi.api.electricity.ElectricityMaker;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.ElectricityUser;
import minedreams.mi.api.electricity.info.EnumVoltage;

/**
 * 机器缓存，用来存储机器寻找到过的发电机，其中数据离线不存储
 *
 * @author EmptyDreams
 * @version V1.0
 */
public final class MachineCache {
	
	private final ElectricityUser user;
	private final Set<MachineInformation> values = new TreeSet<>();
	
	public MachineCache(ElectricityUser user) { this.user = user; }
	@SuppressWarnings("unused")
	public MachineCache(MachineCache cache) {
		this(cache.user);
		values.addAll(cache.values);
	}
	
	/**
	 * 从缓存中读取数据，数据优先读取电压符合用电器的，
	 * 若缓存中没有相关数据内部自动完成计算。
	 * @return 若没有读取到内容则返回null
	 */
	public MachineInformation read() {
		EnumVoltage voltage = user.getVoltage();
		ElectricityMaker maker = null;
		MachineInformation oldInfo = null;
		List<MachineInformation> removes = new ArrayList<>(values.size() / 2);
		for (MachineInformation info : values) {
			if (info.exist()) {
				if (info.isValid(user)) {
					if (info.isValidVoltage(user)) return info;
					if (maker == null) {
						maker = info.getMaker();
						oldInfo = info;
					}
				}
			} else {
				removes.add(info);
			}
		}
		values.removeAll(removes);
		AtomicReference<ElectricityMaker> nMaker = new AtomicReference<>();
		List<ElectricityTransfer> path = new ArrayList<>();
		findMaker(voltage, nMaker, path);
		if (maker == null) {
			if (nMaker.get() == null) return null;
			MachineInformation info = new MachineInformation(nMaker.get(), path);
			if (nMaker.get() != null) write(info);
			return info;
		} else {
			if (nMaker.get() == null) return oldInfo;
			MachineInformation info = new MachineInformation(nMaker.get(), path);
			if (info.isValidVoltage(user)) {
				write(info);
				return info;
			}
			return oldInfo.compareTo(info) < 0 ? oldInfo : info;
		}
	}
	
	public void write(ElectricityMaker maker, List<ElectricityTransfer> path) {
		values.add(new MachineInformation(maker, path));
	}
	
	public void write(MachineInformation information) {
		values.add(information);
	}
	
	public ElectricityUser getUser() { return user; }
	
	private void findMaker(EnumVoltage voltage, AtomicReference<ElectricityMaker> maker,
	                       List<ElectricityTransfer> path) {
		maker.set(null);
		Collection<ElectricityTransfer> links = user.getLinkedWire().values();
		if (links.isEmpty()) return;
		for (ElectricityTransfer nowStart : links) {
			AtomicInteger loss = new AtomicInteger(0);
			if (nowStart.getLinkAmount() == 1) {
				findMakerHelper(nowStart, null, voltage, maker, path, loss);
			} else {
				findMakerHelper(nowStart, nowStart.getNext(), voltage, maker, path, loss);
				if (maker.get() == null) {
					findMakerHelper(nowStart, nowStart.getPrev(), voltage, maker, path, loss);
				}
			}
			if (maker.get() == null) continue;
			WireLinkInfo linkInfo = nowStart.getCache();
			linkInfo.writeInfo(nowStart, path.get(path.size() - 1), loss.get(), voltage);
			break;
		}
	}
	
	private void findMakerHelper(ElectricityTransfer start, ElectricityTransfer prev, EnumVoltage voltage,
	                             AtomicReference<ElectricityMaker> maker, Collection<ElectricityTransfer> path,
	                             AtomicInteger loss) {
		loss.set(0);
		start.forEach(prev, it -> {
			loss.set(loss.get() + it.getLoss(voltage));
			path.add(it);
			for (ElectricityMaker m : it.getLinkMaker()) {
				if (m.getOutputMax() >= user.getEnergy()) {
					maker.set(m);
					return false;
				} else if (maker.get() == null && m.getOutputMax() >= user.getEnergyMin()) {
					maker.set(m);
				}
			}
			return true;
		});
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return values.equals(o);
	}
	
	@Override
	public int hashCode() {
		return values.hashCode();
	}
	
}
