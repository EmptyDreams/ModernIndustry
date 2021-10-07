package xyz.emptydreams.mi.api.net.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.net.message.ParseAddition;
import xyz.emptydreams.mi.api.utils.TickHelper;

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
	private static boolean isAdd = false;
	
	/** 将一个任务添加到队列中，方法内部自动解析Key值 */
	public static void add(IDataReader data, String key) {
		synchronized (queue) {
			queue.add(new ServiceRawQueue.Node(data, key, null));
		}
		tryToCleanQueue();
	}
	
	/** 尝试清空人物列表 */
	public static void tryToCleanQueue() {
		if (isAdd) return;
		World world = Minecraft.getMinecraft().world;
		if (world == null || queue.isEmpty()) return;
		isAdd = true;
		TickHelper.addClientTask(() -> {
			isAdd = false;
			synchronized (queue) {
				Iterator<ServiceRawQueue.Node> it = queue.iterator();
				ServiceRawQueue.Node node;
				while (it.hasNext()) {
					node = it.next();
					int start = node.reader.nowReadIndex();
					ParseAddition result = MessageRegister.parseClient(node.reader, node.key, node.addition);
					if (result.isRetry()) {
						node.reader.setReadIndex(start);
						node.addition = result;
					} else {
						it.remove();
					}
				}
			}
			return true;
		});
	}
	
}