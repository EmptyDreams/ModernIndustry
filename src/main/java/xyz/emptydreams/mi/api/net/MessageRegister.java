package xyz.emptydreams.mi.api.net;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.event.NetWorkRegistryEvent;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.net.message.gui.GuiMessage;
import xyz.emptydreams.mi.api.net.message.player.PlayerMessage;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.LinkedList;
import java.util.List;

import static xyz.emptydreams.mi.api.net.ParseResultEnum.EXCEPTION;

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
		String key = handle.getKey();
		for (IMessageHandle<?> instance : INSTANCES) {
			String name = instance.getKey();
			if (name.hashCode() == key.hashCode() && name.equals(key)) {
				throw new IllegalArgumentException(
						"注册的Handle[" + handle.getClass().getName()
							+ "]和已有Handle[" + instance.getClass().getName() + "]的Key["
								+ name + "]值重复");
			}
		}
		INSTANCES.add(handle);
	}
	
	/**
	 * 尝试解析一个消息
	 * @param message 要解析的信息
	 * @return 是否解析成功
	 */
	public static ParseResultEnum parseServer(IDataReader message, String key) {
		for (IMessageHandle<?> it : INSTANCES) {
			if (key.hashCode() == it.getKey().hashCode() &&
					key.equals(it.getKey())) {
				ParseResultEnum result = it.parseOnServer(message);
				if (result.isThrow()) MISysInfo.err("[MessageRegister]一个信息未被成功处理，该信息被丢弃");
				return result;
			}
		}
		MISysInfo.err("[MessageRegister]信息未找到处理器：" + message.getClass().getName());
		return EXCEPTION;
	}
	
	/**
	 * 尝试解析一个消息
	 * @param message 要解析的信息
	 * @return 是否解析成功
	 */
	@SideOnly(Side.CLIENT)
	public static ParseResultEnum parseClient(IDataReader message, String key) {
		for (IMessageHandle<?> it : INSTANCES) {
			if (key.hashCode() == it.getKey().hashCode() &&
					key.equals(it.getKey())) {
				ParseResultEnum result = it.parseOnClient(message);
				if (result.isThrow()) MISysInfo.err("[MessageRegister]一个信息未被成功处理，该信息被丢弃");
				return result;
			}
		}
		MISysInfo.err("[MessageRegister]信息未找到处理器：" + message);
		return EXCEPTION;
	}
	
	@SubscribeEvent
	public static void registryMessage(NetWorkRegistryEvent event) {
		event.registry(BlockMessage.instance(), GuiMessage.instance(), PlayerMessage.instance());
	}
	
}