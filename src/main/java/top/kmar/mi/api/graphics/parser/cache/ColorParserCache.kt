package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GraphicsStyle

/**
 * 颜色表达式
 *
 * 支持以下格式
 *
 * + `color = color`
 * + `backgroundColor = color`
 * + `fontColor = color`
 *
 * 其中，颜色表达式支持以下格式：
 *
 * + `#RGB`
 * + `#RGBA`
 * + `#RrGgBb`
 * + `#RrGbBbAa`
 * + `rgb(r,g,b)`
 * + `rgba(a,g,b,a)`
 * + `rgb(r g b)`
 * + `rgba(r g b a)`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class ColorParserCache(key: String, value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = value.run {
        val color = IntColor(this)
        when (key) {
            "color" -> return@run { it.color = color }
            "backgroundColor" -> return@run { it.backgroundColor = color }
            "fontColor" -> return@run { it.fontColor = color }
        }
        throw IllegalArgumentException("不合法的颜色表达式：$this")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}