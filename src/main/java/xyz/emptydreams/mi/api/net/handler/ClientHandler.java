package xyz.emptydreams.mi.api.net.handler;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.emptydreams.mi.api.utils.MISysInfo;

/**
 * @author EmptyDreams
 */
public class ClientHandler implements IMessageHandler<CommonMessage, IMessage> {
	
	@Override
	public IMessage onMessage(CommonMessage message, MessageContext ctx) {
		message.parseClient();
		return null;
	}
	
}