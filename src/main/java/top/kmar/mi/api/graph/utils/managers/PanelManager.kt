package top.kmar.mi.api.graph.utils.managers

import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.utils.GeneralPanel
import java.util.*

/**
 * 通用[IPanelContainer]
 * @author EmptyDreams
 */
open class PanelManager : IPanelContainer, GeneralPanel() {

    private val container = LinkedList<IPanel>()

    override fun add(pane: IPanel) {
        container.add(pane)
    }

    override fun remove(pane: IPanel) {
        container.remove(pane)
    }

    override fun forEach(consumer: (IPanel) -> Unit) {
        container.forEach { consumer(it) }
    }

}