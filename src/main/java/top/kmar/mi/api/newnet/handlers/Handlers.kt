package top.kmar.mi.api.newnet.handlers

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import top.kmar.mi.api.newnet.CommonMessage

typealias RetryMessage = CommonMessage

/**
 * 服务端信息处理器
 * @author EmptyDreams
 */
class ServerHandler : IMessageHandler<CommonMessage, RetryMessage> {

    override fun onMessage(message: CommonMessage, ctx: MessageContext): RetryMessage? {
        val handler = MessageHandlerRegedit.find(message.key)
        return handler.parse(message.data, ctx)
    }

}

/**
 * 客户端信息处理器
 * @author EmptyDreams
 */
class ClientHandler : IMessageHandler<CommonMessage, RetryMessage> {

    override fun onMessage(message: CommonMessage, ctx: MessageContext): RetryMessage? {
        val handler = MessageHandlerRegedit.find(message.key)
        return handler.parse(message.data, ctx)
    }

}