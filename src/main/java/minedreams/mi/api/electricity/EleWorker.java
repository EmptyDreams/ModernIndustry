package minedreams.mi.api.electricity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import minedreams.mi.api.electricity.cache.MachineCache;
import minedreams.mi.api.electricity.cache.MachineInformation;
import minedreams.mi.api.electricity.cache.WireLinkInfo;
import minedreams.mi.api.electricity.info.EnumVoltage;
import minedreams.mi.api.net.WaitList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.core.appender.OutputStreamManager;

/**
 * 关于电力系统的工作都在这里进行
 *
 * @author EmptyDreams
 * @version V1.0
 */
@Mod.EventBusSubscriber
public final class EleWorker {
	
	@SubscribeEvent
	public static void runAtTickEndService(@Nullable TickEvent.ServerTickEvent event) {
		if (users.isEmpty()) return;
		runEleTransportSystem();
		users = new LinkedHashMap<>();
	}
	
	private EleWorker() { throw new AssertionError("该类不应该初始化"); }
	
	/** 存储需要工作的电器 */
	private static Map<ElectricityUser, Object> users = new LinkedHashMap<>();
	
	/**
	 * 添加一个请求电力的用电器，该列表中的用电器不一定执行难.
	 * 如果添加的用电器已经在列表中，将覆盖已有数据
	 *
	 * @param user 需要运行的用电器
	 * @param info 附加信息
	 *
	 * @throws NullPointerException 如果 user == null || info == null
	 */
	public static synchronized void addNeedRunUser(ElectricityUser user, Object info) {
		WaitList.checkNull(user, "user");
		WaitList.checkNull(info, "info");
		
		if (user.getWorld().isRemote) return;
		users.put(user, info);
	}
	
	private static int transfersAmount = 50;
	
	/**
	 * 电力传输系统工作一次，因不明因素影响工作速度不太稳定，
	 * 经不严谨测试速度平均值稳定在1tick，最慢达到250+，最快达到10-
	 */
	public static synchronized void runEleTransportSystem() {
		Set<ElectricityTransfer> path = new HashSet<>(transfersAmount);
		Map<EnumFacing, ElectricityMaker> linked;
		ElectricityMaker realMaker = null;
		ElectricityUser user;
		//统计需要遍历的电线
		o:for (Map.Entry<ElectricityUser, Object> entry : users.entrySet()) {
			user = entry.getKey();
			linked = user.getLinkedMaker();
			
			//首先去除可以直接从发电机取电的电器
			if (!linked.isEmpty()) {
				for (ElectricityMaker maker : linked.values()) {
					if (maker.checkOutput(user.getVoltage())) {
						//判断是否可以输出指定电压
						if (maker.getOutputMax() >= user.getEnergy()) {
							//如果发电机电能足够则直接跳过本次循环
							maker.output(user.getEnergy(), user.getVoltage(), true);
							user.useElectricity(entry.getValue(), user.getEnergy(), user.getVoltage());
							continue o;
						} else if (maker.getOutputMax() >= user.getEnergyMin()) {
							//如果发电机电能不足则只使用电能最多并且满足电器最低需求的
							if (realMaker == null || maker.getOutputMax() > realMaker.getOutputMax())
								realMaker = maker;
						}
					}
				}
			}
			
			MachineCache cache = user.getCache();
			MachineInformation info = cache.read();
			int realEnergy = 0;
			EnumVoltage realVoltage = null;
			if (info != null) {
				if (realMaker == null || info.getMaker().getOutputMax() >= user.getEnergy() ||
						    (!realMaker.checkOutput(user.getVoltage()) && info.isValidVoltage(user))) {
					realMaker = info.getMaker();
				}
			}
			
			if (realMaker == null) continue;
			if (realMaker.getOutputMax() >= user.getEnergy()) realEnergy = user.getEnergy();
			else realEnergy = user.getEnergyMin();
			
			if (realMaker.checkOutput(user.getVoltage()))
				realVoltage = user.getVoltage();
			else if (realMaker.getVoltage_min().getVoltage() > user.getVoltage().getVoltage())
				realVoltage = realMaker.getVoltage_min();
			else continue;
			
			realMaker.output(realEnergy, realVoltage, true);
			user.useElectricity(entry.getValue(), realEnergy, realVoltage);
		}
		
		
	}
	
	/**
	 * 使线路上的电线运输只能能量
	 * @param transfers 要运输电力的线路
	 * @param energy 能量大小
	 */
	private static void transfer(Iterable<ElectricityTransfer> transfers, int energy) {
		transfers.forEach(it -> it.transfer(energy));
	}
	
