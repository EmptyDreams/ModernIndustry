package minedreams.mi.api.net;

import java.util.LinkedHashSet;
import java.util.Set;

import minedreams.mi.api.electricity.Electricity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
	
	/** 已经传输的信息总数 */
	private static int amount = 0;
	
	@SubscribeEvent
	public static void runAtTickEndClient(TickEvent.ClientTickEvent event) {
		reClient();
	}
	
	/** 存储客户端待处理的消息 */
	private static Set<MessageBase> client = new LinkedHashSet<>();
	/** 操作client时的锁，因为client可变，所以重新建立一个常量 */
	private static final Object CLIENT_LOCK = new Object();
	
	/** 获取已经执行的信息总数 */
	public static int getAmount() {
		return amount;
	}
	
	public static void addMessageToClientList(MessageBase base) {
		synchronized (CLIENT_LOCK) {
			checkNull(base, "base");
			client.add(base);
		}
	}
	
	public static void toString(StringBuilder sb) {
		synchronized (CLIENT_LOCK) {
			for (MessageBase base : client) {
				sb.append('\t').append(base).append('\n');
			}
		}
	}
	
	/**
	 * 尝试清空client消息列表，只能在客户端调用
	 */
	@SideOnly(Side.CLIENT)
	public static void reClient() {
		if (net.minecraft.client.Minecraft.getMinecraft().world == null) {
			amount = 0;
			return;
		}
		Set<MessageBase> clientMessage = new LinkedHashSet<>();
		synchronized (CLIENT_LOCK) {
			client.forEach(mb -> {
				Electricity et = (Electricity) net.minecraft.client.Minecraft
						                               .getMinecraft().world.getTileEntity(mb.getPos());
				if (et == null) {
					clientMessage.add(mb);
				} else {
					++amount;
					et.reveive(mb.getMessageList());
				}
			});
			client = clientMessage;
		}
	}
	
	/**
	 * 发送信息到客户端
	 *
	 * @param message 要发送的信息
	 * @param players 指定玩家
	 *
	 * @throws NullPointerException 如果message和player任意一个为null
	 * @throws IllegalArgumentException 如果player不能强制转换为EntityPlayerMP[]
	 */
	public static void sendToClient(IMessage message, EntityPlayer... players) {
		checkNull(players, "players");
		checkNull(message, "message");
		
		for (EntityPlayer player : players) {
			if (!(player instanceof EntityPlayerMP))
				throw new IllegalArgumentException("player应该可以被强制转换为EntityPlayerMP");
			NetworkLoader.instance().sendTo(message, (EntityPlayerMP) player);
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
	public static void sendToClient(IMessage message, Iterable<? extends EntityPlayer> player) {
		checkNull(player, "player");
		checkNull(message, "message");
		player.forEach(p -> {
			if (p instanceof EntityPlayerMP) {
				NetworkLoader.instance().sendTo(message, (EntityPlayerMP) p);
			} else {
				throw new IllegalArgumentException("player：" + p);
			}
		});
	}
	
	/**
	 * 发送信息到客户端
	 *
	 * @param message 要发送的信息
	 * @param players 指定玩家
	 *
	 * @throws NullPointerException 如果message和player任意一个为null
	 */
	public static void sendToClient(IMessage message, EntityPlayerMP... players) {
		checkNull(message, "message");
		checkNull(players, "players");
		
		for (EntityPlayerMP player : players)
			NetworkLoader.instance().sendTo(message, player);
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
		NetworkLoader.instance().sendToServer(message);
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
	
}
