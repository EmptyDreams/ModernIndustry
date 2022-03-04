package top.kmar.mi.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import top.kmar.mi.api.net.MessageRegister;
import top.kmar.mi.api.net.message.IMessageHandle;

/**
 * 网络传输注册事件
 * @author EmptyDreams
 */
public class NetWorkRegistryEvent extends Event {
	
	/** 注册信息类型 */
	@SuppressWarnings("deprecation")
	public void registry(IMessageHandle<?, ?>... handles) {
		for (IMessageHandle<?, ?> handle : handles) {
			MessageRegister.registry(handle);
		}
	}
	
}