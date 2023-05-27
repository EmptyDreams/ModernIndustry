package top.kmar.mi.api.graphics.utils.style

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.graphics.components.interfaces.IntColor

/**
 * GUI 样式节点
 * @author EmptyDreams
 */
class StyleNode {

    private val sheet = Object2ObjectOpenHashMap<String, Any>(64)

    /** 将指定节点合并到当前节点中，若两者中的 key 存在重复，则使用`that`中的值覆盖当前值 */
    fun merge(that: StyleNode) {
        sheet.putAll(that.sheet)
    }

    /** 设置一个数据 */
    @JvmName("putValue")
    operator fun set(key: String, value: Any) {
        sheet[key] = value
    }

    fun getIntValue(key: String): Int = sheet[key]!! as Int

    fun getIntOrElse(key: String, def: Int): Int =
        sheet.getOrDefault(key, def) as Int

    fun getIntOrElse(key: String, supplier: () -> Int): Int =
        sheet.getOrElse(key, supplier) as Int

    fun getStringValue(key: String): String = sheet[key]!! as String

    fun getStringOrElse(key: String, def: String): String =
        sheet.getOrDefault(key, def) as String

    fun getStringOrElse(key: String, supplier: () -> String): String =
        sheet.getOrElse(key, supplier) as String

    fun getColorValue(key: String): IntColor = sheet[key]!! as IntColor

    fun getColorOrElse(key: String, def: IntColor): IntColor =
        sheet.getOrDefault(key, def) as IntColor

    fun getColorOrElse(key: String, supplier: () -> IntColor): IntColor =
        sheet.getOrElse(key, supplier) as IntColor

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getValue(key: String): T = sheet[key]!! as T

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrElse(key: String, def: T): T =
        sheet.getOrDefault(key, def) as T

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrElse(key: String, supplier: () -> T): T =
        sheet.getOrElse(key, supplier) as T

    /**
     * 获取一个任意类型的数据
     * @throws NullPointerException 如果 key 不存在
     */
    fun getAnyValue(key: String): Any = sheet[key]!!

    /** 获取一个任意类型的数据 */
    fun getAnyOrElse(key: String, def: Any): Any = sheet.getOrDefault(key, def)

    fun getAnyOrElse(key: String, supplier: () -> Any): Any = sheet.getOrElse(key, supplier)

    fun copy(): StyleNode {
        val result = StyleNode()
        result.sheet.putAll(sheet)
        return result
    }

}