	/**
	 * 从指定位置为用电器查找合适的发电机
	 * @param start 起点
	 * @param user 用电器
	 * @param ignore 忽略损耗计算的电线
	 * @return 带能需求，[0]-表示用电器的电能，[1]-表示损耗的电能，若[0] == -1则表示没有满足的发电机
	 */
	private static int[] findMaker(ElectricityTransfer start, ElectricityUser user,
	                              Collection<ElectricityTransfer> ignore, List<ElectricityTransfer> temp,
	                              AtomicReference<ElectricityMaker> maker) {
		WaitList.checkNull(start, "start");
		WaitList.checkNull(user, "user");
		WaitList.checkNull(ignore, "ignore");
		
		if (start.getLinkAmount() == 1) {
			int loss = findMaker_amount(start, null, user, maker, ignore, temp);
			if (maker.get() == null) return new int[] { -1, 0 };
			if (maker.get().getOutputMax() >= user.getEnergy() + loss) {
				return new int[] { user.getEnergy(), loss};
			} else {
				return new int[] { user.getEnergyMin(), loss};
			}
		} else {
			final AtomicReference<ElectricityMaker> maker2 = new AtomicReference<>(null);
			List<ElectricityTransfer> temp1 = new ArrayList<>();
			List<ElectricityTransfer> temp2 = new ArrayList<>();
			int loss1 = findMaker_amount(start, start.getPrev(), user, maker, ignore, temp1);
			int loss2 = findMaker_amount(start, start.getNext(), user, maker2, ignore, temp2);
			if (maker.get() == null) {
				if (maker2.get() == null) return new int[] { -1, 0 };
				temp.addAll(temp2);
				if (maker2.get().getOutputMax() >= user.getEnergy() + loss2) {
					return new int[] { user.getEnergy(), loss2 };
				} else {
					return new int[] { user.getEnergyMin(), loss2 };
				}
			} else if (maker2.get() == null) {
				temp.addAll(temp1);
				if (maker.get().getOutputMax() >= user.getEnergy() + loss1)
					return new int[] { user.getEnergy(), loss1 };
				else return new int[] { user.getEnergyMin(), loss1 };
			} else {
				if (maker.get().getOutputMax() < user.getEnergy() + loss1) {
					if (maker2.get().getOutputMax() < user.getEnergy() + loss2) {
						if (loss1 < loss2) {
							temp.addAll(temp1);
							return new int[] { user.getEnergyMin(), loss1 };
						} else {
							temp.addAll(temp2);
							maker.set(maker2.get());
							return new int[] { user.getEnergyMin(), loss2 };
						}
					} else {
						temp.addAll(temp2);
						maker.set(maker2.get());
						return new int[] { user.getEnergy(), loss2 };
					}
				} else {
					if (maker2.get().getOutputMax() < user.getEnergy() + loss2) {
						temp.addAll(temp1);
						return new int[] { user.getEnergy(), loss1 };
					} else {
						if (loss1 < loss2) {
							temp.addAll(temp1);
							return new int[] { user.getEnergy(), loss1 };
						} else {
							temp.addAll(temp2);
							maker.set(maker2.get());
							return new int[] { user.getEnergy(), loss2 };
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param start 搜索起点
	 * @param prev 上一个电线
	 * @param user 用电器
	 * @param maker 发电机
	 * @param ignore 忽略电能消耗计算的电线
	 * @param temp 临时存储
	 * @return 电线损耗的电能，返回-1表示没有满足要求的发电机
	 */
	private static int findMaker_amount(ElectricityTransfer start, ElectricityTransfer prev,
	                                    ElectricityUser user, AtomicReference<ElectricityMaker> maker,
	                                    Collection<ElectricityTransfer> ignore, List<ElectricityTransfer> temp) {
		AtomicInteger loss = new AtomicInteger(0);
		maker.set(null);
		
		//遍历电线
		start.forEach(prev, et -> {
			ElectricityMaker m;
			if (!ignore.contains(et))   //若电线没有遍历过则计算电力损耗
				loss.set(loss.get() + et.getLoss(user.getVoltage()));
			//遍历连接的方块，不包括连接的电线
			Collection<TileEntity> links = et.getLinkBlock().values();
			for (TileEntity entity : links) {
				if (entity instanceof  ElectricityMaker) {
					//如果连接的方块是发电机则判断是否可以输出电能
					m = (ElectricityMaker) entity;
					if (m.checkOutput(user.getVoltage()) &&
							    m.getOutputMax() >= user.getEnergy() + loss.get()) {
						//如果发电机电压电能都满足最高要求直接终止运算结果
						maker.set(m);
						temp.add(et);
						return false;
					} else if (m.getOutputMax() >= user.getEnergyMin() + loss.get()) {
						/* 若发电机满足最低输出要求且其比上一个发电机更佳则选用该发电机 */
						//如果上一个发电机是空则直接覆盖
						if (maker.get() == null ||
								    //如果上一个发电机电压不满足而该发电机电压满足则选用该发电机
								    (!maker.get().checkOutput(user.getVoltage()) &&
										     m.checkOutput(user.getVoltage())))
							maker.set(m);
					}
				}
			}
			temp.add(et);
			return true;
		});
		
		return loss.get();
	}
	
}
