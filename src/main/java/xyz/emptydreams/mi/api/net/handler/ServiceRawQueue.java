package xyz.emptydreams.mi.api.net.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.net.message.ParseAddition;

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
	public synchronized static void add(IDataReader data, String key, EntityPlayerMP player) {
		queue.add(new Node(data, key, player));
	}
	
	@SubscribeEvent
	public synchronized static void tryToCleanQueue(TickEvent.ServerTickEvent event) {
		Iterator<Node> it = queue.iterator();
		Node node;
		while (it.hasNext()) {
			node = it.next();
			int start = node.reader.nowReadIndex();
			ParseAddition result = MessageRegister.parseServer(node.reader, node.key, node.addition);
			if (result.isRetry()) {
				node.reader.setReadIndex(start);
				node.addition = result;
			} else {
				it.remove();
			}
		}
	}
	
	static final class Node {
		
		final IDataReader reader;
		final String key;
		ParseAddition addition;
		
		Node(IDataReader reader, String key, EntityPlayerMP player) {
			this.reader = reader;
			this.key = key;
			addition = new ParseAddition(player);
		}
		
	}
	
}