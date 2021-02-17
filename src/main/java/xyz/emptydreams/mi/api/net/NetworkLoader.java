package xyz.emptydreams.mi.api.net;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.net.handler.ClientHandler;
import xyz.emptydreams.mi.api.net.handler.CommonMessage;
import xyz.emptydreams.mi.api.net.handler.ServerHandler;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * 网络信息传递总注册器
 * @author EmptyDremas
 */
public final class NetworkLoader {

	private static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(ModernIndustry.MODID);
	
	private int nextID = -1;
	 
	public NetworkLoader() {
		if (WorldUtil.isClient()) {
			registerMessage(ClientHandler.class, CommonMessage.class, CLIENT);
		} else {
			registerMessage(ServerHandler.class, CommonMessage.class, SERVER);
		}
	}
	
	private <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, ++nextID, side);
	}
	
	public static SimpleNetworkWrapper instance() {
		return instance;
	}
	
}