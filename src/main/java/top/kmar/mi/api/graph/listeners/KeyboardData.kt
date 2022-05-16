package top.kmar.mi.api.graph.listeners

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter

/**
 * 键盘事件信息
 * @author EmptyDreams
 */
data class KeyboardData(
    var code: Int,
    var isFocus: Boolean,
    override val isSync: Boolean
) : IListenerData {

    override fun write(writer: IDataWriter) {
        writer.writeVarInt(code)
        writer.writeBoolean(isFocus)
    }

    override fun read(reader: IDataReader) {
        code = reader.readVarInt()
        isFocus = reader.readBoolean()
    }

}