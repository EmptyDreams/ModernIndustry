package top.kmar.mi.api.craft.elements

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraftforge.common.util.INBTSerializable
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.utils.expands.deepClone

/**
 * 合成表输出
 * @author EmptyDreams
 */
class CraftOutput : INBTSerializable<NBTBase> {

    private val attributes = Object2ObjectOpenHashMap<String, Any>()

    /** 主物品列表 */
    @Suppress("UNCHECKED_CAST")
    var stacks: List<ItemStack>
        get() = (attributes["stacks"] as List<ItemStack>).deepClone()
        set(value) {
            attributes["stacks"] = value.deepClone()
        }

    /** 获取主物品列表中的第一个元素 */
    @Suppress("UNCHECKED_CAST")
    val firstStack: ItemStack
        get() = (attributes["stacks"] as List<ItemStack>).first().copy()

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

    fun getInt(key: String) = attributes[key] as Int

    fun getInt(key: String, def: Int) = (attributes[key] as Int?) ?: def

    fun setInt(key: String, value: Int) {
        attributes[key] = value
    }

    fun getString(key: String) = attributes[key] as String

    fun getString(key: String, def: String) = (attributes[key] as String?) ?: def

    fun setString(key: String, value: String) {
        attributes[key] = value
    }

    fun getBoolean(key: String) = attributes[key] as Boolean

    fun getBoolean(key: String, def: Boolean) = (attributes[key] as Boolean?) ?: def

    fun setBoolean(key: String, value: Boolean) {
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

    override fun serializeNBT(): NBTBase {
        val machine = AutoTypeRegister.match(attributes::class)
        return machine.write2Local(attributes, attributes::class)!!
    }

    override fun deserializeNBT(nbt: NBTBase) {
        val machine = AutoTypeRegister.match(attributes::class)
        machine.read2Obj(nbt, attributes::class) { map ->
            @Suppress("UNCHECKED_CAST")
            attributes.putAll(map as Map<String, Any>)
        }
    }

}