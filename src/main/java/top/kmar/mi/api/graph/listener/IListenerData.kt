package top.kmar.mi.api.graph.listener

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter

/**
 * 事件数据
 * @author EmptyDreams
 */
interface IListenerData {

    /** 将数据写入到[IDataWriter] */
    fun wirte(writer: IDataWriter)

    /** 从[IDataReader]中读取数据 */
    fun read(reader: IDataReader)

}