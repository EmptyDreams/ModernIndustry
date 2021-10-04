package xyz.emptydreams.mi.api.utils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Tick任务
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class TickHelper {
	
	/** 存储任务列表 */
	private static final List<BooleanSupplier> clientTaskList = new LinkedList<>();
	private static final List<BooleanSupplier> serverTaskList = new LinkedList<>();
	
	/**
	 * 添加一个任务到客户端列表，在Tick结束时执行
	 * @param task 任务内容，返回值为执行后是否删除任务
	 */
	public static void addClientTask(BooleanSupplier task) {
		clientTaskList.add(StringUtil.checkNull(task, "task"));
	}
	
	/**
	 * 添加一个任务到服务端列表，在Tick结束时执行
	 * @param task 任务内容，返回值为执行后是否删除任务
	 */
	public static void addServerTask(BooleanSupplier task) {
		serverTaskList.add(StringUtil.checkNull(task, "task"));
	}
	
	/**
	 * 添加一个任务到列表，自动判断客户端/服务端，在Tick结束时执行
	 * @param task 任务内容，返回值为执行后是否删除任务
	 */
	public static void addAutoTask(BooleanSupplier task) {
		if (WorldUtil.isServer()) addServerTask(task);
		else addClientTask(task);
	}
	
	@SubscribeEvent
	public static void handleServiceAllTask(TickEvent.ServerTickEvent event) {
		serverTaskList.removeIf(BooleanSupplier::getAsBoolean);
	}
	
	@SubscribeEvent
	public static void handleClientAllTask(TickEvent.ClientTickEvent event) {
		clientTaskList.removeIf(BooleanSupplier::getAsBoolean);
	}
	
}