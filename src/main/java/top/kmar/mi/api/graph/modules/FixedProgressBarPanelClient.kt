package top.kmar.mi.api.graph.modules

import net.minecraft.client.renderer.texture.ITextureObject
import top.kmar.mi.ModernIndustry.MODID
import top.kmar.mi.api.graph.utils.GuiPainter
import top.kmar.mi.api.graph.utils.json.GuiTextureJsonRegister
import top.kmar.mi.api.utils.bindTexture
import kotlin.math.roundToInt

/**
 * 尺寸固定的进度条
 * @author EmptyDreams
 */
class FixedProgressBarPanelClient(
    x: Int, y: Int, val style: Style
) : MovableProgressBarPanelClient(x, y, style.width, style.height, { panel, painter ->
    style(panel as FixedProgressBarPanelClient, painter)
}) {

    enum class Style(
        modid: String,
        srcKey: String,
        fillKey: String,
        private val painter: (FixedProgressBarPanelClient, GuiPainter) -> Unit
    ) {

        ARROW_RIGHT(
            MODID, "arrowRightSrc", "arrowRightFill",
            FixedProgressBarPanelClient::paintHorizontal
        ),
        ARROW_DOWN(
            MODID, "arrowDownSrc", "arrowDownFill",
            FixedProgressBarPanelClient::paintVertical
        ),
        FIRE(
            MODID, "fireSrc", "fireFill",
            FixedProgressBarPanelClient::paintVerticalOppose
        );

        val width: Int
        val height: Int
        val srcX: Int
        val srcY: Int
        val fillX: Int
        val fillY: Int
        val srcTexture: ITextureObject
        val fillTexture: ITextureObject

        init {
            val (srcTexture, srcRect) = GuiTextureJsonRegister[modid, srcKey]
            val (fillTexture, fillRect) = GuiTextureJsonRegister[modid, fillKey]
            width = srcRect.width
            height = srcRect.height
            srcX = srcRect.x
            srcY = srcRect.y
            fillX = fillRect.x
            fillY = fillRect.y
            this.srcTexture = srcTexture
            this.fillTexture = fillTexture
        }

        operator fun invoke(
            panel: FixedProgressBarPanelClient, painter: GuiPainter
        ) = painter(panel, painter)

    }

    companion object {

        fun paintHorizontal(panel: FixedProgressBarPanelClient, painter: GuiPainter) {
            with (panel) {
                val fillWidth = (percent * width).roundToInt()
                style.srcTexture.bindTexture()
                painter.drawTexture(0, 0, style.srcX, style.srcY, width, height)
                if (fillWidth != 0) {
                    style.fillTexture.bindTexture()
                    painter.drawTexture(0, 0, style.fillX, style.fillY, fillWidth, style.height)
                }
            }
        }

        fun paintVertical(panel: FixedProgressBarPanelClient, painter: GuiPainter) {
            with (panel) {
                val fillHeight = (percent * height).roundToInt()
                style.srcTexture.bindTexture()
                painter.drawTexture(0, 0, style.srcX, style.srcY, width, height)
                if (fillHeight != 0) {
                    style.fillTexture.bindTexture()
                    painter.drawTexture(0, 0, style.fillX, style.fillY, style.width, fillHeight)
                }
            }
        }

        fun paintVerticalOppose(panel: FixedProgressBarPanelClient, painter: GuiPainter) {
            with (panel) {
                val fillHeight = (percent * height).roundToInt()
                style.srcTexture.bindTexture()
                painter.drawTexture(0, 0, style.srcX, style.srcY, width, height)
                if (fillHeight != 0) {
                    val startY = height - fillHeight
                    val fillY = style.fillY + startY
                    style.fillTexture.bindTexture()
                    painter.drawTexture(0, startY, style.fillX, fillY, style.width, fillHeight)
                }
            }
        }

    }

}