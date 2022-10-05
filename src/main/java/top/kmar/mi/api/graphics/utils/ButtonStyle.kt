package top.kmar.mi.api.graphics.utils

import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.utils.toInt
import java.awt.Color

/**
 * 按钮样式数据
 * @author EmptyDreams
 */
class ButtonStyleData(private val graphicsStyle: GraphicsStyle) {

    var style = ButtonStyleEnum.RECT

    fun render(graphics: GuiGraphics, mouseOn: Boolean) = style.render(graphics, graphicsStyle, mouseOn)

}

/**
 * 按钮样式
 * @author EmptyDreams
 */
enum class ButtonStyleEnum {

    RECT {
        val textureLib = ResourceLocation("textures/gui/widgets.png")

        override fun render(graphics: GuiGraphics, style: GraphicsStyle, mouseOn: Boolean) {
            val rectSize = 15
            with(graphics) {
                bindTexture(textureLib)
                // 中央区域坐标
                val startX = style.borderTop.weight
                val startY = style.borderLeft.weight
                val endX = graphics.width - style.borderRight.weight
                val endY = graphics.height - style.borderBottom.weight
                // 绘制中央材质
                for (y in startX until endY step rectSize) {
                    for (x in startY until endX step rectSize) {
                        drawTexture(x, y, 2, 68, rectSize, rectSize)
                    }
                }
                // 中央区域尺寸
                val centerWidth = endX - startX
                val centerHeight = endY - startX
                val lightColor = Color(255, 255, 255, 60).toInt()
                val darkColor = Color(0, 0, 0, 60).toInt()
                // 绘制左上角高亮
                fillRect(startX, startY, centerWidth, 1, lightColor)
                fillRect(startX, startY + 1, 1, centerHeight - 1, lightColor)
                // 绘制右下角阴影
                fillRect(startX, endY - 2, centerWidth, 2, darkColor)
                fillRect(endX - 1, startY, 1, centerHeight - 2, darkColor)
                // 如果被鼠标覆盖则绘制覆盖图层
                if (mouseOn) fillRect(startX, startY, centerWidth, centerHeight, style.color.toInt())
            }
        }
    },
    TRIANGLE {
        override fun render(graphics: GuiGraphics, style: GraphicsStyle, mouseOn: Boolean) {
            TODO("Not yet implemented")
        }
    };

    abstract fun render(graphics: GuiGraphics, style: GraphicsStyle, mouseOn: Boolean)

}