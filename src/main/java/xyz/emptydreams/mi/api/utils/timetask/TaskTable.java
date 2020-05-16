package xyz.emptydreams.mi.api.utils.timetask;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.net.WaitList;

/**
 * 存储事件任务列表
 * @author EmptyDreams
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class TaskTable {
	
	private static final List<ITimeTask> TABLE_LIST = new LinkedList<>();
	@SideOnly(Side.CLIENT)
	private static final List<ITimeTask> CLIENT_TABLE_LIST = new LinkedList<>();
	
	/**
	 * 在客户端注册一个任务
	 * @throws NullPointerException 如果 task == null
	 * @throws IllegalArgumentException 如果 maxTime <= 0
	 */
	@SideOnly(Side.CLIENT)
	public static void registerClientTask(ITimeTask task) {
		WaitList.checkNull(task, "task");
		CLIENT_TABLE_LIST.add(task);
	}
	
	/**
	 * 在服务端注册一个任务
	 * @throws NullPointerException 如果 task == null
	 * @throws IllegalArgumentException 如果 maxTime <= 0
	 */
	public static void registerTask(ITimeTask task) {
		WaitList.checkNull(task, "task");
		TABLE_LIST.add(task);
	}
	
	/**
	 * 在客户端移除一个任务
	 * @return 是否移除成功
	 */
	@SideOnly(Side.CLIENT)
	public static boolean unregisterClientTask(ITimeTask task) {
		Iterator<ITimeTask> it = CLIENT_TABLE_LIST.iterator();
		ITimeTask t;
		while (it.hasNext()) {
			t = it.next();
			if (t.equals(task)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 移除一个任务
	 * @return 是否移除成功
	 */
	public static boolean unregisterTask(ITimeTask task) {
		Iterator<ITimeTask> it = TABLE_LIST.iterator();
		ITimeTask t;
		while (it.hasNext()) {
			t = it.next();
			if (t.equals(task)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void clientAccept(TickEvent.ClientTickEvent event) {
		Iterator<ITimeTask> iterator = CLIENT_TABLE_LIST.iterator();
		ITimeTask it;
		while (iterator.hasNext()) {
			it = iterator.next();
			if (it.plus() >= it.getMaxTime()) {
				if (it.accept()) {
					iterator.remove();
				} else {
					it.setTime(0);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void serverAccept(TickEvent.ServerTickEvent event) {
		Iterator<ITimeTask> iterator = TABLE_LIST.iterator();
		ITimeTask it;
		while (iterator.hasNext()) {
			it = iterator.next();
			if (it.plus() >= it.getMaxTime()) {
				if (it.accept()) {
					iterator.remove();
				} else {
					it.setTime(0);
				}
			}
		}
	}
	
}
