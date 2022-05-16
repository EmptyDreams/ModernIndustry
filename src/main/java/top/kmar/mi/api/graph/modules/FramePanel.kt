package top.kmar.mi.api.graph.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.graph.utils.managers.PanelManager

/**
 * GUI窗体
 * @author EmptyDreams
 */
open class FramePanel : Container(), IPanelContainer {

    private val panelsManager = PanelManager(0, 0, 0)

    override fun canInteractWith(playerIn: EntityPlayer) = true

    override fun addSlot(creater: (Int) -> Slot): Slot {
        return super.addSlotToContainer(creater(inventorySlots.size))
    }

    override fun add(pane: IPanel) = panelsManager.add(pane)

    override fun remove(pane: IPanel) = panelsManager.remove(pane)

    override fun forEach(consumer: (IPanel) -> Unit) = panelsManager.forEach(consumer)

    override fun registryListener(listener: IListener) = panelsManager.registryListener(listener)

    override fun removeListener(clazz: Class<out IListener>) = panelsManager.removeListener(clazz)

    override fun onAdd2Container(father: IPanelContainer) = panelsManager.onAdd2Container(father)

    override fun onRemoveFromContainer(father: IPanelContainer) = panelsManager.onRemoveFromContainer(father)

    override fun activeListener(clazz: Class<out IListener>, data: IListenerData, writer: IDataWriter) {
        panelsManager.activeListener(clazz, data, writer)
    }

    override fun send(writer: IDataWriter) = panelsManager.send(writer)

    override fun receive(reader: IDataReader) = panelsManager.receive(reader)

}