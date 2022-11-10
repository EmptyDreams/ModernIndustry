package top.kmar.mi.api.net.messages.player

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.net.CommonMessage
import top.kmar.mi.api.net.NetworkLoader
import top.kmar.mi.api.net.handlers.IAutoNetworkHandler
import top.kmar.mi.api.net.handlers.MessageHandlerRegedit
import top.kmar.mi.api.net.handlers.RetryMessage
import top.kmar.mi.api.regedits.others.AutoLoader
import top.kmar.mi.api.utils.TickHelper
import top.kmar.mi.api.utils.expands.clientPlayer

/**
 * 基于玩家的网络交互
 * @author EmptyDreams
 */
@AutoLoader
object PlayerMessage : IAutoNetworkHandler {

    const val key = "mi:player"

    init {
        MessageHandlerRegedit.registry(key, this)
    }

    /** 发送信息到服务端 */
    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun sendToServer(key: String, data: NBTBase) {
        val message = packing(clientPlayer, key, data)
        NetworkLoader.instance.sendToServer(message)
    }

    /** 发送信息到客户端 */
    @JvmStatic
    fun sendToClient(player: EntityPlayer, key: String, data: NBTBase) {
        val message = packing(player, key, data)
        NetworkLoader.instance.sendTo(message, player as EntityPlayerMP)
    }

    private fun packing(player: EntityPlayer, key: String, data: NBTBase): IMessage {
        val message = NBTTagCompound().apply {
            setString("key", key)
            setTag("data", data)
        }
        return CommonMessage(this.key, message)
    }

    override fun parse(message: NBTBase, ctx: MessageContext): RetryMessage? {
        message as NBTTagCompound
        val player = if (ctx.side.isServer) ctx.serverHandler.player else clientPlayer
        val key = message.getString("key")
        val data = message.getTag("data")
        TickHelper.addAutoTask {
            PlayerHandlerRegedit.apply(key, player, data)
            true
        }
        return null
    }

}