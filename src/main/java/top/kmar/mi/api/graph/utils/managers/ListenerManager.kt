package top.kmar.mi.api.graph.utils.managers

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

    operator fun invoke(clazz: Class<out IListener>, data: IListenerData, writer: IDataWriter) {
        for (value in listenerList) {
            if (clazz == value.javaClass) {
                val op = value(data)
                writer.writeBoolean(op.isNotEmpty)
                if (op.isNotEmpty) writer.writeData(op)
            }
        }
    }

}