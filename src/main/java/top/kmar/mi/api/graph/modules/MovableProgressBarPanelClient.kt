package top.kmar.mi.api.graph.modules

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.graph.interfaces.IProgressBarPanel
import top.kmar.mi.api.graph.utils.GeneralPanelClient
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.gui.client.RuntimeTexture
import top.kmar.mi.api.utils.data.math.Size2D
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

/**
 * 尺寸可活动的进度条
 * @author EmptyDreams
 */
class MovableProgressBarPanelClient(
    x: Int, y: Int, width: Int, height: Int,
    private val painter: (MovableProgressBarPanelClient, GuiPainter) -> Unit
) : GeneralPanelClient(x, y, width, height), IProgressBarPanel {

    override var maxValue: Int = 0
    override var value: Int = 0

    /**
     * 绘制图像
     * @throws IllegalArgumentException 如果控件高度小于 2
     */
    override fun paint(painter: GuiPainter) {
        if (height < 2) throw IllegalArgumentException("控件的高度不应小于 2 [$height]")
        this.painter(this, painter)
    }

    companion object {

        private val srcCacheMap = Object2ObjectOpenHashMap<Size2D, RuntimeTexture>()
        private val fillCacheMap = Object2ObjectOpenHashMap<Size2D, RuntimeTexture>()

        /** 绘制矩形进度条（从左到右） */
        @JvmStatic
        fun drawRectRight(panel: MovableProgressBarPanelClient, painter: GuiPainter) {
            val src = srcCacheMap.computeIfAbsent(panel.size) {
                panel.run {
                    val image = BufferedImage(width, height, 6)
                    val graphics = image.createGraphics()
                    graphics.color = Color(139, 139, 139)
                    graphics.fillRect(0, 0, width, height - 1)
                    graphics.color = Color(104, 104, 104)
                    graphics.drawLine(0, height - 1, width - 1, height - 1)
                    graphics.dispose()
                    RuntimeTexture.instanceNoCache(image)
                }
            }
            val fill = fillCacheMap.computeIfAbsent(panel.size) {
                panel.run {
                    val image = BufferedImage(width, height - 1, 6)
                    val graphics = image.createGraphics()
                    graphics.color = Color.WHITE
                    graphics.fillRect(0, 0, width, height - 1)
                    graphics.dispose()
                    RuntimeTexture.instanceNoCache(image)
                }
            }
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