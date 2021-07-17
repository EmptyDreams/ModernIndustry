package xyz.emptydreams.mi.api.net.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.net.message.ParseAddition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 客户端任务队列
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public final class ClientRawQueue {
	
	private static final List<ServiceRawQueue.Node> queue = new LinkedList<>();
	
	/** 将一个任务添加到队列中，方法内部自动解析Key值 */
	public synchronized static void add(IDataReader data, String key) {
		queue.add(new ServiceRawQueue.Node(data, key, null));
	}
	
	@SubscribeEvent
	public synchronized static void tryToCleanQueue(TickEvent.ClientTickEvent event) {
		World world = Minecraft.getMinecraft().world;
		if (world == null || queue.isEmpty()) return;
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
	
}