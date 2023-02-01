package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.modes.ProgressBarStyle
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.data.enums.VerticalDirectionEnum

/**
 * 进度条表达式
 *
 * 支持以下格式：
 *
 * + `progress-direction direction`
 * + `progress-style style`
 * + `progress-text bool`
 * + `progress-text-location vertical`
 * + `progress-min-height num`
 * + `progress-min-width num`
 * + `progress-min width height`
 *
 * 1. `direction`支持：`top/up`、`right`、`bottom/down`、`left`
 * 2. `style`支持：`arrow`、`rect`
 * 3. `vertical`支持：`top`、`middle`、`bottom`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class ProgressParserCache(key: String, value: String) : IParserCache {

    @Suppress("DuplicatedCode")
    private val task: (GraphicsStyle) -> Unit = when (key.length) {
        9 + 9 -> {  // direction or min-width
            if (key.endsWith('n')) {
                val direction = when (value) {
                    "top", "up" -> Direction2DEnum.UP
                    "right" -> Direction2DEnum.RIGHT
                    "bottom", "down" -> Direction2DEnum.DOWN
                    "left" -> Direction2DEnum.LEFT
                    else -> throw IllegalArgumentException("不合法的进度条方向表达式：$key = $value")
                }
                { it.progress.direction = direction }
            } else {
                val number = value.toInt();
                { it.progress.minWidth = number }
            }
        }
        9 + 5 -> {     // style
            val style =  when (value.length) {
                5 -> ProgressBarStyle.ARROW
                4 -> ProgressBarStyle.RECT
                else -> throw IllegalArgumentException("不合法的进度条方向表达式：$key = $value")
            }
            { it.progress.style = style }
        }
        9 + 4 -> {      // text
            val bool = value.toBoolean();
            { it.progress.showText = bool }
        }
        9 + 13 -> {     // text-location
            val direction = when (value) {
                "top" -> VerticalDirectionEnum.UP
                "middle" -> VerticalDirectionEnum.CENTER
                "bottom" -> VerticalDirectionEnum.DOWN
                else -> throw IllegalArgumentException("不合法的进度条方向表达式：$key = $value")
            }
            { it.progress.textLocation = direction }
        }
        9 + 10 -> {     // min-height
            val number = value.toInt();
            { it.progress.minHeight = number }
        }
        9 + 3 -> {      // min
            val args = value.split(Regex("""\s""")).stream().mapToInt { it.toInt() }.toArray()
            if (args.size != 2) throw IllegalArgumentException("不合法的进度条尺寸表达式：$key = $value");
            {
                it.progress.minWidth = args[0]
                it.progress.minHeight = args[1]
            }
        }
        else -> throw IllegalArgumentException("不合法的进度条尺寸表达式：$key = $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}