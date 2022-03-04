package top.kmar.mi.api.net.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.net.MessageRegister;
import top.kmar.mi.api.net.message.ParseAddition;
import top.kmar.mi.api.utils.TickHelper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务端等待队列
 * @author EmptyDreams
 */
public class ServiceRawQueue {
	
	private static final List<Node> queue = new LinkedList<>();
	private static boolean isAdd = false;
	
	/** 将一个任务添加到队列中，方法内部自动解析Key值 */
	public static void add(IDataReader data, String key, EntityPlayerMP player) {
		synchronized (queue) {
			queue.add(new Node(data, key, player));
		}
		tryToCleanQueue();
	}
	
	public static void tryToCleanQueue() {
		if (isAdd) return;
		if (queue.isEmpty()) return;
		isAdd = true;
		TickHelper.addServerTask(() -> {
			synchronized (queue) {
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
				if (queue.isEmpty()) isAdd = false;
			}
			return !isAdd;
		});
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