package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle

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
                Pair(array[0].trim(), array[1].trim().lowercase())
            }
            return when (key) {
                "width" -> SizeParserCache(value, false)
                "height" -> SizeParserCache(value, true)
                else -> {
                    when {
                        key.endsWith("Color") -> ColorParserCache(key, value)
                        key.contains("border") -> BorderParserCache(key, value)
                        key.contains("margin") -> MarginParserCache(key, value)
                        else -> throw IllegalArgumentException("未知表达式：$content")
                    }
                }
            }
        }

    }

}