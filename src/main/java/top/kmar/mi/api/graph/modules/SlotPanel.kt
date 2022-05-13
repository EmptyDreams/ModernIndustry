package top.kmar.mi.api.graph.modules

import net.minecraft.inventory.Slot
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.interfaces.IPanelContainer
import top.kmar.mi.api.graph.utils.GeneralPanel
import top.kmar.mi.api.graph.utils.GeneralPanelClient
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.graph.utils.managers.TextureCacheManager
import java.awt.Color

/**
 * 服务端[Slot]控件
 * @author EmptyDreams
 */
class SlotPanel(
    val x: Int,
    var y: Int,
    val length: Int,
    private val slotCreater: (SlotPanel, Int) -> Slot
) : GeneralPanel() {

    private var slot: Slot? = null

    override fun onAdd2Container(father: IPanelContainer) {
        if (slot != null) throw UnsupportedOperationException("[SlotPanel]不允许重复初始化")
        slot = father.addSlot { slotCreater(this, it) }
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        throw UnsupportedOperationException("[SlotPanel]不支持移除")
    }

}

/**
 * 客户端[Slot]控件
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class SlotPanelClient(
    x: Int, y: Int, length: Int,
    private val slotCreater: (SlotPanelClient, Int) -> Slot
) : GeneralPanelClient(x, y, length, length) {

    private var slot: Slot? = null

    override fun paint(painter: GuiPainter) {
        val texture = cacheManager[size].bindTexture()
        painter.drawTexture(0, 0, width, height, texture)
    }

    override fun onAdd2Container(father: IPanelContainer) {
        if (slot != null) throw UnsupportedOperationException("[SlotPanelClient]不允许重复初始化")
        slot = father.addSlot { slotCreater(this, it) }
    }

    override fun onRemoveFromContainer(father: IPanelContainer) {
        throw UnsupportedOperationException("[SlotPanel]不支持移除")
    }

    companion object {

        val fillColor = Color(139, 139, 139)
        val lightBorderColor: Color = Color.WHITE
        val darkBorderColor = Color(55, 55, 55)

        val cacheManager = TextureCacheManager { size, graphics ->
            with(graphics) {
                color = fillColor
                fillRect(0, 0, size.width, size.height)
                color = darkBorderColor
                drawLine(0, 0, size.width - 1, 0)
                drawLine(0, 1, 0, size.height - 2)
                color = lightBorderColor
                drawLine(1, size.height - 1, size.width - 1, size.height - 1)
                drawLine(size.width - 1, 1, size.width - 1, size.height - 2)
            }
        }

    }
}