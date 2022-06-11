package top.kmar.mi.api.net.message.panel

import net.minecraft.entity.player.EntityPlayer
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.net.message.IMessageAddition
import top.kmar.mi.api.utils.WorldUtil.*
import top.kmar.mi.api.utils.isClient

/**
 * Panel网络通信的附加数据
 * @author EmptyDreams
 */
class PanelAddition(
    /** 数据类型 */
    type: Type? = null,
    /** 目标玩家 */
    player: EntityPlayer? = null
) : IMessageAddition {

    var player: EntityPlayer?
        private set
    var type: Type?
        private set

    init {
        this.player = player
        this.type = type
    }

    override fun writeTo(writer: IDataWriter) {
        writer.writeChar(type!!.value)
        if (player!!.world.isClient()) writer.writeUuid(player!!.uniqueID)
    }

    override fun readFrom(reader: IDataReader) {
        type = Type.valueOf(reader.readChar())
        player = if (isServer()) getPlayer(reader.readUuid()) else getPlayerAtClient()
    }

    enum class Type(val value: Char) {

        /** TICK通信 */
        TICK('t'),
        /** 事件通信 */
        LISTENER('l');

        companion object {

            @JvmStatic
            fun valueOf(value: Char): Type {
                for (type in values()) {
                    if (type.value == value) return type
                }
                throw IllegalArgumentException("未知的类型[$value]")
            }

        }

    }

}