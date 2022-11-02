package top.kmar.mi.api.utils.container

/**
 * 存储缓存内容
 * @author EmptyDreams
 */
class CacheContainer<T>(private val supplier: () -> T) {

    private var value: T? = null

    fun get() = invoke()

    operator fun invoke(): T =
        value ?: run {
            val tmp = supplier()
            value = tmp
            tmp
        }

    fun set(value: T) {
        this.value = value
    }

    fun clear() {
        value = null
    }

    fun reset() {
        value = supplier()
    }

}