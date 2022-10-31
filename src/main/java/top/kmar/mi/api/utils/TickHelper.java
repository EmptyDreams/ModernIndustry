package top.kmar.mi.api.utils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Tick任务
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class TickHelper {
	
	private static final Object serverLock = new Object();
	private static final Object clientLock = new Object();
	
	/** 存储任务列表 */
	private static List<BooleanSupplier> clientTaskList = new LinkedList<>();
	private static List<BooleanSupplier> serverTaskList = new LinkedList<>();
	
	/**
	 * 添加一个任务到客户端列表，在Tick结束时执行
	 * @param task 任务内容，返回值为执行后是否删除任务
	 */
	public static void addClientTask(BooleanSupplier task) {
		synchronized (clientLock) {
			clientTaskList.add(StringUtil.checkNull(task, "task"));
		}
	}
	
	/**
	 * 添加一个任务到服务端列表，在Tick结束时执行
	 * @param task 任务内容，返回值为执行后是否删除任务
	 */
	public static void addServerTask(BooleanSupplier task) {
		synchronized (serverLock) {
			serverTaskList.add(StringUtil.checkNull(task, "task"));
		}
	}
	
	/**
	 * 添加一个任务到列表，自动判断客户端/服务端，在Tick结束时执行
	 * @param task 任务内容，返回值为执行后是否删除任务
	 */
	public static void addAutoTask(BooleanSupplier task) {
		if (WorldExpandsKt.isServer()) addServerTask(task);
		else addClientTask(task);
	}
	
	@SubscribeEvent
	public static void handleServiceAllTask(TickEvent.ServerTickEvent event) {
		List<BooleanSupplier> old;
		synchronized (serverLock) {
			old = serverTaskList;
			serverTaskList = new LinkedList<>();
		}
		old.removeIf(BooleanSupplier::getAsBoolean);
		serverTaskList.addAll(old);
	}
	
	@SubscribeEvent
	public static void handleClientAllTask(TickEvent.ClientTickEvent event) {
		List<BooleanSupplier> old;
		synchronized (clientLock) {
			old = clientTaskList;
			clientTaskList = new LinkedList<>();
		}
		old.removeIf(BooleanSupplier::getAsBoolean);
		clientTaskList.addAll(old);
	}
	
}