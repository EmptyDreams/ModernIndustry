package top.kmar.mi.api.graph.interfaces

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.utils.data.math.Point2D

/**
 * 进度条的接口
 * @author EmptyDreams
 */
interface IProgressBarPanel {

    /** 最大值 */
    var maxValue: Int

    /** 当前值 */
    var value: Int

    /** 进度 */
    val percent: Double
        get() = value.toDouble() / maxValue

    /** 用于显示文字的函数 */
    @get:SideOnly(Side.CLIENT)
    @set:SideOnly(Side.CLIENT)
    var showText: ((IProgressBarPanel, GuiPainter) -> Unit)

    @SideOnly(Side.CLIENT)
    companion object {

        /** 不显示文字 */
        @JvmStatic
        fun noText(panel: IProgressBarPanel, painter: GuiPainter) {}

        /** 居中打印文字 */
        fun <T> showCenter(panel: T, painter: GuiPainter)
                where T : IProgressBarPanel, T : IPanelClient {
            drawString(painter, "${panel.value} / ${panel.maxValue}", 0) { width, height ->
                Point2D(
                    (panel.width - width).shr(1),
                    (panel.height - height).shr(1)
                )
            }
        }

        /** 上方居中打印 */
        fun <T> shwoUp(panel: T, painter: GuiPainter)
                where T : IProgressBarPanel, T : IPanelClient {
            drawString(painter, "${panel.value} / ${panel.maxValue}", 0) { width, height ->
                Point2D((panel.width - width).shr(1), -height - 1)
            }
        }

        /** 下方居中打印 */
        fun <T> showDown(panel: T, painter: GuiPainter)
                where T : IProgressBarPanel, T : IPanelClient {
            drawString(painter, "${panel.value} / ${panel.maxValue}", 0) { width, _ ->
                Point2D((panel.width - width).shr(1), panel.height + 1)
            }
        }

        /**
         * 绘制字符串
         * @param painter 画笔
         * @param text 要绘制的字符串
         * @param pos 获取要打印的字符串的坐标（传入参数：`width, height`）
         */
        fun drawString(painter: GuiPainter, text: String, color: Int, pos: (Int, Int) -> Point2D) {
            val textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text)
            val textHeight = 10
            val location = pos(textWidth, textHeight)
            painter.extraPainter(location.x, location.y, textWidth, textHeight)
                   .drawString(0, 0, text, color)
        }

    }

}