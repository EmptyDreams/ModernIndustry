package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.utils.data.enums.Direction2DEnum

/**
 * 位置表达式
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class LocationParserCache(direction: Direction2DEnum, value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = run {
        val number = value.toInt()
        return@run when (direction) {
            Direction2DEnum.UP -> { { it.top = number } }
            Direction2DEnum.DOWN -> { { it.bottom = number } }
            Direction2DEnum.LEFT -> { { it.left = number } }
            Direction2DEnum.RIGHT -> { { it.right = number } }
        }
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}