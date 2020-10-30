package xyz.emptydreams.mi.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import xyz.emptydreams.mi.api.net.MessageRegister;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;

/**
 * 网络传输注册事件
 * @author EmptyDreams
 */
@SuppressWarnings("deprecation")
public class NetWorkRegistryEvent extends Event {
	
	/** 注册信息类型 */
	public void registry(IMessageHandle<?>... handles) {
		for (IMessageHandle<?> handle : handles) {
			MessageRegister.registry(handle);
		}
	}
	
}
