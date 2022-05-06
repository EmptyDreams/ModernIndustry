package top.kmar.mi.api.graph.utils

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

    operator fun invoke(clazz: Class<out IListener>, data: IListenerData) {
        for (value in listenerList) {
            if (value::class.java == clazz) value(data)
        }
    }

}