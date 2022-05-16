package top.kmar.mi.api.graph.utils.managers

import net.minecraft.inventory.Slot
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.interfaces.IPanelClient
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.interfaces.IPanelContainerClient
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.graph.utils.GeneralPanelClient
import java.util.*

/**
 * 客户端控件管理器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
open class PanelManagerClient(
    x: Int, y: Int, width: Int, height: Int, var index: Int
) : IPanelContainerClient, GeneralPanelClient(x, y, width, height) {

    private val container = LinkedList<IPanelClient>()
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
        container.add(pane as IPanelClient)
    }

    override fun remove(pane: IPanel) {
        container.remove(pane)
    }

    override fun forEachClient(consumer: (IPanelClient) -> Unit) {
        container.forEach { consumer(it) }
    }

    override fun onAdd2Container(father: IPanelContainer) {
        super<GeneralPanelClient>.onAdd2Container(father)
        super<IPanelContainerClient>.onAdd2Container(father)
        slotList!!.forEach { slot -> father.addSlot { slot } }
        slotList = null
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        super<GeneralPanelClient>.onRemoveFromContainer(father)
        super<IPanelContainerClient>.onRemoveFromContainer(father)
    }

    override fun activeListener(clazz: Class<out IListener>, data: IListenerData, writer: IDataWriter) {
        super<GeneralPanelClient>.activeListener(clazz, data, writer)
        super<IPanelContainerClient>.activeListener(clazz, data, writer)
    }

}