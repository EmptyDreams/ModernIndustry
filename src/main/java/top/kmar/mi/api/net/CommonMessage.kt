package top.kmar.mi.api.net

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import top.kmar.mi.api.utils.expands.readNbt
import top.kmar.mi.api.utils.expands.readString
import top.kmar.mi.api.utils.expands.writeNbt
import top.kmar.mi.api.utils.expands.writeString

/**
 * 通用信息
 * @author EmptyDreams
 */
class CommonMessage(
    key: String, data: NBTBase
) : IMessage {

    /** 处理该信息的处理器的 key */
    var key = key
        private set
    /** 要传递的信息 */
    var data = data
        private set

    override fun fromBytes(buf: ByteBuf) {
        key = buf.readString()
        data = buf.readNbt()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeString(key)
        buf.writeNbt(data)
    }

}