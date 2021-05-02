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
import xyz.emptydreams.mi.api.register.others.AutoLoader;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * 网络信息传递总注册器
 * @author EmptyDremas
 */
@AutoLoader
public final class NetworkLoader {

	private static final SimpleNetworkWrapper instance =
			NetworkRegistry.INSTANCE.newSimpleChannel(ModernIndustry.MODID);
	
	public static SimpleNetworkWrapper instance() {
		return instance;
	}
	
	/** 存储当前ID分配位点 */
	private static int nextID = -1;
	
	static {
		registerMessage(ServerHandler.class, CommonMessage.class, SERVER);
		registerMessage(ClientHandler.class, CommonMessage.class, CLIENT);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler,
			Class<REQ> requestMessageType, Side side) {
		instance.registerMessage(messageHandler, requestMessageType, ++nextID, side);
	}
	
	private NetworkLoader() {
		throw new AssertionError("不应该调用该构造函数");
	}
	
}