@file:Suppress("unused")

package top.kmar.mi.api.graphics.utils.modes

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.utils.container.PairIntInt
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.expands.ceilDiv2
import top.kmar.mi.api.utils.expands.swapIf
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 进度条样式数据
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class ProgressBarData(private val node: StyleNode) {

    /** 进度条方向 */
    var direction: Direction2DEnum
        get() = node.progressDirection
        set(value) { node.progressDirection = value }

    /** 进度条样式 */
    var style: ProgressBarStyle
        get() = node.progressVariety
        set(value) { node.progressVariety = value }

    /** 是否显示进度 */
    val showText: Boolean
        get() = text != ProgressBarTextEnum.NONE

    /** 文本位置 */
    var text: ProgressBarTextEnum
        get() = node.progressText
        set(value) { node.progressText = value }

    /** 文本颜色 */
    var color: IntColor
        get() = node.progressTextColor
        set(value) { node.progressTextColor = value }

    /** 最小高度 */
    var minHeight
        get() = node.progressMinHeight
        set(value) { node.progressMinHeight = value }

    /** 最小宽度 */
    var minWidth
        get() = node.progressMinWidth
        set(value) { node.progressMinWidth = value }

    fun render(cmpt: CmptClient, graphics: GuiGraphics, percent: Float) = style.render(graphics, cmpt, percent)

}

/**
 * 进度条文本位置
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
enum class ProgressBarTextEnum {

    /** 当进度条为横向时在进度条上方，否则在左侧 */
    HEAD,
    /** 在进度条中央 */
    MIDDLE,
    /** 当进度条为横向时在进度条下方，否则在右侧 */
    TAIL,
    /** 不显示文字 */
    NONE;

    companion object {

        @JvmStatic
        fun of(name: String) =
            tryOf(name) ?: throw IllegalArgumentException("未知名称：$name")

        @JvmStatic
        internal fun tryOf(name: String): ProgressBarTextEnum? =
            when(name) {
                "head" -> HEAD
                "middle", "center" -> MIDDLE
                "tail" -> TAIL
                "none" -> NONE
                else -> null
            }

    }

}

/**
 * 进度条样式
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
enum class ProgressBarStyle {

    ARROW {
        override fun render(graphics: GuiGraphics, cmpt: CmptClient, percent: Float) {
            with(cmpt.style) {
                val direction = progressDirection
                if (direction.isVertical()) {
                    renderHelper(
                        graphics, percent, cmpt.width, cmpt.height, progressStyle.minWidth,
                        direction, backgroundColor, color
                    )
                } else {
                    renderHelper(
                        graphics, percent, cmpt.width, cmpt.height, progressStyle.minHeight,
                        direction, backgroundColor, color
                    )
                }
            }
        }

        private fun renderHelper(
            graphics: GuiGraphics, percent: Float,
            width: Int, height: Int, minSize: Int,
            direction: Direction2DEnum,
            background: IntColor, color: IntColor
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
                    graphics.fillTrapezoidal(lightCout, 0, xSize, darkCout, Direction2DEnum.UP, background)
                    graphics.fillTrapezoidal(
                        0, darkCout, xSize + darkSize.shl(1),
                        lightCout, Direction2DEnum.UP, color
                    )
                }
                Direction2DEnum.DOWN -> {
                    val (lightCout, darkCout) = getTriangleCout()
                    val xSize = if (width and 1 == 0) 2 else 1
                    val rectHeight = height - width.ceilDiv2()
                    graphics.fillTrapezoidal(
                        lightCout, rectHeight + lightCout, xSize,
                        darkCout, Direction2DEnum.DOWN, background
                    )
                    graphics.fillTrapezoidal(
                        0, rectHeight, xSize + darkSize.shl(1),
                        lightCout, Direction2DEnum.DOWN, color
                    )
                }
                Direction2DEnum.LEFT -> {
                    val (lightCout, darkCout) = getTriangleCout()
                    val ySize = if (height and 1 == 0) 2 else 1
                    graphics.fillTrapezoidal(0, lightCout, ySize, darkCout, Direction2DEnum.LEFT, background)
                    graphics.fillTrapezoidal(
                        darkCout, 0, ySize + darkCout.shl(1),
                        lightCout, Direction2DEnum.LEFT, color
                    )
                }
                Direction2DEnum.RIGHT -> {
                    val (lightCout, darkCout) = getTriangleCout()
                    val ySize = if (height and 1 == 0) 2 else 1
                    val rectWidth = width - height.ceilDiv2()
                    graphics.fillTrapezoidal(
                        rectWidth + lightCout, lightCout, ySize,
                        darkCout, Direction2DEnum.RIGHT, background
                    )
                    graphics.fillTrapezoidal(
                        rectWidth, 0, ySize + darkCout.shl(1),
                        lightCout, Direction2DEnum.RIGHT, color
                    )
                }
            }
        }
    },
    RECT {
        override fun render(graphics: GuiGraphics, cmpt: CmptClient, percent: Float) {
            with(cmpt.style) {
                val direction = progressDirection
                if (direction.isVertical()) {
                    renderHelper(
                        graphics, percent, cmpt.height, cmpt.width, progressStyle.minWidth,
                        borderRight, direction,
                        backgroundColor, color
                    )
                } else {
                    renderHelper(
                        graphics, percent, cmpt.height, cmpt.width, progressStyle.minHeight,
                        borderBottom, direction,
                        backgroundColor, color
                    )
                }
            }
        }

        private fun renderHelper(
            graphics: GuiGraphics, percent: Float,
            width: Int, height: Int, minWidth: Int,
            border: BorderStyle, direction: Direction2DEnum,
            background: IntColor, color: IntColor
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
                    border.weight, height, border.color
                )
            } else {
                graphics.fillRect(darkY, offsetX, darkSize, drawWidth - border.weight, background)
                graphics.fillRect(lightY, offsetX, lightSize, drawWidth - border.weight, color)
                graphics.fillRect(
                    0, offsetX + drawWidth - border.weight,
                    height, border.weight, border.color
                )
            }
        }

    };

    abstract fun render(graphics: GuiGraphics, cmpt: CmptClient, percent: Float)

    companion object {

        @JvmStatic
        fun of(name: String): ProgressBarStyle =
            when (name) {
                "rect" -> RECT
                "arrow" -> ARROW
                else -> throw IllegalArgumentException("未知名称：$name")
            }

    }

}