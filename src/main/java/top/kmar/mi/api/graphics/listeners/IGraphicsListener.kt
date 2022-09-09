package top.kmar.mi.api.graphics.listeners

/**
 * 事件接口
 * @author EmptyDreams
 */
fun interface IGraphicsListener<T : ListenerData> {

    /** 触发事件 */
    fun active(`data`: T)

    @Suppress("UNCHECKED_CAST")
    fun activeObj(`data`: ListenerData) = active(`data` as T)

}