package top.kmar.mi.api.graphics.utils.style

/**
 * 用于管理两个方向的样式数据
 * @author EmptyDreams
 */
class Direction2StyleManager<T : Any>(
    private val node: StyleNode,
    val name: String
) {

    var horizontal: T
        get() = node.getValue("${name}-horizontal")
        set(value) { node["${name}-horizontal"] = value }

    var vertical: T
        get() = node.getValue("${name}-vertical")
        set(value) { node["${name}-vertical"] = value }

    fun setAll(value: T) {
        horizontal = value
        vertical = value
    }

}