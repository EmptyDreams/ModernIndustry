package top.kmar.mi.api.graphics.utils

import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.utils.container.PairIntInt
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.floorDiv2
import top.kmar.mi.api.utils.isEven
import top.kmar.mi.api.utils.minusIf
import top.kmar.mi.api.utils.toInt
import java.awt.Color
import kotlin.math.absoluteValue

/**
 * 按钮样式数据
 * @author EmptyDreams
 */
class ButtonStyleData(private val graphicsStyle: GraphicsStyle) {

    /** 按钮风格 */
    var style = ButtonStyleEnum.RECT

    /** 按钮方向 */
    var direction = Direction2DEnum.RIGHT

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
            renderBorder(graphics, style)
        }

        fun renderBorder(graphics: GuiGraphics, style: GraphicsStyle) {
            val top = style.borderTop
            val bottom = style.borderBottom
            val left = style.borderLeft
            val right = style.borderRight
            val direction = style.button.direction
            fun renderHelper(
                startX: Int, startY: Int, xStep: Int, yStep: Int,
                color: Color, realWidth: Int, realHeight: Int
            ): PairIntInt {
                var x = startX
                var y = startY
                var width = xStep.absoluteValue
                var height = yStep.absoluteValue
                val colour = color.toInt()
                while (true) {
                    graphics.fillRect(x, y, width, height, colour)
                    x += xStep
                    y += yStep
                    if (x < 0) {
                        width += x
                        x = 0
                    } else if (x + width > realWidth) {
                        width = realWidth - x
                    }
                    if (y < 0) {
                        height += y
                        y = 0
                    } else  if (y + height > realHeight) {
                        height = realHeight - y
                    }
                    if (width <= 0 || height <= 0) break
                }
                return PairIntInt(x, y)
            }

            when (direction) {
                Direction2DEnum.UP -> {
                    val width = style.width().minusIf(1) { isEven() }
                    val height = style.height()
                    val x = width.floorDiv2()   // 中央坐标
                    renderHelper(
                        x - left.weight, 1, -left.weight, 1,
                        left.color, width, height
                    )
                    val (_, y) = renderHelper(
                        x + 1, 1, right.weight, 1,
                        right.color, width, height
                    )
                    with(graphics) {
                        // 顶点
                        fillRect(x, 0, 1, 1, right.color.toInt())
                        // 底边
                        fillRect(0, height - 1, width, bottom.weight, bottom.color.toInt())
                        // 侧边补充
                        val plusHeight = height - y - 1
                        if (plusHeight <= 0) return@with
                        fillRect(0, y, left.weight, height - y - 1, left.color.toInt())
                        fillRect(width - right.weight, y, right.weight, height - y - 1, right.color.toInt())
                    }
                }
                Direction2DEnum.DOWN -> {
                    val width = style.width().minusIf(1) { isEven() }
                    val height = style.height()
                    val x = width.floorDiv2()   // 中央坐标
                    renderHelper(
                        x - left.weight, height - 2,
                        -left.weight, -1, left.color, width, height
                    )
                    val (_, y) = renderHelper(
                        x + 1, height - 2,
                        right.weight, -1, right.color, width, height
                    )
                    with(graphics) {
                        // 顶点
                        fillRect(x, height - 1, 1, 1, right.color.toInt())
                        // 底边
                        fillRect(0, 0, width, top.weight, top.color.toInt())
                        // 侧边补充
                        if (y < top.weight) return@with
                        val plusHeight = y
                        fillRect(0, top.weight, left.weight, plusHeight, left.color.toInt())
                        fillRect(width - right.weight, top.weight, right.weight, plusHeight, right.color.toInt())
                    }
                }
                Direction2DEnum.LEFT -> {
                    val width = style.width()
                    val height = style.height().minusIf(1) { isEven() }
                    val y = height.floorDiv2()
                    renderHelper(1, y - top.weight, 1, -top.weight, top.color, width, height)
                    val (x, _) = renderHelper(
                        1, y + 1,
                        1, bottom.weight, bottom.color, width, height
                    )
                    with(graphics) {
                        fillRect(0, y, 1, 1, top.color.toInt())
                        fillRect(width - right.weight, 0, right.weight, height, right.color.toInt())
                        val plusWidth = width - x - 1
                        if (plusWidth <= 0) return@with
                        fillRect(x, 0, plusWidth, top.weight, top.color.toInt())
                        fillRect(x, height - bottom.weight, plusWidth, bottom.weight, bottom.color.toInt())
                    }
                }
                Direction2DEnum.RIGHT -> {
                    val width = style.width()
                    val height = style.height().minusIf(1) { isEven() }
                    val y = height.floorDiv2()
                    renderHelper(
                        width - 2, y - top.weight,
                        -1,  -top.weight, top.color, width, height
                    )
                    val (x, _) = renderHelper(
                        width - 2, y + 1,
                        -1, bottom.weight, bottom.color, width, height
                    )
                    with(graphics) {
                        fillRect(width - 1, y, 1, 1, top.color.toInt())
                        fillRect(0, 0, left.weight, height, left.color.toInt())
                        if (x < left.weight) return@with
                        val plusWidth = x
                        fillRect(left.weight, 0, plusWidth, top.weight, top.color.toInt())
                        fillRect(left.weight, height - bottom.weight, plusWidth, bottom.weight, bottom.color.toInt())
                    }
                }
            }
        }
    };

    abstract fun render(graphics: GuiGraphics, style: GraphicsStyle, mouseOn: Boolean)

}