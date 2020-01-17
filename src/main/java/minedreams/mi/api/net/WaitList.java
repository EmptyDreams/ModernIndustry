package minedreams.mi.api.net;

import java.util.*;

import minedreams.mi.api.electricity.Electricity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 所有客户端/服务端需要等待处理的消息存放在此处
 *
 * @author EmptyDreams
 * @version V1.0
 */
@Mod.EventBusSubscriber
public class WaitList {
	
	@SubscribeEvent
	public static void runAtTickEndClient(TickEvent.ClientTickEvent event) {
		reClient();
		sendAll(true);
	}
	
	@SubscribeEvent
	public static void runAtTickEndService(TickEvent.ServerTickEvent event) {
		sendAll(false);
	}
	
	/** 存储客户端待处理的消息 */
	public static final Set<MessageBase> client = new LinkedHashSet<>();
	
	/** 存储客户端待发送的数据 */
	private static final Set<MessageBase> clientMessage = new LinkedHashSet<>();
	
	/** 存储服务端待发送的数据 */
	private static final Map<IMessage, Set<EntityPlayerMP>> serviceMessage = new LinkedHashMap<>();
	
	/**
	 * 尝试清空client消息列表，只能在客户端调用
	 */
	@SideOnly(Side.CLIENT)
	public static void reClient() {
		if (net.minecraft.client.Minecraft.getMinecraft().world == null) return;
		client.forEach(mb -> {
			Electricity et = (Electricity) net.minecraft.client.Minecraft
					                               .getMinecraft().world.getTileEntity(mb.getPos());
			if (et != null) et.reveive(mb.getMessageList());
		});
		client.clear();
	}
	
	/**
	 * 发送所有消息
	 */
	private static void sendAll(boolean isClient) {
		if (isClient) {
			synchronized (clientMessage) {
				for (IMessage im : clientMessage) NetworkLoader.instance().sendToServer(im);
				clientMessage.clear();
			}
		} else {
			synchronized (serviceMessage) {
				for (Map.Entry<IMessage, Set<EntityPlayerMP>> entry : serviceMessage.entrySet()) {
					entry.getValue().forEach(player -> NetworkLoader.instance().sendTo(entry.getKey(), player));
				}
				serviceMessage.clear();
			}
		}
	}
	
	/**
	 * 发送信息到客户端
	 *
	 * @param message 要发送的信息
	 * @param world 世界对象
	 * @param name 指定玩家的名称
	 *
	 * @throws NullPointerException 如果message和player任意一个为null
	 * @throws IllegalArgumentException 如果name中的玩家不在世界中存在
	 */
	public static void sendToClient(IMessage message, World world, String... name) {
		if (name == null) throw new NullPointerException("name == null");
		if (message == null) throw new NullPointerException("message == null");
		checkNull(message, "message");
		checkNull(world, "world");
		checkNull(name, "name");
		
		EntityPlayerMP[] players = new EntityPlayerMP[name.length];
		EntityPlayer temp;
		for (int i = 0; i < name.length; ++i) {
			temp = world.getPlayerEntityByName(name[i]);
			if (temp == null) throw new IllegalArgumentException("name[" + i + "]：" + name[i] + "-该玩家不存在");
			players[i] = (EntityPlayerMP) temp;
		}
		sendToClient(message, players);
	}
	
	/**
	 * 发送信息到客户端
	 *
	 * @param message 要发送的信息
	 * @param player 指定玩家
	 *
	 * @throws NullPointerException 如果message和player任意一个为null
	 * @throws IllegalArgumentException 如果player不能强制转换为EntityPlayerMP[]
	 */
	public static void sendToClient(IMessage message, EntityPlayer... player) {
		checkNull(player, "player");
		checkNull(message, "message");
		if (!(player instanceof EntityPlayerMP[]))
			throw new IllegalArgumentException("player应该可以被强制转换为EntityPlayerMP");
		sendToClient(message, (EntityPlayerMP[]) player);
	}
	
