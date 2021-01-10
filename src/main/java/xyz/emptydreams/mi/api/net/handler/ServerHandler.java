package xyz.emptydreams.mi.api.net.handler;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 服务端接收器
 * @author EmptyDreams
 */
public class ServerHandler implements IMessageHandler<CommonMessage, IMessage> {
	
	@Override
	public IMessage onMessage(CommonMessage message, MessageContext ctx) {
		message.parseServer();
		return null;
	}
	
}