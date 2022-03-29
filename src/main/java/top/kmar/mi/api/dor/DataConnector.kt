package top.kmar.mi.api.dor

import top.kmar.mi.api.dor.interfaces.IDataReader
import java.util.*

/**
 * `dor`连接器
 *
 * **遍历该列表时会清空内容**
 *
 * @author EmptyDreams
 */
class DataConnector : Iterable<IDataReader> {

    private val list = LinkedList<IDataReader>()
    /** 用于标记该`connector`是否为无效的 */
    var isInvalidate = false
        private set

    /** 将指定`dor`推入到连接器尾部 */
    fun pushBack(operator: LockDataOperator) {
        operator.lock()
        list.addLast(operator)
    }

    /** 将指定`dor`推入到连接器的头部 */
    fun pushFront(operator: LockDataOperator) {
        operator.lock()
        list.addFirst(operator)
    }

    /** 读取并删除头部`dor` */
    fun popFront(): IDataReader {
        if (isInvalidate) throw IllegalStateException("该连接器已经被废弃")
        val result = list.pollFirst()
        if (list.isEmpty()) isInvalidate = true
        return result
    }

    override fun iterator(): Iterator<IDataReader> = ConnectorIterator()

    inner class ConnectorIterator : Iterator<IDataReader> {

        override fun hasNext(): Boolean = list.isNotEmpty()

        override fun next(): IDataReader = popFront()

    }

}