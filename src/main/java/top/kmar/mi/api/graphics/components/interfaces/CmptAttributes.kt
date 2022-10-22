package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap
import kotlin.reflect.KProperty

/**
 * 控件属性
 * @author EmptyDreams
 */
class CmptAttributes : Iterable<Map.Entry<String, String>> {

    private val map = Object2ObjectRBTreeMap<String, String>()

    var id: String by map

    /** 获取一个值，不存在则返回空的字符串 */
    operator fun get(key: String) = map[key] ?: ""

    /** 获取一个值，不存在则返回默认值 */
    operator fun get(key: String, def: String) = map[key] ?: def

    /** 获取一个值，不存在则调用`defGetter`获取缺省值 */
    fun get(key: String, defGetter: () -> String) = map[key] ?: defGetter()

    operator fun set(key: String, value: String) {
        map[key] = value
    }

    fun copy(id: String = this.id) = CmptAttributes().apply {
        map.putAll(this@CmptAttributes.map)
        this["id"] = id
    }

    override fun iterator() = map.iterator()

    operator fun getValue(obj: Any, property: KProperty<*>) = this[property.name]

    operator fun setValue(obj: Any, property: KProperty<*>, value: String) {
        this[property.name] = value
    }

    /** 获取一个Int类型的委托类 */
    fun toIntDelegate(def: Int = 0) = IntDelegate(def.toString())

    /** 获取一个Boolean类型的委托类 */
    fun toBoolDelegate() = BoolDelegate()

    inner class IntDelegate(private val def: String) {

        operator fun getValue(obj: Any, property: KProperty<*>): Int = get(property.name, def).toInt()

        operator fun setValue(obj: Any, property: KProperty<*>, value: Int) {
            set(property.name, value.toString())
        }

    }

    inner class BoolDelegate {

        operator fun getValue(obj: Any, property: KProperty<*>) = property.name in map

        operator fun setValue(obj: Any, property: KProperty<*>, value: Boolean) {
            if (value) map[property.name] = ""
            else map.remove(property.name)
        }

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