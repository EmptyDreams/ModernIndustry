package top.kmar.mi.api.utils.container

import net.minecraft.nbt.NBTTagInt
import net.minecraftforge.common.util.INBTSerializable

/**
 * 以Enum为key的布尔映射表，该类仅支持对象数量<=16的枚举类
 * @author EmptyDreams
 */
@Suppress("NOTHING_TO_INLINE")
class DoubleIndexEnumMap<T : Enum<*>>(val values: Array<T>) :
        Iterable<DoubleIndexEnumMap<T>.Entry>, INBTSerializable<NBTTagInt> {

    private var data = 0

    /** 获取指定键对应的左值 */
    fun getLeft(key: T): Boolean = ((data ushr getOffsetLeft(key)) and 1) == 1

    /** 获取指定键对应的右值 */
    fun getRight(key: T): Boolean = ((data ushr getOffsetRight(key)) and 1) == 1

    /** 设置指定键的左值 */
    fun setLeft(key: T, value: Boolean) {
        data = if (value) data or (1 shl getOffsetLeft(key))
                else data and (1 shl getOffsetLeft(key)).inv()
    }

    /** 设置指定键的右值 */
    fun setRight(key: T, value: Boolean) {
        data = if (value) data or (1 shl getOffsetRight(key))
                else data and (1 shl getOffsetRight(key)).inv()
    }

    operator fun set(key: T, isLeft: Boolean, value: Boolean) {
        if (isLeft) setLeft(key, value)
        else setRight(key, value)
    }

    /** 同时设置指定键的左值和右值 */
    operator fun set(key: T, value: Boolean) {
        setLeft(key, value)
        setRight(key, value)
    }

    operator fun get(key: T, isLeft: Boolean) =
        if (isLeft) getLeft(key) else getRight(key)

    fun isInit() = data == 0

    /** 设置内部值 */
    fun setValue(value: Int) {
        data = value
    }

    /** 获取内部值得整型表示 */
    fun getValue() = data

    private inline fun getOffsetLeft(key: T) = (key.ordinal shl 1) + 1

    private inline fun getOffsetRight(key: T) = key.ordinal shl 1

    override fun toString(): String {
        val sb = StringBuilder()
        for (value in values) {
            sb.append(value.name).append(',').append(getLeft(value)).append('.').append(getRight(value)).append('|')
        }
        return sb.toString()
    }

    override fun serializeNBT() = NBTTagInt(data)

    override fun deserializeNBT(nbt: NBTTagInt) {
        data = nbt.int
    }

    override fun iterator() = DoubleIterator(0)

    inner class DoubleIterator(private var index: Int): Iterator<Entry> {

        override fun hasNext() = index != values.lastIndex

        override fun next() = Entry(values[index++])

    }

    inner class Entry(val key: T) {

        var left: Boolean
            get() = getLeft(key)
            set(value) = setLeft(key, value)

        var right: Boolean
            get() = getRight(key)
            set(value) = setRight(key, value)

        operator fun component1() = key

        operator fun component2() = left

        operator fun component3() = right

        override fun toString() = "left=$left,right=$right"

    }

}