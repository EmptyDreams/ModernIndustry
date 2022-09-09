package top.kmar.mi.api.graph.utils.managers

import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import java.util.*

/**
 * 事件管理器
 * @author EmptyDreams
 */
class ListenerManager {

    private val listenerList = LinkedList<IListener>()

    fun registryListener(listener: IListener) {
        listenerList.add(listener)
    }

    fun removeListener(clazz: Class<out IListener>) {
        listenerList.removeIf { it::class.java == clazz }
    }

    fun sync(reader: IDataReader) {
        for (value in listenerList) {

        }
    }

    /** 触发指定事件 */
    operator fun invoke(clazz: Class<out IListener>, `data`: IListenerData, writer: IDataWriter) {
        val flag = BitSet(listenerList.size)
        val content = ByteDataOperator()
        for ((index, value) in listenerList.withIndex()) {
            if (clazz == value.javaClass) {
                val op = value(`data`)
                if (op.isSync) {
                    flag.set(index)
                    op.write(content)
                }
            }
        }

    }

}