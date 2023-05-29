package top.kmar.mi.api.graphics.utils.style

/**
 * 用于管理四个方向的样式数据
 * @author EmptyDreams
 */
class Direction4StyleManager<T : Any>(
    private val node: StyleNode,
    val name: String
) {

    var top: T
        get() = node.getValue("${name}-top")
        set(value) { node["${name}-top"] = value }

    var bottom: T
        get() = node.getValue("${name}-bottom")
        set(value) { node["${name}-bottom"] = value }

    var left: T
        get() = node.getValue("${name}-left")
        set(value) { node["${name}-left"] = value }

    var right: T
        get() = node.getValue("${name}-right")
        set(value) { node["${name}-right"] = value }

    fun setTopAndBottom(value: T) {
        top = value
        bottom = value
    }

    fun setLeftAndRight(value: T) {
        left = value
        right = value
    }

    fun setAll(value: T) {
        setTopAndBottom(value)
        setLeftAndRight(value)
    }

    /**
     * 设置所有方向的值
     * @param valueList 包含值的列表，顺序为：上、右、下、左，长度必须为 4
     */
    fun setAll(valueList: Iterable<T>) {
        val itor = valueList.iterator()
        top = itor.next()
        right = itor.next()
        bottom = itor.next()
        left = itor.next()
        assert(!itor.hasNext()) { "传入的列表长度不为 4" }
    }

    /** 遍历所有方向的值 */
    inline fun forEachAll(consumer: (T) -> Unit) {
        consumer(top)
        consumer(right)
        consumer(bottom)
        consumer(left)
    }

}