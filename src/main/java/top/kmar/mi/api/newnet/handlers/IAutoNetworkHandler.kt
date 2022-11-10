package top.kmar.mi.api.newnet.handlers

import net.minecraft.nbt.NBTBase
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * 信息处理器接口
 * @author EmptyDreams
 */
interface IAutoNetworkHandler {

    /**
     * 处理从另一端发送过来的信息
     * @return 要返回给对方的信息，不需要返回则返回 `null`
     */
    fun parse(message: NBTBase, ctx: MessageContext): RetryMessage?

}