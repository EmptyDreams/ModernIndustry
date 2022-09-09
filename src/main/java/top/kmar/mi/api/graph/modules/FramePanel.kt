package top.kmar.mi.api.graph.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.interfaces.ISlotPanel
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.graph.utils.managers.PanelManager
import top.kmar.mi.api.net.handler.MessageSender
import top.kmar.mi.api.net.message.panel.PanelAddition
import top.kmar.mi.api.net.message.panel.PanelAddition.Type
import top.kmar.mi.api.net.message.panel.PanelMessage
import java.util.*

/**
 * GUI窗体
 * @author EmptyDreams
 */
open class FramePanel(val player: EntityPlayer) : Container(), IPanelContainer {

    private val panelsManager = PanelManager(0, 0, 0)
    private val slotPanelList = TreeSet<ISlotPanel> { o1, o2 -> o1.startIndex.compareTo(o2.startIndex) }

    override fun canInteractWith(playerIn: EntityPlayer) = true

    override fun addSlot(creater: (Int) -> Slot): Slot {
        return super.addSlotToContainer(creater(inventorySlots.size))
    }

    override fun add(pane: IPanel) {
        panelsManager.add(pane)
        if (pane is ISlotPanel) slotPanelList.add(pane)
    }

    override fun remove(pane: IPanel) = panelsManager.remove(pane)

    override fun forEachPanels(consumer: (IPanel) -> Unit) = panelsManager.forEachPanels(consumer)

    override fun registryListener(listener: IListener) = panelsManager.registryListener(listener)

    override fun removeListener(clazz: Class<out IListener>) = panelsManager.removeListener(clazz)

    override fun onAdd2Container(father: IPanelContainer) = panelsManager.onAdd2Container(father)

    override fun onRemoveFromContainer(father: IPanelContainer) = panelsManager.onRemoveFromContainer(father)

    override fun activeListener(clazz: Class<out IListener>, `data`: IListenerData, writer: IDataWriter) {
        panelsManager.activeListener(clazz, `data`, writer)
    }

    /** 把数据发送到客户端 */
    private fun send2Client(`data`: IDataReader, type: Type) {
        val message = PanelMessage.create(`data`, PanelAddition(type, player))
        MessageSender.sendToClient(player as EntityPlayerMP, message)
    }

    override fun send(writer: IDataWriter) = panelsManager.send(writer)

    override fun receive(type: Type, reader: IDataReader) = panelsManager.receive(type, reader)

    /**
     * 触发指定事件并进行网络通信
     * @see activeListener
     */
    fun activeListenerWithSync(clazz: Class<out IListener>, data: IListenerData) {
        val writer = ByteDataOperator()
        activeListener(clazz, data, writer)
        if (writer.isNotEmpty) send2Client(writer, Type.LISTENER)
    }

    /** 每Tick触发一次同步 */
    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        val writer = ByteDataOperator()
        val flag = send(writer)
        if (flag) send2Client(writer, Type.TICK)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var distPanel: ISlotPanel? = null
        for (panel in slotPanelList) {
            if (index in panel) {
                distPanel = panel
                break
            }
        }
        if (distPanel == null) return ItemStack.EMPTY
        val slot = inventorySlots[index]
        if (!slot.hasStack) return ItemStack.EMPTY
        var stack = slot.stack.copy()
        val oldStack = stack.copy()
        for (panel in slotPanelList) {
            if (panel === distPanel) continue
            stack = panel.putStack(stack, false)
        }
        if (stack.count == oldStack.count) return ItemStack.EMPTY
        slot.putStack(stack)
        return oldStack
    }

}