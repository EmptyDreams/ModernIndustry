package top.kmar.mi.api.net.message.graphics

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.net.message.IMessageAddition

/**
 * GUI控件网络通信的附加信息
 * @author EmptyDreams
 */
class GraphicsAddition(
    /** 传输信息的控件的ID */
    var id: String = ""
) : IMessageAddition {

    override fun writeTo(writer: IDataWriter) {
        writer.writeString(id)
    }

    override fun readFrom(reader: IDataReader) {
        id = reader.readString()
    }

}