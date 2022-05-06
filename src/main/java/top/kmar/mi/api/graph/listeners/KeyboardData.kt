package top.kmar.mi.api.graph.listeners

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter

data class KeyboardData(
    var code: Int,
    var isFocus: Boolean
) : IListenerData {

    override fun wirte(writer: IDataWriter) {
        writer.writeVarInt(code)
        writer.writeBoolean(isFocus)
    }

    override fun read(reader: IDataReader) {
        code = reader.readVarInt()
        isFocus = reader.readBoolean()
    }

}