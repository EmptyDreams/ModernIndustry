package top.kmar.mi.api.graph.modules

import top.kmar.mi.api.graph.interfaces.IProgressBarPanel
import top.kmar.mi.api.graph.utils.GeneralPanelClient
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.graph.utils.managers.TextureCacheManager
import java.awt.Color
import kotlin.math.roundToInt

/**
 * 尺寸可活动的进度条
 * @author EmptyDreams
 */
open class MovableProgressBarPanelClient(
    x: Int, y: Int, width: Int, height: Int,
    private val painter: (MovableProgressBarPanelClient, GuiPainter) -> Unit = Companion::drawRectRight
) : GeneralPanelClient(x, y, width, height), IProgressBarPanel {

    override var maxValue: Int = 0
    override var value: Int = 0
    override var showText: (IProgressBarPanel, GuiPainter) -> Unit = IProgressBarPanel.Companion::noText

    /**
     * 绘制图像
     * @throws IllegalArgumentException 如果控件高度小于 2
     */
    override fun paint(painter: GuiPainter) {
        if (height < 2) throw IllegalArgumentException("控件的高度不应小于 2 [$height]")
        this.painter(this, painter)
        showText(this, painter)
    }

    companion object {

        val srcCacheMap = TextureCacheManager { size, graphics ->
            with(graphics) {
                color = Color(139, 139, 139)
                fillRect(0, 0, size.width, size.height - 1)
                color = Color(104, 104, 104)
                drawLine(0, size.height - 1, size.width - 1, size.height - 1)
            }
        }
        val fillCacheMap = TextureCacheManager { size, graphics ->
            graphics.color = Color.WHITE
            graphics.fillRect(0, 0, size.width, size.height - 1)
        }

        /** 绘制矩形进度条（从左到右） */
        @JvmStatic
        fun drawRectRight(panel: MovableProgressBarPanelClient, painter: GuiPainter) {
            val src = srcCacheMap[panel.size]
            val fill = fillCacheMap[panel.size]
            val fillWidth = (panel.percent * panel.width).roundToInt()
            src.bindTexture()
            painter.drawTexture(0, 0, panel.width, panel.height, src)
            if (fillWidth != 0) {
                fill.bindTexture()
                painter.drawTexture(0, 0, fillWidth, panel.height, fill)
            }
        }

    }

}