package top.kmar.mi.api.graph.utils.managers

import net.minecraft.inventory.Slot
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.graph.utils.GeneralPanel
import java.util.*

/**
 * 通用[IPanelContainer]
 * @author EmptyDreams
 */
open class PanelManager(
    val x: Int, val y: Int,
    private var index: Int
) : IPanelContainer, GeneralPanel() {

    private val container = LinkedList<IPanel>()
    private var slotList: MutableList<Slot>? = LinkedList()

    override fun addSlot(creater: (Int) -> Slot): Slot {
        val slot = creater(index++).apply {
            xPos += x
            yPos += y
        }
        slotList!!.add(slot)
        return slot
    }

    override fun add(pane: IPanel) {
        container.add(pane)
    }

    override fun remove(pane: IPanel) {
        container.remove(pane)
    }

    override fun forEachPanels(consumer: (IPanel) -> Unit) {
        container.forEach { consumer(it) }
    }

    override fun onAdd2Container(father: IPanelContainer) {
        super<GeneralPanel>.onAdd2Container(father)
        super<IPanelContainer>.onAdd2Container(father)
        slotList!!.forEach { slot -> father.addSlot { slot } }
        slotList = null
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        super<GeneralPanel>.onRemoveFromContainer(father)
        super<IPanelContainer>.onRemoveFromContainer(father)
    }

    override fun activeListener(clazz: Class<out IListener>, `data`: IListenerData, writer: IDataWriter) {
        super<GeneralPanel>.activeListener(clazz, `data`, writer)
        super<IPanelContainer>.activeListener(clazz, `data`, writer)
    }

}