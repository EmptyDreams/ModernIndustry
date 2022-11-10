package top.kmar.mi.api.net.messages.player

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase

/**
 * 用于在服务端/客户端接收到玩家信息时处理相关内容
 * @author EmptyDreams
 */
interface IPlayerHandler {

    /**
     * 处理信息
     * @param player 发送（接收）消息的玩家
     * @param data 数据内容
     */
    fun apply(player: EntityPlayer, data: NBTBase)

}