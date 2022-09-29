package top.kmar.mi.api.graphics.components

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.toInt
import java.awt.Color

/**
 * GUI背景框
 * @author EmptyDreams
 */
@AutoCmpt("background")
class BackgroundCmpt(id: String) : Cmpt(id) {

    override fun initClientObj() = BackgroundGraphicsClient()

    @SideOnly(Side.CLIENT)
    inner class BackgroundGraphicsClient : CmptClient {

        override val service = this@BackgroundCmpt
        override val style = GraphicsStyle(service).apply {
            borderTop.color = Color.WHITE
            borderRight.color = Color(85, 85, 85)
            borderBottom.color = borderRight.color
            borderLeft.color = borderTop.color
            backgroundColor = Color.BLACK
            color = Color(198, 198, 198)
        }

        override fun render(graphics: GuiGraphics) {
            with(style) {
                graphics.fillRect(3, 3, width() - 6, height() - 6, color.toInt())
            }
            renderBorder(graphics)
            renderBackground(graphics)
        }

        override fun renderBackground(graphics: GuiGraphics) {
            val background = style.backgroundColor.toInt()
            val width = style.width()
            val height = style.height()
            with(graphics) {
                // 上边框
                fillRect(3, 0, width - 7, 2, background)
                fillRect(2, 2, 1, 1, background)
                fillRect(width - 4, 2, 1, 1, background)
                fillRect(width - 3, 3, 1, 2, background)
                // 右边框
                fillRect(width - 1, 5, 1, height - 8, background)
                fillRect(width - 2, 3, 1, 2, background)
                fillRect(width - 2, height - 3, 1, 1, background)
                // 下边框
                fillRect(4, height - 2, width - 7, 2, background)
                fillRect(width - 3, height - 3, 1, 1, background)
                fillRect(3, height - 3, 1, 1, background)
                fillRect(2, height - 5, 1, 2, background)
                // 左边框
                fillRect(0, 3, 1, height - 8, background)
                fillRect(1, height - 5, 1, 2, background)
                fillRect(1, 2, 1, 1, background)
            }
        }

        override fun renderBorder(graphics: GuiGraphics) {
            val borderTop = style.borderTop.color.toInt()
            val borderRight = style.borderRight.color.toInt()
            val borderBottom = style.borderBottom.color.toInt()
            val borderLeft = style.borderLeft.color.toInt()
            val width = style.width()
            val height = style.height()
            with(graphics) {
                fillRect(1, 2, width - 5, 3, borderTop)
                fillRect(width - 4, 5, 3, height - 7, borderRight)
                fillRect(4, height - 5, width - 7, 3, borderBottom)
                fillRect(1, 3, 3, height - 8, borderLeft)
                fillRect(width - 6, height - 7, 2, 2, borderBottom)
                fillRect(4, 5, 2, 2, borderTop)
            }
        }

    }

}