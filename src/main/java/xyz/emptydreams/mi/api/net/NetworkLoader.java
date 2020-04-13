package xyz.emptydreams.mi.api.net;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.net.guinet.GUIMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * 网络信息传递总注册器
 * @author EmptyDremas
 * @version V1.0
 */
public final class NetworkLoader {

	private static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(ModernIndustry.MODID);
	
	private int nextID = -1;
	 
	public NetworkLoader() {
		registerMessage(MessageBase.ClientHandler.class, MessageBase.class, CLIENT);
		registerMessage(GUIMessage.ClientHandler.class, GUIMessage.class, CLIENT);
		registerMessage(MessageBase.ServiceHandler.class, MessageBase.class, SERVER);
	}
	
	private <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, ++nextID, side);
	}
	
	public static SimpleNetworkWrapper instance() {
		return instance;
	}
	
}
