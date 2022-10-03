package top.kmar.mi.api.graphics.utils

import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.toInt
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 进度条样式数据
 * @author EmptyDreams
 */
class ProgressBarData {

    /** 进度条方向 */
    var direction = Direction2DEnum.RIGHT

    /** 进度条样式 */
    var style = ProgressBarStyle.ARROW

    /** 最小高度 */
    var minHeight = 3

    /** 最小宽度 */
    var minWidth = 3

}

enum class ProgressBarStyle {

    ARROW {
        override fun render(graphics: GuiGraphics, style: GraphicsStyle, percent: Float) {
            TODO("Not yet implemented")
        }
    },
    RECT {
        override fun render(graphics: GuiGraphics, style: GraphicsStyle, percent: Float) {
            with(style) {
                if (progress.direction.isVertical())
                    renderHelper(
                        graphics, percent, height(), width(), progress.minWidth,
                        borderRight, progress.direction,
                        backgroundColor.toInt(), color.toInt()
                    )
                else
                    renderHelper(
                        graphics, percent, height(), width(), progress.minHeight,
                        borderBottom, progress.direction,
                        backgroundColor.toInt(), color.toInt()
                    )
            }
        }

        private fun renderHelper(
            graphics: GuiGraphics, percent: Float,
            width: Int, height: Int, minWidth: Int,
            border: BorderStyle, direction: Direction2DEnum,
            background: Int, color: Int
        ) {
            val lightSize = (height * percent).roundToInt() // 填充高度
            val darkSize = height - lightSize   // 空白高度
            val drawWidth = max(width, minWidth) // 宽度
            val offsetX = (width - drawWidth) shr 1 // 居中偏移
            val lightY: Int     // 填充起始坐标
            val darkY: Int      // 空白起始坐标
            if ((direction.isVertical() && direction.isUp()) ||
                    (direction.isHorizontal() && direction.isLeft())) {
                lightY = darkSize
                darkY = 0
            } else {
                lightY = 0
                darkY = lightSize
            }

            if (direction.isVertical()) {
                graphics.fillRect(offsetX, darkY, drawWidth - border.weight, darkSize, background)
                graphics.fillRect(offsetX, lightY, drawWidth - border.weight, lightSize, color)
                graphics.fillRect(
                    offsetX + drawWidth - border.weight, 0,
                    border.weight, height, border.color.toInt()
                )
            } else {
                graphics.fillRect(darkY, offsetX, darkSize, drawWidth - border.weight, background)
                graphics.fillRect(lightY, offsetX, lightSize, drawWidth - border.weight, color)
                graphics.fillRect(
                    0, offsetX + drawWidth - border.weight,
                    height, border.weight, border.color.toInt()
                )
            }
        }

    };

    abstract fun render(graphics: GuiGraphics, style: GraphicsStyle, percent: Float)

}