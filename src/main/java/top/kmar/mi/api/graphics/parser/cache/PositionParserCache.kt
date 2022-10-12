package top.kmar.mi.api.graphics.parser.cache

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.PositionEnum

/**
 * 排版方式表达式
 *
 * 支持以下值：`relative`、`absolute`、`fixed`
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class PositionParserCache(value: String) : IParserCache {

    private val task: (GraphicsStyle) -> Unit = run {
        val position = PositionEnum.of(value)
        return@run { it.position = position }
    }

    override fun invoke(style: GraphicsStyle) = task(style)

}