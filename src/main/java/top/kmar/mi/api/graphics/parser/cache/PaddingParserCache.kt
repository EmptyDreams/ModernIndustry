package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle

/**
 * padding属性的解析器
 *
 * 支持以下格式：
 *
 * + `padding top right bottom left`
 * + `padding topAndBottom leftAndRight`
 * + `padding-top num`
 * + `padding-right num`
 * + `padding-bottom num`
 * + `padding-left num`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class PaddingParserCache(key: String, value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = when (key) {
        "padding" -> {
            val args = value.split(Regex("""\s""")).stream()
                .filter { it.isNotBlank() }
                .mapToInt { it.toInt() }
                .toArray()
            if (args.size == 2) {
                {
                    it.paddingTop = args[0]
                    it.paddingBottom = args[0]
                    it.paddingLeft = args[1]
                    it.paddingRight = args[1]
                }
            } else {
                {
                    it.paddingTop = args[0]
                    it.paddingRight = args[1]
                    it.paddingBottom = args[2]
                    it.paddingLeft = args[3]
                }
            }
        }
        "padding-top" -> {
            val number = value.toInt();
            { it.paddingTop = number }
        }
        "padding-right" -> {
            val number = value.toInt();
            { it.paddingRight = number }
        }
        "padding-bottom" -> {
            val number = value.toInt();
            { it.paddingBottom = number }
        }
        "padding-left" -> {
            val number = value.toInt();
            { it.paddingLeft = number }
        }
        else -> throw IllegalArgumentException("非法的padding表达式：$key: $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}