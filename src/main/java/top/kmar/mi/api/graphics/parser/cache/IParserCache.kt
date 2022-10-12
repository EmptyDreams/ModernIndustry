package top.kmar.mi.api.graphics.parser.cache

import top.kmar.mi.api.graphics.utils.GraphicsStyle

/**
 * 解析缓存
 * @author EmptyDreams
 */
interface IParserCache {

    /** 修改样式表 */
    operator fun invoke(style: GraphicsStyle)

    companion object {

        fun build(content: String): IParserCache {
            val (key, value) = content.run {
                val array = split('=')
                Pair(array[0], array[1].lowercase())
            }
            return when (key) {
                "width" -> SizeParserCache(value, false)
                "height" -> SizeParserCache(value, true)
                else -> throw IllegalArgumentException("未知表达式：$content")
            }
        }

    }

}