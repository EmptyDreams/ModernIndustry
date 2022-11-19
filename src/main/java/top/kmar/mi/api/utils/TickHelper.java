package top.kmar.mi.api.utils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.utils.expands.WorldExpandsKt;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BooleanSupplier;

/**
 * Tick任务
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class TickHelper {
    
    /** 存储任务列表 */
    private static final Queue<BooleanSupplier> clientTaskQueue = new ConcurrentLinkedQueue<>();
    private static final Queue<BooleanSupplier> serverTaskQueue = new ConcurrentLinkedQueue<>();
    
    /**
     * 添加一个任务到客户端列表，在Tick结束时执行
     * @param task 任务内容，返回值为执行后是否删除任务
     */
    public static void addClientTask(BooleanSupplier task) {
        clientTaskQueue.add(StringUtil.checkNull(task, "task"));
    }
    
    /**
     * 添加一个任务到服务端列表，在Tick结束时执行
     * @param task 任务内容，返回值为执行后是否删除任务
     */
    public static void addServerTask(BooleanSupplier task) {
        serverTaskQueue.add(StringUtil.checkNull(task, "task"));
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
        WorldExpandsKt.callTickableListUpdateTask(null);
        int size = serverTaskQueue.size();
        for (int i = 0; i != size; ++i) {
            BooleanSupplier supplier = serverTaskQueue.remove();
            if (!supplier.getAsBoolean()) serverTaskQueue.add(supplier);
        }
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void handleClientAllTask(TickEvent.ClientTickEvent event) {
        WorldExpandsKt.callTickableListUpdateTask(null);
        int size = clientTaskQueue.size();
        for (int i = 0; i != size; ++i) {
            BooleanSupplier supplier = clientTaskQueue.remove();
            if (!supplier.getAsBoolean()) clientTaskQueue.add(supplier);
        }
    }
    
}