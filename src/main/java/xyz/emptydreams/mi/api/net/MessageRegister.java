package xyz.emptydreams.mi.api.net;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.event.NetWorkRegistryEvent;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.net.message.gui.GuiMessage;
import xyz.emptydreams.mi.api.net.message.player.PlayerMessage;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 信息注册类.
 * 该类用于在消息从服务端（客户端）发送到客户端（服务端）后，
 * 保证在接收端可以正确解析信息。
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class MessageRegister {
	
	private static final List<IMessageHandle<?>> INSTANCES = new LinkedList<>();
	
	/**
	 * 注册一个信息类型
	 * @deprecated 请使用NetWorkRegistryEvent事件注册
	 * @see NetWorkRegistryEvent
	 */
	@Deprecated
	public static void registry(IMessageHandle<?> handle) {
		StringUtil.checkNull(handle, "handle");
		if (!INSTANCES.contains(handle)) INSTANCES.add(handle);
	}
	
	/**
	 * 尝试解析一个消息
	 * @param message 要解析的信息
	 * @return 是否解析成功
	 */
	public static boolean parseServer(NBTTagCompound message) {
		for (IMessageHandle<?> it : INSTANCES) {
			if (it.match(message)) {
				if (it.parseOnServer(message)) return true;
				MISysInfo.err("[MessageRegister]有一个信息解析失败：" + it.getInfo(message));
				return false;
			}
		}
		MISysInfo.err("[MessageRegister]信息未找到处理器：" + message);
		return false;
	}
	
	/**
	 * 尝试解析一个消息
	 * @param message 要解析的信息
	 * @return 是否解析成功
	 */
	@SideOnly(Side.CLIENT)
	public static boolean parseClient(NBTTagCompound message) {
		for (IMessageHandle<?> it : INSTANCES) {
			if (it.match(message)) {
				if (it.parseOnClient(message)) return true;
				MISysInfo.err("[MessageRegister]有一个信息解析失败：" + it.getInfo(message));
				return false;
			}
		}
		MISysInfo.err("[MessageRegister]信息未找到处理器：" + message);
		return false;
	}
	
	@SubscribeEvent
	public static void registryMessage(NetWorkRegistryEvent event) {
		event.registry(BlockMessage.instance(), GuiMessage.instance(), PlayerMessage.instance());
	}
	
}