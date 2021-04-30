package xyz.emptydreams.mi.api.net.handler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.net.ParseResultEnum;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务端等待队列
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public class ServiceRawQueue {
	
	private static final List<Node> queue = new LinkedList<>();
	
	/** 将一个任务添加到队列中，方法内部自动解析Key值 */
	public static void add(IDataReader data, String key) {
		queue.add(new Node(data, key));
	}
	
	@SubscribeEvent
	public static void tryToCleanQueue(TickEvent.ServerTickEvent event) {
		Iterator<Node> it = queue.iterator();
		Node node;
		while (it.hasNext()) {
			node = it.next();
			ParseResultEnum result = MessageRegister.parseServer(node.reader, node.key);
			if (!result.isRetry()) {
				it.remove();
			}
		}
	}
	
	static final class Node {
		
		IDataReader reader;
		String key;
		
		Node(IDataReader reader, String key) {
			this.reader = reader;
			this.key = key;
		}
		
	}
	
}