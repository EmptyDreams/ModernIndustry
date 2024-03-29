package top.kmar.mi.api.utils.container

import net.minecraft.nbt.NBTTagInt
import net.minecraftforge.common.util.INBTSerializable

/**
 * 以Enum为key的布尔映射表，该类仅支持对象数量<=32的枚举类
 * @author EmptyDreams
 */
class IndexEnumMap<T : Enum<*>>(private val keys: Array<T>) :
        Iterable<IndexEnumMap<T>.Entry>, INBTSerializable<NBTTagInt> {

    /** 布尔值  */
    private var value = 0

    operator fun set(key: T, flag: Boolean) {
        value = if (flag) value or (1 shl key.ordinal) else value and (1 shl key.ordinal).inv()
    }

    operator fun get(key: T): Boolean {
        return value shr key.ordinal and 1 == 1
    }

    /** 判断map是否相当于没有存储值  */
    fun isInit(): Boolean {
        return value == 0
    }

    /** 获取内部值  */
    fun getValue(): Int {
        return value
    }

    /** 设置内部值  */
    fun setValue(value: Int) {
        this.value = value
    }

    override fun toString(): String = Integer.toBinaryString(getValue())

    override fun serializeNBT() = NBTTagInt(value)

    override fun deserializeNBT(nbt: NBTTagInt) {
        value = nbt.int
    }

    override fun iterator() = EnumIterator(0)

    inner class EnumIterator(var index: Int) : Iterator<IndexEnumMap<T>.Entry> {

        override fun hasNext() = index != keys.lastIndex

        override fun next() = Entry(keys[index++])

    }

    inner class Entry(val key: T) {

        var value: Boolean
            get() = this@IndexEnumMap[key]
            set(value) = this@IndexEnumMap.set(key, value)

        operator fun component1() = key

        operator fun component2() = value

        override fun toString() = "key=$key,value=$value"
    }

}