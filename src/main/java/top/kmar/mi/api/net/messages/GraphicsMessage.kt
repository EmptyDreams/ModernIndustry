package top.kmar.mi.api.net.messages

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.net.CommonMessage
import top.kmar.mi.api.net.NetworkLoader
import top.kmar.mi.api.net.handlers.IAutoNetworkHandler
import top.kmar.mi.api.net.handlers.MessageHandlerRegedit
import top.kmar.mi.api.net.handlers.RetryMessage
import top.kmar.mi.api.utils.MISysInfo
import top.kmar.mi.api.utils.TickHelper
import top.kmar.mi.api.utils.container.SideConcurrentQueue
import top.kmar.mi.api.utils.expands.clientPlayer
import top.kmar.mi.api.utils.expands.handleAll
import top.kmar.mi.api.utils.expands.isServer

/**
 * 基于 GUI 的网络通信
 * @author EmptyDreams
 */
object GraphicsMessage : IAutoNetworkHandler {

    const val key = "mi:graphics"
    /** 最大尝试次数 */
    const val maxTryCount = 50

    init {
        MessageHandlerRegedit.registry(key, this)
    }

    /** 发送信息到客户端 */
    @JvmStatic
    fun sendToClient(data: NBTBase, id: String, player: EntityPlayer) {
        val message = packing(data, id, player)
        NetworkLoader.instance.sendTo(message, player as EntityPlayerMP)
    }

    /** 发送信息到服务端 */
    @JvmStatic
    fun sendToServer(data: NBTBase, id: String) {
        val message = packing(data, id, Minecraft.getMinecraft().player)
        NetworkLoader.instance.sendToServer(message)
    }

    private fun packing(data: NBTBase, id: String, player: EntityPlayer): IMessage {
        val container = player.openContainer as BaseGraphics
        val message = NBTTagCompound().apply {
            setString("key", container.key.toString())
            setString("id", id)
            setTag("data", data)
        }
        return CommonMessage(key, message)
    }

    override fun parse(message: NBTBase, ctx: MessageContext): RetryMessage? {
        message as NBTTagCompound
        val key = message.getString("key")
        val data = message.getTag("data")
        val player = if (ctx.side.isServer) ctx.serverHandler.player else clientPlayer
        val id = message.getString("id")
        queue.add(Node(player, ResourceLocation(key), id, data))
        return null
    }

    private val queue = SideConcurrentQueue<Node>()

    init {
        TickHelper.addServerTask {
            queue.handleAll { parse(it) }
            false
        }
        if (FMLCommonHandler.instance().side.isClient) {
            TickHelper.addClientTask {
                queue.handleAll { parse(it) }
                false
            }
        }
    }

    private fun parse(node: Node): Boolean {
        val container = node.player.openContainer as? BaseGraphics
        if (container == null || container.key != node.key) {
            if (node.count >= maxTryCount) {
                MISysInfo.err("[GraphicsMessage] 信息由于玩家未打开对应的呃 GUI 而被抛弃：\n\t\t$node")
                return true
            }
            ++node.count
            queue.add(node)
            return false
        }
        val element = container.getElementByID(node.id)
        if (element == null) {
            if (node.count >= maxTryCount) {
                MISysInfo.err("[GraphicsMessage] 信息由于玩家打开的 GUI 中不包含指定 ID 而被抛弃：\n\t\t$node")
                return true
            }
            node.count += 5
            queue.add(node)
            return false
        }
        try {
            if (isServer()) element.receiveNetworkMessage(node.data as NBTTagCompound)
            else element.client.receive(node.data)
        } catch (e: Throwable) {
            MISysInfo.err("[GraphicsMessage] 处理信息的过程中发生异常", e)
        }
        return true
    }

    private data class Node(
        val player: EntityPlayer,
        val key: ResourceLocation,
        val id: String,
        val data: NBTBase
    ) {
        var count = 0
    }

}