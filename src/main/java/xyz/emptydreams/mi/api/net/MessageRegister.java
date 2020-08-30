package xyz.emptydreams.mi.api.net;

import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.net.message.block.BlockMessage;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 信息注册类.
 * 该类用于在消息从服务端（客户端）发送到客户端（服务端）后，
 * 保证在接收端可以正确解析信息。
 * @author EmptyDreams
 */
public final class MessageRegister {
	
	private static final List<IMessageHandle<?>> INSTANCES = new LinkedList<>();
	private static final List<IMessageHandle<?>> INSTANCES_CLIENT = new LinkedList<>();
	
	/** 注册一个信息类型 */
	public static void registry(IMessageHandle<?> handle) {
		StringUtil.checkNull(handle, "handle");
		if (WorldUtil.isServer(null)) INSTANCES.add(handle);
		else INSTANCES_CLIENT.add(handle);
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
				MISysInfo.err("有一个信息解析失败：" + it.getInfo(message));
				return false;
			}
		}
		MISysInfo.err("信息未找到处理器：" + message);
		return false;
	}
	
	/**
	 * 尝试解析一个消息
	 * @param message 要解析的信息
	 * @return 是否解析成功
	 */
	public static boolean parseClient(NBTTagCompound message) {
		for (IMessageHandle<?> it : INSTANCES_CLIENT) {
			if (it.match(message)) {
				if (it.parseOnClient(message)) return true;
				MISysInfo.err("有一个信息解析失败：" + it.getInfo(message));
				return false;
			}
		}
		MISysInfo.err("信息未找到处理器：" + message);
		return false;
	}
	
	static {
		registry(BlockMessage.instance());
	}
	
}
