package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.HorizontalAlignModeEnum
import top.kmar.mi.api.graphics.utils.VerticalAlignModeEnum

/**
 * 排版表达式
 *
 * 支持以下格式：
 *
 * + `align = vertical horizontal`
 * + `alignHorizontal = horizontal`
 * + `alignVertical = vertical`
 *
 * 水平对齐方式支持：`left`、`middle`、`right`
 *
 * 垂直对齐方式支持：`top`、`middle`、`bottom`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class AlignParserCache(key: String, value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = when (key) {
        "align" -> {
            val args = value.split(Regex("""\s*""")).filter { it.isNotBlank() }
            if (args.size != 2) throw IllegalArgumentException("不合法的排版表达式：$key = $value")
            val vertical = when (args[0]) {
                "top" -> VerticalAlignModeEnum.TOP
                "middle" -> VerticalAlignModeEnum.MIDDLE
                "bottom" -> VerticalAlignModeEnum.BOTTOM
                else -> throw IllegalArgumentException("不合法的排版表达式：$key = $value")
            }
            val horizontal = when (args[1].length) {
                4 -> HorizontalAlignModeEnum.LEFT
                6 -> HorizontalAlignModeEnum.MIDDLE
                5 -> HorizontalAlignModeEnum.RIGHT
                else -> throw IllegalArgumentException("不合法的排版表达式：$key = $value")
            }
            {
                it.alignVertical = vertical
                it.alignHorizontal = horizontal
            }
        }
        "alignHorizontal" -> {
            val horizontal = when (value.length) {
                4 -> HorizontalAlignModeEnum.LEFT
                6 -> HorizontalAlignModeEnum.MIDDLE
                5 -> HorizontalAlignModeEnum.RIGHT
                else -> throw IllegalArgumentException("不合法的排版表达式：$key = $value")
            }
            { it.alignHorizontal = horizontal }
        }
        "alignVertical" -> {
            val vertical = when (value) {
                "top" -> VerticalAlignModeEnum.TOP
                "middle" -> VerticalAlignModeEnum.MIDDLE
                "bottom" -> VerticalAlignModeEnum.BOTTOM
                else -> throw IllegalArgumentException("不合法的排版表达式：$key = $value")
            }
            { it.alignVertical = vertical }
        }
        else -> throw IllegalArgumentException("不合法的排版表达式：$key = $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}