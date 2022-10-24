package top.kmar.mi.api.net.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.net.MessageRegister;
import top.kmar.mi.api.net.message.ParseAddition;
import top.kmar.mi.api.utils.TickHelper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 客户端任务队列
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class ClientRawQueue {
    
    private static final List<ServiceRawQueue.Node> queue = new LinkedList<>();
    volatile private static boolean isAdd = false;
    
    /** 将一个任务添加到队列中 */
    public static void add(NBTTagCompound data, String key) {
        synchronized (queue) {
            queue.add(new ServiceRawQueue.Node(data, key, null));
        }
        tryToCleanQueue();
    }
    
    /** 尝试清空人物列表 */
    public static void tryToCleanQueue() {
        if (isAdd || queue.isEmpty()) return;
        isAdd = true;
        TickHelper.addClientTask(() -> {
            World world = Minecraft.getMinecraft().world;
            if (world == null) return false;
            synchronized (queue) {
                Iterator<ServiceRawQueue.Node> it = queue.iterator();
                ServiceRawQueue.Node node;
                while (it.hasNext()) {
                    node = it.next();
                    ParseAddition result = MessageRegister.parseClient(node.data, node.key, node.addition);
                    if (result.isRetry()) node.addition = result;
                    else it.remove();
                }
                if (queue.isEmpty()) isAdd = false;
            }
            return !isAdd;
        });
    }
    
}