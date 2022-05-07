package top.kmar.mi.api.graph.utils

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.interfaces.IPanelClient
import top.kmar.mi.api.graph.interfaces.IPanel
import top.kmar.mi.api.graph.interfaces.IPanelContainerClient
import java.util.*

/**
 * 客户端控件管理器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
open class PanelManagerClient(
    x: Int, y: Int, width: Int, height: Int
) : IPanelContainerClient, GeneralPanelClient(x, y, width, height) {

    constructor(builder: PanelBuilder) : this(builder.x, builder.y, builder.width, builder.height)

    private val container = LinkedList<IPanelClient>()

    override fun add(pane: IPanel) {
        container.add(pane as IPanelClient)
    }

    override fun remove(pane: IPanel) {
        container.remove(pane)
    }

    override fun forEachClient(consumer: (IPanelClient) -> Unit) {
        container.forEach { consumer(it) }
    }

}