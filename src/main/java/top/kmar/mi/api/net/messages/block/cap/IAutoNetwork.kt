package top.kmar.mi.api.net.messages.block.cap

import net.minecraft.nbt.NBTBase
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * 基于方块的自动化网络通信接口
 *
 * 应当由
 *
 * @author EmptyDreams
 */
fun interface IAutoNetwork {

    /**
     * 处理从另一端发送过来的信息
     * @param nbt 接收到的信息
     * @param ctx 网络信息
     */
    fun receive(nbt: NBTBase, ctx: MessageContext)

}