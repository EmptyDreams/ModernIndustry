package top.kmar.mi.api.graph.listeners

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter

/**
 * 事件数据
 * @author EmptyDreams
 */
interface IListenerData {

    /** 是否需要网络同步 */
    val isNeedSync: Boolean

    /** 将数据写入到[IDataWriter] */
    fun wirte(writer: IDataWriter)

    /** 从[IDataReader]中读取数据 */
    fun read(reader: IDataReader)

}