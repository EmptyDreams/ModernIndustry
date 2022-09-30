package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap

/**
 * 控件属性
 * @author EmptyDreams
 */
class CmptAttributes {

    private val map = Object2ObjectRBTreeMap<String, String>()

    val id: String
        get() = this["id"]

    /** 获取一个值，不存在则返回空的字符串 */
    operator fun get(key: String) = map[key] ?: ""

    /** 获取一个值，不存在则返回默认值 */
    operator fun get(key: String, def: String) = map[key] ?: def

    /** 获取一个值，不存在则调用`defGetter`获取缺省值 */
    fun get(key: String, defGetter: () -> String) = map[key] ?: defGetter()

    operator fun set(key: String, value: String) {
        map[key] = value
    }

    companion object {

        @JvmStatic
        fun valueOfID(id: String): CmptAttributes {
            val result = CmptAttributes()
            result["id"] = id
            return result
        }

    }

}