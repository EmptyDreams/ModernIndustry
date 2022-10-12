package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.utils.data.enums.Direction2DEnum

/**
 * 解析缓存
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
interface IParserCache {

    /** 修改样式表 */
    operator fun invoke(style: GraphicsStyle)

    companion object {

        fun build(content: String): IParserCache {
            val (key, value) = content.run {
                val array = split('=')
                Pair(array[0].trim(), array[1].trim())
            }
            return when (key) {
                "width" -> SizeParserCache(value, false)
                "height" -> SizeParserCache(value, true)
                "position" -> PositionParserCache(value)
                "top" -> LocationParserCache(Direction2DEnum.UP, value)
                "right" -> LocationParserCache(Direction2DEnum.RIGHT, value)
                "bottom" -> LocationParserCache(Direction2DEnum.DOWN, value)
                "left" -> LocationParserCache(Direction2DEnum.LEFT, value)
                "display" -> DisplayParserCache(value)
                else -> {
                    @Suppress("SpellCheckingInspection")
                    when {
                        key.endsWith("olor") -> ColorParserCache(key, value)
                        key.startsWith("border") -> BorderParserCache(key, value)
                        key.startsWith("margin") -> MarginParserCache(key, value)
                        key.startsWith("align") -> AlignParserCache(key, value)
                        key.startsWith("progress") -> ProgressParserCache(key, value)
                        key.startsWith("button") -> ProgressParserCache(key, value)
                        else -> throw IllegalArgumentException("未知表达式：$content")
                    }
                }
            }
        }

    }

}