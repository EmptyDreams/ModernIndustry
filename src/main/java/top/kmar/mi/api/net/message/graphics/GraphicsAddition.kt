package top.kmar.mi.api.net.message.graphics

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagString
import top.kmar.mi.api.net.message.IMessageAddition

/**
 * GUI控件网络通信的附加信息
 * @author EmptyDreams
 */
class GraphicsAddition(
    /** 传输信息的控件的ID */
    var id: String = ""
) : IMessageAddition {

    override fun writeTo() = NBTTagString(id)

    override fun readFrom(nbt: NBTBase) {
        id = (nbt as NBTTagString).string
    }

}