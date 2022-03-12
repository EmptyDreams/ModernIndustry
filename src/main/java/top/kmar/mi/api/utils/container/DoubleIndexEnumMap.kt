package top.kmar.mi.api.utils.container

import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDorSerialize
import top.kmar.mi.api.utils.data.enums.HorizontalDirectionEnum
import top.kmar.mi.api.utils.data.enums.HorizontalDirectionEnum.LEFT

/**
 * 以Enum为key的布尔映射表，该类仅支持对象数量<=16的枚举类
 * @author EmptyDreams
 */
@Suppress("NOTHING_TO_INLINE")
class DoubleIndexEnumMap<T : Enum<*>>(val values: Array<T>): Iterable<DoubleIndexEnumMap<T>.Entry>, IDorSerialize {

    private var data = 0

    /** 获取指定键对应的左值 */
    fun getLeft(key: T): Boolean {
        return ((data ushr getOffsetLeft(key)) and 1) == 1
    }

    /** 获取指定键对应的右值 */
    fun getRight(key: T): Boolean {
        val offset = key.ordinal shl 1
        return ((data ushr getOffsetRight(key)) and 1) == 1
    }

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

    operator fun set(key: T, direction: HorizontalDirectionEnum, value: Boolean) {
        if (direction === LEFT) setLeft(key, value)
        else setRight(key, value)
    }

    operator fun get(key: T, direction: HorizontalDirectionEnum) =
        if (direction === LEFT) getLeft(key) else getRight(key)

    fun isInit() = data == 0

    private inline fun getOffsetLeft(key: T) = (key.ordinal shl 1) + 1

    private inline fun getOffsetRight(key: T) = key.ordinal shl 1

    override fun serializeDor(): IDataReader {
        val operator = ByteDataOperator(5)
        operator.writeVarInt(data)
        return operator
    }

    override fun deserializedDor(reader: IDataReader) {
        data = reader.readVarInt()
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

        operator fun component1() = left

        operator fun component2() = right

        override fun toString() = "left=$left,right=$right"

    }

}