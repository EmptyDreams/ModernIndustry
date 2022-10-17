package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle

/**
 * margin表达式
 *
 * 支持以下格式：
 *
 * + `margin top right bottom left`
 * + `margin topAndBottom rightAndLeft`
 * + `margin-left left`
 * + `margin-right right`
 * + `margin-bottom bottom`
 * + `margin-left left`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class MarginParserCache(key: String, value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = value.run {
        if (key.length == 6) {  // margin
            val args = value.split(Regex("""\s"""))
                .stream().filter { it.isNotBlank() }.mapToInt { it.toInt() }.toArray()
            when (args.size) {
                4 -> {
                    return@run {
                        it.marginTop = args[0]
                        it.marginRight = args[1]
                        it.marginBottom = args[2]
                        it.marginLeft = args[3]
                    }
                }
                2 -> {
                    return@run {
                        it.marginTop = args[0]
                        it.marginBottom = args[0]
                        it.marginRight = args[1]
                        it.marginLeft = args[1]
                    }
                }
                1 -> {
                    return@run {
                        it.marginTop = args[0]
                        it.marginBottom = args[0]
                        it.marginRight = args[0]
                        it.marginLeft = args[0]
                    }
                }
            }
        } else {    // margin-direction
            val number = value.toInt()
            when (key[7]) {
                't' -> return@run { it.marginTop = number }
                'r' -> return@run { it.marginRight = number }
                'b' -> return@run { it.marginBottom = number }
                'l' -> return@run { it.marginLeft = number }
            }
        }
        throw IllegalArgumentException("不合法的margin表达式：$key = $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}