package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.ButtonStyleEnum
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.utils.data.enums.Direction2DEnum

/**
 * 按钮表达式
 *
 * 支持以下格式：
 *
 * + `button-style style`
 * + `button-direction direction`
 *
 * `style`支持：`rect`、`triangle`
 * `direction`支持：`top/up`、`right`、`bottom/down`、`left`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class ButtonParserCache(key: String, value: String) : IParserCache {

    @Suppress("DuplicatedCode")
    private val task: (GraphicsStyle) -> Unit = when (key.length) {
        7 + 5 -> {      // style
            val style = when (value.length) {
                4 -> ButtonStyleEnum.RECT
                8 -> ButtonStyleEnum.TRIANGLE
                else -> throw IllegalArgumentException("不合法的按钮风格表达式：$key = $value")
            }
            { it.button.style = style }
        }
        7 + 9 -> {     // direction
            val direction = when (value) {
                "top", "up" -> Direction2DEnum.UP
                "right" -> Direction2DEnum.RIGHT
                "bottom", "down" -> Direction2DEnum.DOWN
                "left" -> Direction2DEnum.LEFT
                else -> throw IllegalArgumentException("不合法的按钮方向表达式：$key = $value")
            }
            { it.button.direction = direction }
        }
        else -> throw IllegalArgumentException("不合法的按钮表达式：$key = $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}