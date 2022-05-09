package top.kmar.mi.api.net.message.panel

import net.minecraft.entity.player.EntityPlayer
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.net.message.IMessageAddition
import top.kmar.mi.api.utils.WorldUtil

/**
 *
 * @author EmptyDreams
 */
class PanelAddition(
    player: EntityPlayer? = null
) : IMessageAddition {

    var player: EntityPlayer?
        private set

    init {
        this.player = player
    }

    override fun writeTo(writer: IDataWriter) {
        writer.writeUuid(player!!.uniqueID)
    }

    override fun readFrom(reader: IDataReader) {
        player = WorldUtil.getPlayer(reader.readUuid())
    }

}