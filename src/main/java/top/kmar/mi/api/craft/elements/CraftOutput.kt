package top.kmar.mi.api.craft.elements

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.item.ItemStack

/**
 * 合成表输出
 * @author EmptyDreams
 */
class CraftOutput {

    private val attributes = Object2ObjectOpenHashMap<String, Any>()

    /** 主物品列表 */
    @Suppress("UNCHECKED_CAST")
    var stacks: List<ItemStack>
        get() = (attributes["stacks"] as List<ItemStack>).deepClone()
        set(value) {
            attributes["stacks"] = value.deepClone()
        }

    /** 将指定物品放入主物品列表（会覆盖原有值） */
    fun setStacks(vararg stacks: ItemStack) {
        val list = ArrayList<ItemStack>(stacks.size)
        stacks.forEach { list.add(it.copy()) }
        attributes["stacks"] = list
    }

    /** 获取一个值并强转为物品列表 */
    @Suppress("UNCHECKED_CAST")
    fun getStacks(key: String) = (attributes[key] as List<ItemStack>).deepClone()

    /** 设置一个值 */
    fun setStacks(key: String, value: List<ItemStack>) {
        attributes[key] = value.deepClone()
    }

    /** 获取一个值并强转为整型 */
    fun getInt(key: String) = attributes[key] as Int

    /** 设置一个值 */
    fun setInt(key: String, value: Int) {
        attributes[key] = value
    }

    /** 获取一个值并强转为字符串 */
    fun getString(key: String) = attributes[key] as String

    /** 设置一个值 */
    fun setString(key: String, value: String) {
        attributes[key] = value
    }

    /** 复制当前对象 */
    fun copy(): CraftOutput {
        val result = CraftOutput()
        result.attributes.putAll(attributes)
        val stacks = attributes["stacks"] as List<*>?
        if (stacks != null) result.attributes["stacks"] = ArrayList(stacks)
        return result
    }

    /** 深度复制列表 */
    private fun List<ItemStack>.deepClone(): List<ItemStack> {
        val list = ArrayList<ItemStack>(size)
        forEach { list.add(it.copy()) }
        return list
    }

}