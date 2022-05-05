package top.kmar.mi.api.graph.interfaces

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.client.GuiPainter

/**
 * 具有管理控件能力的控件的客户端接口
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
interface IPanelContainerClient : IPaneClient, IPanelContainer {

    override fun isClient(): Boolean = true

    override fun forEach(consumer: (IPanel) -> Unit) {
        forEachClient(consumer)
    }

    fun forEachClient(consumer: (IPaneClient) -> Unit)

    override fun paint(painter: GuiPainter) {
        forEachClient { painter.paintPanel(it) }
    }

}