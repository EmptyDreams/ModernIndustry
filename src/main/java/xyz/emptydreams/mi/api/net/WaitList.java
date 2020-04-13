package xyz.emptydreams.mi.api.net;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import xyz.emptydreams.mi.api.net.guinet.GUIMessage;
import xyz.emptydreams.mi.api.net.guinet.IAutoGuiNetWork;
import xyz.emptydreams.mi.utils.MISysInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
	public static void runAtTickEndService(TickEvent.ServerTickEvent event) {
		sendAll(false);
	}
	
	@SubscribeEvent
	public static void runAtTickEndClient(TickEvent.ClientTickEvent event) {
		reClient();
		sendAll(true);
	}
	
	/** 存储客户端待处理的消息 */
	private static final Set<MessageBase> CLIENT = new LinkedHashSet<>();
	/** 存储客户端GUI待处理消息 */
	private static final Set<GUIMessage> GUI_CLIENT = new LinkedHashSet<>();
	
	/** 获取已经执行的信息总数 */
	public static int getAmount() {
		return amount;
	}
	
	public static void addMessageToClientList(MessageBase base) {
		checkNull(base, "base");
		synchronized (CLIENT) {
			CLIENT.add(base);
		}
	}
	
	public static void addMessageToClientList(GUIMessage gui) {
		checkNull(gui, "gui");
		synchronized (GUI_CLIENT) {
			GUI_CLIENT.add(gui);
		}
	}
	
	public static void toString(StringBuilder sb) {
		synchronized (CLIENT) {
			for (IMessage base : CLIENT) {
				sb.append('\t').append(base).append('\n');
			}
		}
		synchronized (GUI_CLIENT) {
			for (IMessage base : GUI_CLIENT) {
				sb.append('\t').append(base).append('\n');
			}
		}
	}
	
	private static void sendAll(boolean isClient) {
		if (isClient) {
			NetworkRegister.forEachBlock(true, network -> {
				NBTTagCompound message = network.send();
				if (message != null) {
					TileEntity te = (TileEntity) network;
					message.setIntArray("_pos", new int[] {
							te.getPos().getX(),
							te.getPos().getY(),
							te.getPos().getZ()});
					message.setInteger("_world", te.getWorld().provider.getDimension());
					sendToService(new MessageBase(message));
				}
			});
		} else {
			try {
				NetworkRegister.forEachBlock(false, network -> {
					NBTTagCompound message = network.send();
					if (message != null) {
						TileEntity te = (TileEntity) network;
						message.setIntArray("_pos", new int[] {
								te.getPos().getX(),
								te.getPos().getY(),
								te.getPos().getZ()});
						message.setInteger("_world", te.getWorld().provider.getDimension());
						Set<EntityPlayerMP> players = AutoNetworkHelper.getPlayer(te.getWorld(), message);
						if (players != null) sendToClient(new MessageBase(message), players);
					}
				});
				NetworkRegister.forEachGui(false, network -> {
					NBTTagCompound message = network.send();
					if (message != null) {
						message.setInteger("_code", network.getAuthCode());
						Set<EntityPlayerMP> players = AutoNetworkHelper.getPlayer(network.getWorld(), message);
						if (players != null) sendToClient(new GUIMessage(message), players);
					}
				});
			} catch (ClassCastException e) {
				System.err.println("网络传输名单中存在违规信息！");
				throw e;
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
		synchronized (CLIENT) {
			Iterator<MessageBase> it = CLIENT.iterator();
			MessageBase mb;
			IAutoNetwork network;
			while (it.hasNext()) {
				mb = it.next();
				network = (IAutoNetwork) net.minecraft.client.Minecraft
						                        .getMinecraft().world.getTileEntity(
								mb.getBlockPos());
				if (network != null) {
					++amount;
					it.remove();
					network.receive(mb.getCompound());
				}
			}
		}
		synchronized (GUI_CLIENT) {
			Iterator<GUIMessage> it = GUI_CLIENT.iterator();
			GUIMessage message;
			IAutoGuiNetWork netWork;
			while (it.hasNext()) {
				message = it.next();
				NBTTagCompound compound = message.getCompound();
				EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
				Container con = player.openContainer;
				if (con instanceof IAutoGuiNetWork) {
					int code = compound.getInteger("_code");
					IAutoGuiNetWork network = (IAutoGuiNetWork) con;
					net.minecraft.client.gui.inventory.GuiContainer gui = network.getGuiContainer();
					network = (IAutoGuiNetWork) gui;
					if (network.checkAutoCode(code)) {
						if (network.isLive()) {
							it.remove();
							network.receive(compound);
						} else {
							MISysInfo.err("GUI网络传输丢包：客户端GUI不处于生存状态！");
						}
					} else {
						MISysInfo.err("GUI网络传输丢包：serveCode=" + code + ", clientCode=" + network.getAuthCode());
					}
				} else {
					if (message.plusAmount() > 30) {
						MISysInfo.err("GUI网络传输丢包：serveCode=" + message.getCompound().getInteger("_code"));
						it.remove();
					}
				}
			}
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
	public static void sendToClient(IMessage message, Iterable<? extends EntityPlayerMP> player) {
		checkNull(player, "player");
		checkNull(message, "message");
		player.forEach(p -> {
			if (p instanceof EntityPlayerMP) {
				NetworkLoader.instance().sendTo(message, p);
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
	public static void sendToService(IMessage message) {
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
