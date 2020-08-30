package xyz.emptydreams.mi.api.newnet.handler;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public class ClientHandler implements IMessageHandler<xyz.emptydreams.mi.api.newnet.handler.CommonMessage, IMessage> {
	
	@Override
	public IMessage onMessage(xyz.emptydreams.mi.api.newnet.handler.CommonMessage message, MessageContext ctx) {
		message.parseClient();
		return null;
	}
	
}
