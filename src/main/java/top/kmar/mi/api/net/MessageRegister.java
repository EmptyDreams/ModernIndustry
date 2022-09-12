package top.kmar.mi.api.net;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.event.NetWorkRegistryEvent;
import top.kmar.mi.api.net.message.IMessageHandle;
import top.kmar.mi.api.net.message.ParseAddition;
import top.kmar.mi.api.net.message.block.BlockMessage;
import top.kmar.mi.api.net.message.player.PlayerMessage;
import top.kmar.mi.api.utils.MISysInfo;
import top.kmar.mi.api.utils.StringUtil;

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
	
	private static final List<IMessageHandle<?, ?>> INSTANCES = new LinkedList<>();
	
	/**
	 * 注册一个信息类型
	 * @deprecated 请使用NetWorkRegistryEvent事件注册
	 * @see NetWorkRegistryEvent
	 */
	@Deprecated
	public static void registry(IMessageHandle<?, ?> handle) {
		StringUtil.checkNull(handle, "handle");
		String key = handle.getKey();
		for (IMessageHandle<?, ?> instance : INSTANCES) {
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
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static ParseAddition parseServer(IDataReader message, String key, ParseAddition parseAddition) {
		for (IMessageHandle it : INSTANCES) {
			if (key.hashCode() == it.getKey().hashCode() &&
					key.equals(it.getKey())) {
				ParseAddition result = it.parseOnServer(message, parseAddition);
				if (result.isThrow()) {
					MISysInfo.err("[MessageRegister]一个信息未被成功处理，该信息被丢弃："
									+ "\n\tkey =\t" + key
									+ "\n\thandle =\t" + it.getClass().getName());
				}
				return result;
			}
		}
		MISysInfo.err("[MessageRegister]信息未找到处理器：" + key);
		return parseAddition.setParseResult(ParseResultEnum.EXCEPTION);
	}
	
	/**
	 * 尝试解析一个消息
	 * @param message 要解析的信息
	 * @return 是否解析成功
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SideOnly(Side.CLIENT)
	public static ParseAddition parseClient(IDataReader message, String key, ParseAddition parseAddition) {
		for (IMessageHandle it : INSTANCES) {
			if (key.hashCode() == it.getKey().hashCode() &&
					key.equals(it.getKey())) {
				ParseAddition result = it.parseOnClient(message, parseAddition);
				if (result.isThrow())
					MISysInfo.err("[MessageRegister]一个信息未被成功处理，该信息被丢弃");
				return result;
			}
		}
		MISysInfo.err("[MessageRegister]信息未找到处理器：" + key);
		return parseAddition.setParseResult(ParseResultEnum.EXCEPTION);
	}
	
	@SubscribeEvent
	public static void registryMessage(NetWorkRegistryEvent event) {
		event.registry(BlockMessage.instance(), PlayerMessage.instance());
	}
	
}