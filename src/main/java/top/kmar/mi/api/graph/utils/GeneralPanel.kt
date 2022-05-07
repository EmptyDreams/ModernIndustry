package top.kmar.mi.api.graph.utils

import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.graph.utils.managers.ListenerManager

/**
 * 通用Panel
 * @author EmptyDreams
 */
open class GeneralPanel : IPanel {

    private val listenerList = ListenerManager()

    override fun onAdd2Container(father: IPanelContainer) {}

    override fun onRemoveFromContainer(father: IPanelContainer) {}

    override fun registryListener(listener: IListener) {
        listenerList.registryListener(listener)
    }

    override fun removeListener(clazz: Class<out IListener>) {
        listenerList.removeListener(clazz)
    }

    override fun activeListener(clazz: Class<out IListener>, data: IListenerData) {
        listenerList(clazz, data)
    }

}