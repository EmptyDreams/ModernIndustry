package top.kmar.mi.api.graph.utils

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.interfaces.IPaneClient
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

    private val container = LinkedList<IPaneClient>()

    override fun add(pane: IPanel) {
        container.add(pane as IPaneClient)
    }

    override fun remove(pane: IPanel) {
        container.remove(pane)
    }

    override fun forEachClient(consumer: (IPaneClient) -> Unit) {
        container.forEach { consumer(it) }
    }

}