package top.kmar.mi.api.utils.container

import kotlin.reflect.KProperty

/**
 * 存储缓存内容
 * @author EmptyDreams
 */
class CacheContainer<T>(private val supplier: () -> T) {

    private var value: T? = null

    val isInit: Boolean
        get() = value != null

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

    operator fun getValue(obj: Any, property: KProperty<*>): T = invoke()

    operator fun setValue(obj: Any, property: KProperty<*>, value: T?) {
        this.value = value
    }

}