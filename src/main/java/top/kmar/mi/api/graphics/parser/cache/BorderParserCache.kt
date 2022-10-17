package top.kmar.mi.api.graphics.parser.cache

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.utils.container.PairIntObj
import java.util.*

/**
 * 边框
 *
 * 支持以下格式：
 *
 * + `border topAndBottomColor leftAndRightColor`
 * + `border topColor rightColor bottomColor leftColor`
 * + `border-weight topAndBottomWeight leftAndRightWeight`
 * + `border-weight topWeight rightWeight bottomWeight leftWeight`
 * + `border-[ direction ] color weight`
 * + `border-[ direction ] color`
 * + `border-[ direction ]-eight weight`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class BorderParserCache(key: String, value: String) : IParserCache {

    private fun splitColor(value: String): PairIntObj<IntList> {
        val result = IntArrayList(4)
        var left = -1
        var index = 0
        for ((i, it) in value.withIndex()) {
            index = i
            when (it) {
                'r' -> {
                    if (left == -1) left = index
                }
                ')' -> result.add(IntColor(value.substring(left .. index)).value)
                ' ' -> {}
                else -> {
                    if (left == -1) break
                }
            }
        }
        return PairIntObj(index, result)
    }

    private val task: (GraphicsStyle) -> Unit = value.run {
        when (key) {
            "border" -> {
                val (_, colors) = splitColor(value)
                if (colors.size == 4) {
                    return@run {
                        it.borderTop.color = IntColor(colors[0])
                        it.borderRight.color = IntColor(colors[1])
                        it.borderBottom.color = IntColor(colors[2])
                        it.borderLeft.color = IntColor(colors[3])
                    }
                } else if (colors.size == 2) {
                    return@run {
                        it.borderTop.color = IntColor(colors[0])
                        it.borderBottom.color = IntColor(colors[0])
                        it.borderLeft.color = IntColor(colors[1])
                        it.borderRight.color = IntColor(colors[1])
                    }
                }
            }
            "border-weight" -> {
                val weights = value.split(Regex("""\s""")).stream().mapToInt { it.toInt() }.toArray()
                if (weights.size == 4) {
                    return@run {
                        it.borderTop.weight = weights[0]
                        it.borderRight.weight = weights[1]
                        it.borderBottom.weight = weights[2]
                        it.borderLeft.weight = weights[3]
                    }
                } else if (weights.size == 2) {
                    return@run {
                        it.borderTop.weight = weights[0]
                        it.borderBottom.weight = weights[0]
                        it.borderLeft.weight = weights[1]
                        it.borderRight.weight = weights[1]
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
                if (key.endsWith("weight")) {
                    val num = toInt()
                    return@run { getBorder(it).weight = num }
                } else {
                    val (index, colors) = splitColor(value)
                    val color = IntColor(colors[0])
                    val splitIndex = value.lastIndexOfAny(charArrayOf(' ', '\t')) + 1
                    if (splitIndex < index) return@run { getBorder(it).color = color }
                    else {
                        val weight = substring(splitIndex).toInt()
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