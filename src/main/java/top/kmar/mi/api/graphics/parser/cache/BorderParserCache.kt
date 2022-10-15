package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import kotlin.streams.toList

/**
 * 边框
 *
 * 支持以下格式：
 *
 * + `border = topAndLeftColor rightAndBottomColor`
 * + `border = topColor rightColor bottomColor leftColor`
 * + `border-weight = topAndLeftWeight rightAndBottomWeight`
 * + `border-weight = topWeight rightWeight bottomWeight leftWeight`
 * + `border-[ direction ] = color weight`
 * + `border-[ direction ] = color`
 * + `border-[ direction ]-eight = weight`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class BorderParserCache(key: String, value: String) : IParserCache {

    val task: (GraphicsStyle) -> Unit = value.run {
        val args = split(Regex("""\s""")).stream().filter { it.isNotBlank() }.toList()
        when (key) {
            "border" -> {
                val colors = args.map { IntColor(it) }
                if (args.size == 4) {
                    return@run {
                        it.borderTop.color = colors[0]
                        it.borderRight.color = colors[1]
                        it.borderBottom.color = colors[2]
                        it.borderLeft.color = colors[3]
                    }
                } else if (args.size == 2) {
                    return@run {
                        it.borderTop.color = colors[0]
                        it.borderLeft.color = colors[0]
                        it.borderRight.color = colors[1]
                        it.borderBottom.color = colors[1]
                    }
                }
            }
            "border-weight" -> {
                val weights = args.map { it.toInt() }
                if (args.size == 4) {
                    return@run {
                        it.borderTop.weight = weights[0]
                        it.borderRight.weight = weights[1]
                        it.borderBottom.weight = weights[2]
                        it.borderLeft.weight = weights[3]
                    }
                } else if (args.size == 2) {
                    return@run {
                        it.borderTop.weight = weights[0]
                        it.borderLeft.weight = weights[1]
                        it.borderRight.weight = weights[2]
                        it.borderBottom.weight = weights[3]
                    }
                }
            }
            else -> {
                val getBorder = when (key[7]) {
                    't' -> { style: GraphicsStyle -> style.borderTop }
                    'r' -> { style: GraphicsStyle -> style.borderRight }
                    'b' -> { style: GraphicsStyle -> style.borderBottom }
                    'l' -> { style: GraphicsStyle -> style.borderLeft }
                    else -> throw IllegalArgumentException("不合法的边框表达式：$key = $value")
                }
                if (key.endsWith("weight")) return@run { getBorder(it).weight = toInt() }
                else {
                    val color = IntColor(args[0])
                    if (args.size == 1) return@run { getBorder(it).color = color }
                    else if (args.size == 2) {
                        val weight = args[1].toInt()
                        return@run {
                            val border = getBorder(it)
                            border.color = color
                            border.weight = weight
                        }
                    }
                }
            }
        }
        throw IllegalArgumentException("不合法的边框表达式：$key = $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}