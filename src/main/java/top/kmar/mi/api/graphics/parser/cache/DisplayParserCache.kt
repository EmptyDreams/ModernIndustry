package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.modes.DisplayModeEnum
import top.kmar.mi.api.graphics.utils.GraphicsStyle

/**
 * 显示方式表达式
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class DisplayParserCache(value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = when (value.length) {
        3 -> { { it.display = DisplayModeEnum.DEF } }
        4 -> { { it.display = DisplayModeEnum.NONE } }
        6 -> { { it.display = DisplayModeEnum.INLINE } }
        else -> throw IllegalArgumentException("不合法的显示表达式：display = $value")
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}