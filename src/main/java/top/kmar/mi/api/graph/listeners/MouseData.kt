package top.kmar.mi.api.graph.listeners

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter

data class MouseData(
    var mouseX: Float,
    var mouseY: Float,
    var code: Int,
    var wheel: Int
) : IListenerData {

    override fun wirte(writer: IDataWriter) {
        writer.writeFloat(mouseX)
        writer.writeFloat(mouseY)
        writer.writeVarInt(code)
        writer.writeVarInt(wheel)
    }

    override fun read(reader: IDataReader) {
        mouseX = reader.readFloat()
        mouseY = reader.readFloat()
        code = reader.readVarInt()
        wheel = reader.readVarInt()
    }

}