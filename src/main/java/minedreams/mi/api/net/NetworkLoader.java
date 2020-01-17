package minedreams.mi.api.net;

import minedreams.mi.ModernIndustry;
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
		registerMessage(MessageBase.ClientHandler.class, CLIENT);
		registerMessage(MessageBase.ServiceHandler.class, SERVER);
	}
	
	private <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Side side) {
		instance.registerMessage(messageHandler, (Class<REQ>) MessageBase.class, ++nextID, side);
	}
	
	public static SimpleNetworkWrapper instance() {
		return instance;
	}
	
}