	/**
	 * 发送信息到客户端
	 *
	 * @param message 要发送的信息
	 * @param player 指定玩家
	 *
	 * @throws NullPointerException 如果message和player任意一个为null
	 * @throws IllegalArgumentException 如果player中的元素不能强制转换为EntityPlayerMP
	 */
	public static void sendToClient(IMessage message, Set<? extends EntityPlayer> player) {
		checkNull(player, "player");
		checkNull(message, "message");
		Set<EntityPlayerMP> set = serviceMessage.containsKey(message) ?
				                          serviceMessage.get(message) : new LinkedHashSet<>();
		player.forEach(p -> {
			if (p instanceof EntityPlayerMP) {
				set.add((EntityPlayerMP) p);
			} else {
				throw new IllegalArgumentException("player：" + p);
			}
		});
		synchronized (serviceMessage) {
			serviceMessage.put(message, set);
		}
	}
	
	/**
	 * 发送信息到客户端
	 *
	 * @param message 要发送的信息
	 * @param player 指定玩家
	 *
	 * @throws NullPointerException 如果message和player任意一个为null
	 * @throws IllegalArgumentException 如果player中的元素不能强制转换为EntityPlayerMP
	 */
	public static void sendToClient(IMessage message, List<? extends EntityPlayer> player) {
		checkNull(player, "player");
		checkNull(message, "message");
		Set<EntityPlayerMP> set = serviceMessage.containsKey(message) ?
				                          serviceMessage.get(message) : new LinkedHashSet<>();
		player.forEach(p -> {
			if (p instanceof EntityPlayerMP) {
				set.add((EntityPlayerMP) p);
			} else {
				throw new IllegalArgumentException("player：" + p);
			}
		});
		synchronized (serviceMessage) {
			serviceMessage.put(message, set);
		}
	}
	
	/**
	 * 发送信息到客户端
	 *
	 * @param message 要发送的信息
	 * @param player 指定玩家
	 *
	 * @throws NullPointerException 如果message和player任意一个为null
	 */
	public static void sendToClient(IMessage message, EntityPlayerMP... player) {
		checkNull(message, "message");
		checkNull(player, "player");
		if (serviceMessage.containsKey(message)) {
			Collections.addAll(serviceMessage.get(message), player);
		} else {
			Set<EntityPlayerMP> set = new LinkedHashSet<>();
			Collections.addAll(set, player);
			synchronized (serviceMessage) {
				serviceMessage.put(message, set);
			}
		}
	}
	
	/**
	 * 发送信息到服务端
	 * @param message 要发送的信息
	 *
	 * @throws NullPointerException 如果message为null
	 */
	@SideOnly(Side.CLIENT)
	public static void sendToService(MessageBase message) {
		if (message == null) throw new NullPointerException("message == null");
		synchronized (clientMessage) {
			clientMessage.add(message);
		}
	}
	
	/**
	 * 检查参数是否为null
	 *
	 * @throws NullPointerException 如果参数为null
	 * @throws IllegalArgumentException 如果objects与name长度不一样
	 */
	public static void checkNull(Object[] objects, String[] name) {
		if (objects.length != name.length) throw new IllegalArgumentException("objects的长度与name的不相等");
		if (objects == null) throw new NullPointerException("objects == null");
		if (name == null) throw new NullPointerException("name == null");
		for (int i = 0; i < name.length; ++i) {
			if (objects[i] == null) throw new NullPointerException(name[i] + " == null");
		}
	}
	
	/**
	 * 检查参数是否为null
	 *
	 * @throws NullPointerException 如果参数为null
	 */
	public static void checkNull(Object object, String name) {
		if (object == null) throw new NullPointerException(name + " == null");
	}
	
	private final static class Lists<T> extends LinkedList<T> {
		@Override
		public boolean add(T o) {
			synchronized (client) {
				return super.add(o);
			}
		}
	}
	
}
