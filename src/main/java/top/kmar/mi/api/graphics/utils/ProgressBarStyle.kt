package top.kmar.mi.api.graphics.utils

import top.kmar.mi.api.utils.ceilDiv2
import top.kmar.mi.api.utils.container.PairIntInt
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.swapIf
import top.kmar.mi.api.utils.toInt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 进度条样式数据
 * @author EmptyDreams
 */
class ProgressBarData(private val graphicsStyle: GraphicsStyle) {

    /** 进度条方向 */
    var direction = Direction2DEnum.RIGHT

    /** 进度条样式 */
    var style = ProgressBarStyle.ARROW

    /** 最小高度 */
    var minHeight = 3

    /** 最小宽度 */
    var minWidth = 3

    fun render(graphics: GuiGraphics, percent: Float) = style.render(graphics, graphicsStyle, percent)

}

enum class ProgressBarStyle {

    ARROW {
        override fun render(graphics: GuiGraphics, style: GraphicsStyle, percent: Float) {
            with(style) {
                val background = backgroundColor.toInt()
                val color = this.color.toInt()
                if (progress.direction.isVertical())
                    renderHelper(
                        graphics, percent, width(), height(), progress.minWidth,
                        progress.direction, background, color
                    )
                else
                    renderHelper(
                        graphics, percent, width(), height(), progress.minHeight,
                        progress.direction, background, color
                    )
            }
        }

        /** 绘制三角形 */
        private fun renderTriangle(
            graphics: GuiGraphics, startX: Int, startY: Int, startXSize: Int, startYSize: Int,
            xStep: Int, yStep: Int, xSizeStep: Int, ySizeStep: Int,
            cout: Int, color: Int
        ) {
            var x = startX
            var y = startY
            var xSize = startXSize
            var ySize = startYSize
            for (i in 0 until cout) {
                graphics.fillRect(x, y, xSize, ySize, color)
                x += xStep
                y += yStep
                xSize += xSizeStep
                ySize += ySizeStep
            }
        }

        private fun renderHelper(
            graphics: GuiGraphics, percent: Float,
            width: Int, height: Int, minSize: Int,
            direction: Direction2DEnum,
            background: Int, color: Int
        ) {
            val simWidth = if (direction.isVertical()) height else width
            val lightSize = (simWidth * percent).roundToInt()
            val darkSize = simWidth - lightSize
            // 绘制矩形区域
            when (direction) {
                Direction2DEnum.UP -> {
                    val rectHeight = height - width.ceilDiv2()
                    val lightHeight = min(lightSize, rectHeight)
                    val darkHeight = rectHeight - lightHeight
                    val x = (width - minSize).ceilDiv2()
                    graphics.fillRect(x, height - rectHeight, minSize, darkHeight, background)
                    if (lightHeight != 0)
                        graphics.fillRect(x, height - lightHeight, minSize, lightHeight, color)
                }
                Direction2DEnum.DOWN -> {
                    val rectHeight = height - width.ceilDiv2()
                    val lightHeight = min(lightSize, rectHeight)
                    val darkHeight = rectHeight - lightHeight
                    val x = (width - minSize).ceilDiv2()
                    graphics.fillRect(x, lightSize, minSize, darkHeight, background)
                    if (lightHeight != 0)
                        graphics.fillRect(x, 0, minSize, lightHeight, color)
                }
                Direction2DEnum.LEFT -> {
                    val rectWidth = width - height.ceilDiv2()
                    val lightWidth = min(lightSize, rectWidth)
                    val darkWidth = rectWidth - lightWidth
                    val y = (height - minSize).ceilDiv2()
                    graphics.fillRect(width - rectWidth, y, darkWidth, minSize, background)
                    if (lightWidth != 0)
                        graphics.fillRect(width - lightWidth, y, lightWidth, minSize, color)
                }
                Direction2DEnum.RIGHT -> {
                    val rectWidth = width - height.ceilDiv2()
                    val lightWidth = min(lightSize, rectWidth)
                    val darkWidth = rectWidth - lightWidth
                    val y = (height - minSize).ceilDiv2()
                    graphics.fillRect(lightWidth, y, darkWidth, minSize, background)
                    if (lightWidth != 0)
                        graphics.fillRect(0, y, lightWidth, minSize, color)
                }
            }
            /**
             * 获取三角区域亮暗宽度
             * @return PairIntInt first为亮色，second为暗色
             */
            val getTriangleCout = {
                val (coutSize, progressSize) = height.swapIf(width, direction.isVertical())
                val maxCout = coutSize.ceilDiv2()
                val lightCout = min(maxCout, max(0, lightSize - progressSize + maxCout))
                PairIntInt(lightCout, maxCout - lightCout)
            }
            // 绘制三角形区域
            when (direction) {
                Direction2DEnum.UP -> {
                    val (lightCout, darkCout) = getTriangleCout()
                    val xSize = if (width and 1 == 0) 2 else 1
                    val x = width shr 1
                    renderTriangle(
                        graphics,
                        x, 0, xSize, 1,
                        -1, 1, 2, 0,
                        darkCout, background
                    )
                    renderTriangle(
                        graphics,
                        x - darkCout, darkCout,
                        xSize + darkSize.shl(1), 1,
                        -1, 1, 2, 0,
                        lightCout, color
                    )
                }
                Direction2DEnum.DOWN -> {
                    val (lightCout, darkCout) = getTriangleCout()
                    val xSize = if (width and 1 == 0) 2 else 1
                    val x = width shr 1
                    renderTriangle(
                        graphics,
                        x, height - 1, xSize, 1,
                        -1, -1, 2, 0,
                        darkCout, background
                    )
                    renderTriangle(
                        graphics,
                        x - darkCout, height - darkCout - 1,
                        xSize + darkSize.shl(1), 1,
                        -1, -1, 2, 0,
                        lightCout, color
                    )
                }
                Direction2DEnum.LEFT -> {
                    val (lightCout, darkCout) = getTriangleCout()
                    val ySize = if (height and 1 == 0) 2 else 1
                    val y = height shr 1
                    renderTriangle(
                        graphics,
                        0, y, 1, ySize,
                        1, -1, 0, 2,
                        darkCout, background
                    )
                    renderTriangle(
                        graphics,
                        darkCout, y - darkCout, 1, ySize + darkCout.shl(1),
                        1, -1, 0, 2,
                        lightCout, color
                    )
                }
                Direction2DEnum.RIGHT -> {
                    val (lightCout, darkCout) = getTriangleCout()
                    val ySize = if (height and 1 == 0) 2 else 1
                    val y = height shr 1
                    renderTriangle(
                        graphics, width - 1, y, 1, ySize,
                        -1, -1, 0, 2,
                        darkCout, background
                    )
                    renderTriangle(
                        graphics,
                        width - darkCout - 1, y - darkCout,
                        1, ySize + darkCout.shl(1),
                        -1, -1, 0, 2,
                        lightCout, color
                    )
                }
            }
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