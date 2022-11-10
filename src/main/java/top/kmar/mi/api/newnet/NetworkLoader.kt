package top.kmar.mi.api.newnet

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.newnet.handlers.ClientHandler
import top.kmar.mi.api.newnet.handlers.ServerHandler
import top.kmar.mi.api.regedits.others.AutoLoader
import java.util.concurrent.atomic.AtomicInteger

/**
 * 自动化网络通信的 loader
 * @author EmptyDreams
 */
@AutoLoader
object NetworkLoader {

    @JvmStatic
    val instance: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModernIndustry.MODID)

    init {
        registerMessage(ServerHandler::class.java, CommonMessage::class.java, Side.SERVER)
        registerMessage(ClientHandler::class.java, CommonMessage::class.java, Side.CLIENT)
    }

    private val idIndex = AtomicInteger(0)

    private fun <REQ : IMessage?, REPLY : IMessage?> registerMessage(
        messageHandler: Class<out IMessageHandler<REQ, REPLY>?>,
        requestMessageType: Class<REQ>, side: Side
    ) {
        instance.registerMessage(messageHandler, requestMessageType, idIndex.getAndIncrement(), side)
    }

}