package top.kmar.mi.api.graphics.utils.style

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * 用于管理两个方向的样式数据
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class Direction2StyleManager<T : Any>(
    private val node: StyleNode,
    val name: String
) {

    var x: T
        get() = node.getValue("${name}-x")
        set(value) { node["${name}-x"] = value }

    var y: T
        get() = node.getValue("${name}-y")
        set(value) { node["${name}-y"] = value }

    fun setAll(value: T) {
        x = value
        y = value
    }

}