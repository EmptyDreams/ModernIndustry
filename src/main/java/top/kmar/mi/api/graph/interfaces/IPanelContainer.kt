package top.kmar.mi.api.graph.interfaces

import net.minecraft.inventory.Slot
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.net.message.panel.PanelAddition

/**
 * 具有管理控件能力的控件接口
 * @author EmptyDreams
 */
interface IPanelContainer : IPanel {

    /** 添加一个slot */
    fun addSlot(creater: (Int) -> Slot): Slot

    /** 添加一个控件 */
    fun add(pane: IPanel)

    /** 移除指定控件 */
    fun remove(pane: IPanel)

    /** 遍历所有控件 */
    fun forEachPanels(consumer: (IPanel) -> Unit)

    override fun onAdd2Container(father: IPanelContainer) {
        forEachPanels { it.onAdd2Container(father) }
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        forEachPanels { it.onRemoveFromContainer(father) }
    }

    override fun activeListener(clazz: Class<out IListener>, `data`: IListenerData, writer: IDataWriter) {
        forEachPanels {
            val op = ByteDataOperator()
            it.activeListener(clazz, `data`, op)
            writer.writeBoolean(op.isNotEmpty)
            if (op.isNotEmpty) writer.writeData(op)
        }
    }

    override fun send(writer: IDataWriter): Boolean {
        var result = false
        forEachPanels { if (it.send(writer)) result = true }
        return result
    }

    override fun receive(type: PanelAddition.Type, reader: IDataReader) {
        forEachPanels { it.receive(type, reader) }
    }

}