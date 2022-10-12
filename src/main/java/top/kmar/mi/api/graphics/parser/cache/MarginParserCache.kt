package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle

/**
 * margin表达式
 *
 * 支持以下格式：
 *
 * + `margin = top right bottom left`
 * + `margin = topAndBottom rightAndLeft`
 * + `marginLeft = left`
 * + `marginRight = right`
 * + `marginBottom = bottom`
 * + `marginLeft = left`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class MarginParserCache(key: String, value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = value.run {
        if (this == "margin") {
            val args = value.split(Regex("""\s*"""))
                .stream().mapToInt { it.toInt() }.toArray()
            if (args.size == 4) {
                return@run {
                    it.marginTop = args[0]
                    it.marginRight = args[1]
                    it.marginBottom = args[2]
                    it.marginLeft = args[3]
                }
            } else if (args.size == 2) {
                return@run {
                    it.marginTop = args[0]
                    it.marginBottom = args[0]
                    it.marginRight = args[1]
                    it.marginLeft = args[1]
                }
            }
        } else {
            val number = value.toInt()
            when (key[6]) {
                'T' -> return@run { it.marginTop = number }
                'R' -> return@run { it.marginRight = number }
                'B' -> return@run { it.marginBottom = number }
                'L' -> return@run { it.marginLeft = number }
            }
        }
        throw IllegalArgumentException("不合法的margin表达式：$key = $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}