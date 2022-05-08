package top.kmar.mi.api.graph.interfaces

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.graph.listeners.IListener
import top.kmar.mi.api.graph.listeners.IListenerData
import top.kmar.mi.api.graph.utils.GuiPainter

/**
 * 具有管理控件能力的控件的客户端接口
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
interface IPanelContainerClient : IPanelClient, IPanelContainer {

    override fun isClient(): Boolean = true

    override fun forEach(consumer: (IPanel) -> Unit) {
        forEachClient(consumer)
    }

    fun forEachClient(consumer: (IPanelClient) -> Unit)

    override fun paint(painter: GuiPainter) {
        forEachClient { painter.paintPanel(it) }
    }

    override fun onAdd2Container(father: IPanelContainer) {
        forEach { it.onAdd2Container(father) }
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        forEach { it.onRemoveFromContainer(father) }
    }

    override fun activeListener(clazz: Class<out IListener>, data: IListenerData) {
        forEach { it.activeListener(clazz, data) }
    }

    override fun send(writer: IDataWriter): Boolean {
        var result = false
        forEach { if (it.send(writer)) result = true }
        return result
    }

    override fun receive(reader: IDataReader) {
        forEach { it.receive(reader) }
    }

}