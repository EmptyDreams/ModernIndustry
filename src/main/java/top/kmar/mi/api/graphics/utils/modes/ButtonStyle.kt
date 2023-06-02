package top.kmar.mi.api.graphics.utils.modes

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.components.interfaces.IntColor.Companion.shadow
import top.kmar.mi.api.graphics.components.interfaces.IntColor.Companion.white
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.utils.container.PairIntInt
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.expands.floorDiv2
import top.kmar.mi.api.utils.expands.isEven
import top.kmar.mi.api.utils.expands.minusIf
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * 按钮样式数据
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class ButtonStyleData(private val node: StyleNode) {

    var style: ButtonStyleEnum
        get() = node.buttonVariety
        set(value) { node.buttonVariety = value }

    var direction: Direction2DEnum
        get() = node.buttonDirection
        set(value) { node.buttonDirection = value }

    fun render(cmpt: CmptClient, graphics: GuiGraphics, mouseOn: Boolean) = style.render(graphics, cmpt, mouseOn)

}

/**
 * 按钮样式
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
enum class ButtonStyleEnum {

    RECT {
        val textureLib = ResourceLocation("textures/gui/widgets.png")

        override fun render(graphics: GuiGraphics, cmpt: CmptClient, mouseOn: Boolean) {
            val rectSize = 15
            val style = cmpt.style
            with(graphics) {
                //if (!cmpt.overflowHidden)
                scissor()
                // 中央区域坐标
                val startX = style.borderTop.weight
                val startY = style.borderLeft.weight
                val endX = cmpt.width - style.borderRight.weight
                val endY = cmpt.height - style.borderBottom.weight
                bindTexture(textureLib)
                // 绘制中央材质
                for (y in startX until endY step rectSize) {
                    for (x in startY until endX step rectSize) {
                        drawTexture256(x, y, 2, 68, rectSize, rectSize)
                    }
                }
                // 中央区域尺寸
                val centerWidth = endX - startX
                val centerHeight = endY - startX
                val lightColor = IntColor(255, 255, 255, 60)
                val darkColor = IntColor(0, 0, 0, 60)
                // 绘制左上角高亮
                fillRect(startX, startY, centerWidth, 1, lightColor)
                fillRect(startX, startY + 1, 1, centerHeight - 1, lightColor)
                // 绘制右下角阴影
                fillRect(startX, endY - 2, centerWidth, 2, darkColor)
                fillRect(endX - 1, startY, 1, centerHeight - 2, darkColor)
                // 如果被鼠标覆盖则绘制覆盖图层
                if (mouseOn) fillRect(startX, startY, centerWidth, centerHeight, hoverColor)
                //if (!cmpt.overflowHidden)
                graphics.unscissor()
            }
        }
    },
    TRIANGLE {
        override fun render(graphics: GuiGraphics, cmpt: CmptClient, mouseOn: Boolean) {
            val style = cmpt.style
            val top = style.borderTop
            val bottom = style.borderBottom
            val left = style.borderLeft
            val right = style.borderRight
            val background = style.backgroundColor
            val color = style.color

            when (style.buttonDirection) {
                Direction2DEnum.UP -> {
                    val width = cmpt.width.minusIf(1) { isEven() }
                    val height = cmpt.height
                    val x = width.floorDiv2()   // 中央坐标
                    renderHelper(
                        graphics,
                        x - left.weight, 1, -left.weight, 1,
                        left.color, width, height
                    )
                    val (_, y) = renderHelper(
                        graphics,
                        x + 1, 1, right.weight, 1,
                        right.color, width, height
                    )
                    with(graphics) {
                        // 顶点
                        fillRect(x, 0, 1, 1, right.color)
                        // 底边
                        fillRect(0, height - 1, width, bottom.weight, bottom.color)
                        // 侧边补充
                        val plusHeight = height - y - bottom.weight
                        if (plusHeight <= 0) return@with
                        fillRect(0, y, left.weight, height - y - 1, left.color)
                        fillRect(width - right.weight, y, right.weight, height - y - 1, right.color)
                    }
                    with(graphics) {
                        val plusHeight = height - y - bottom.weight
                        // 填充背景
                        fillTriangle(left.weight + 1, 1, y - 2, Direction2DEnum.UP, background)
                        fillRect(
                            left.weight, y - 1,
                            width - left.weight - right.weight, plusHeight + 1,
                            background
                        )
                        // 阴影
                        val shadowWidth = ((width - left.weight - right.weight) * 0.9).toInt()
                        fillRect(
                            width - shadowWidth - 1, height - bottom.weight - 1,
                            shadowWidth, 1,
                            shadow
                        )
                        fillRect(width - right.weight - 1, y, 1, plusHeight - 1, shadow)
                        // 鼠标覆盖
                        if (mouseOn) {
                            fillTriangle(left.weight + 1, 1, y - 2, Direction2DEnum.UP, hoverColor)
                            fillRect(
                                left.weight, y - 1,
                                width - left.weight - right.weight, plusHeight + 1,
                                hoverColor
                            )
                        }
                        // 高亮
                        for (i in 0 until y - 2) {
                            fillRect(x - i, 1 + i, 1, 1, white)
                        }
                        for (i in 1 until (y * 0.3).roundToInt()) {
                            fillRect(x + i, 1 + i, 1, 1, white)
                        }
                    }
                }
                Direction2DEnum.DOWN -> {
                    val width = cmpt.width.minusIf(1) { isEven() }
                    val height = cmpt.height
                    val x = width.floorDiv2()   // 中央坐标
                    renderHelper(
                        graphics,
                        x - left.weight, height - 2,
                        -left.weight, -1, left.color, width, height
                    )
                    val (_, y) = renderHelper(
                        graphics,
                        x + 1, height - 2,
                        right.weight, -1, right.color, width, height
                    )
                    with(graphics) {
                        // 顶点
                        fillRect(x, height - 1, 1, 1, right.color)
                        // 底边
                        fillRect(0, 0, width, top.weight, top.color)
                        // 侧边补充
                        if (y < top.weight) return@with
                        val plusHeight = y
                        fillRect(0, top.weight, left.weight, plusHeight, left.color)
                        fillRect(width - right.weight, top.weight, right.weight, plusHeight, right.color)
                    }
                    with(graphics) {
                        // 填充背景
                        fillTriangle(
                            left.weight + 1, y + top.weight + 1,
                            height - y - 2 - top.weight,
                            Direction2DEnum.DOWN, background
                        )
                        fillRect(
                            left.weight, top.weight,
                            width - left.weight - right.weight, y + 1,
                            background
                        )
                        // 阴影
                        for (i in 0 until height - y - 3) {
                            fillRect(x + i, height - 2 - i, 1, 1, shadow)
                        }
                        if (y >= top.weight)
                            fillRect(width - right.weight - 1, top.weight + 1, 1, y, shadow)
                        if (mouseOn) {
                            fillTriangle(
                                left.weight + 1, y + top.weight + 1,
                                height - y - 2 - top.weight,
                                Direction2DEnum.DOWN, hoverColor
                            )
                            fillRect(
                                left.weight, top.weight,
                                width - left.weight - right.weight, y + 1,
                                hoverColor
                            )
                        }
                        // 高亮
                        fillRect(
                            left.weight, top.weight,
                            ((width - left.weight - right.weight) * 0.8).toInt(), 1,
                            white
                        )
                        if (y > top.weight) fillRect(left.weight, top.weight, 1, y, white)
                    }
                }
                Direction2DEnum.LEFT -> {
                    val width = cmpt.width
                    val height = cmpt.height.minusIf(1) { isEven() }
                    val y = height.floorDiv2()
                    renderHelper(
                        graphics,
                        1, y - top.weight,
                        1, -top.weight, top.color, width, height
                    )
                    val (x, _) = renderHelper(
                        graphics,
                        1, y + 1,
                        1, bottom.weight, bottom.color, width, height
                    )
                    with(graphics) {
                        fillRect(0, y, 1, 1, top.color)
                        fillRect(width - right.weight, 0, right.weight, height, right.color)
                        val plusWidth = width - x - 1
                        if (plusWidth <= 0) return@with
                        fillRect(x, 0, plusWidth, top.weight, top.color)
                        fillRect(x, height - bottom.weight, plusWidth, bottom.weight, bottom.color)
                    }
                    with(graphics) {
                        // 填充背景
                        val plusWidth = width - x - 1
                        fillTriangle(1, top.weight + 1, x - 2, Direction2DEnum.LEFT, background)
                        fillRect(
                            x - 1, top.weight,
                            plusWidth + 1, height - top.weight - bottom.weight,
                            background
                        )
                        // 阴影
                        for (i in 0 until  width - plusWidth - 3) {
                            fillRect(i + 2, y + i + 1, 1, 1, shadow)
                        }
                        fillRect(x, height - bottom.weight - 1, plusWidth, 1, shadow)
                        if (mouseOn) {
                            fillTriangle(1, top.weight + 1, x - 2, Direction2DEnum.LEFT, hoverColor)
                            fillRect(
                                x - 1, top.weight,
                                plusWidth + 1, height - top.weight - bottom.weight,
                                hoverColor
                            )
                        }
                        // 高亮
                        fillRect(x, top.weight, plusWidth, 1, white)
                        for (i in 3 until width - plusWidth) {
                            fillRect(i - 1, y - i + 2, 1, 1, white)
                        }
                    }
                }
                Direction2DEnum.RIGHT -> {
                    val width = cmpt.width
                    val height = cmpt.height.minusIf(1) { isEven() }
                    val y = height.floorDiv2()
                    renderHelper(
                        graphics,
                        width - 2, y - top.weight,
                        -1,  -top.weight, top.color, width, height
                    )
                    val (x, _) = renderHelper(
                        graphics,
                        width - 2, y + 1,
                        -1, bottom.weight, bottom.color, width, height
                    )
                    with(graphics) {
                        fillRect(width - 1, y, 1, 1, top.color)
                        fillRect(0, 0, left.weight, height, left.color)
                        if (x < left.weight) return@with
                        val plusWidth = x
                        fillRect(left.weight, 0, plusWidth, top.weight, top.color)
                        fillRect(
                            left.weight, height - bottom.weight,
                            plusWidth, bottom.weight,
                            bottom.color
                        )
                    }
                    with(graphics) {
                        // 填充背景
                        fillTriangle(
                            x + left.weight + 1, top.weight + 1,
                            width - x - 3,
                            Direction2DEnum.RIGHT, background
                        )
                        fillRect(
                            left.weight, top.weight,
                            x + 1, height - top.weight - bottom.weight,
                            background
                        )
                        // 阴影
                        for (i in 0 until width - x - 2) {
                            fillRect(width - i - 2, y + i, 1, 1, shadow)
                        }
                        if (x >= left.weight)
                            fillRect(left.weight + 1, height - bottom.weight - 1, x - 1, 1, shadow)
                        if (mouseOn) {
                            fillTriangle(
                                x + left.weight + 1, top.weight + 1,
                                width - x - 3,
                                Direction2DEnum.RIGHT, hoverColor
                            )
                            fillRect(
                                left.weight, top.weight,
                                x + 1, height - top.weight - bottom.weight,
                                hoverColor
                            )
                        }
                        // 高亮
                        fillRect(left.weight, top.weight, 1, height.floorDiv2(), white)
                        if (x >= left.weight) fillRect(left.weight + 1, top.weight, x, 1, white)
                        for (i in 4 until width - x) {
                            fillRect(x + i - 2, top.weight - 3 + i, 1, 1, white)
                        }
                    }
                }
            }
        }

        fun renderHelper(
            graphics: GuiGraphics,
            startX: Int, startY: Int, xStep: Int, yStep: Int,
            color: IntColor, realWidth: Int, realHeight: Int
        ): PairIntInt {
            var x = startX
            var y = startY
            var width = xStep.absoluteValue
            var height = yStep.absoluteValue
            while (true) {
                graphics.fillRect(x, y, width, height, color)
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
    };

    abstract fun render(graphics: GuiGraphics, cmpt: CmptClient, mouseOn: Boolean)

    companion object {

        @JvmStatic
        private val hoverColor = IntColor(0, 127, 255, 75)

        @JvmStatic
        fun from(name: String): ButtonStyleEnum =
            when (name) {
                "rect" -> RECT
                "triangle" -> TRIANGLE
                else -> throw IllegalArgumentException("未知名称：$name")
            }

    }

}