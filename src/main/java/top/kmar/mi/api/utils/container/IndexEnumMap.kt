package top.kmar.mi.api.utils.container

import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.araw.interfaces.IDorSerialize

/**
 * 以Enum为key的布尔映射表，该类仅支持对象数量<=32的枚举类
 * @author EmptyDreams
 */
class IndexEnumMap<T : Enum<*>>(private val keys: Array<T>) :
        IDorSerialize, Iterable<IndexEnumMap<T>.Entry> {

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

    override fun serializeDor(writer: IDataWriter) {
        writer.writeVarInt(value)
    }

    override fun deserializedDor(reader: IDataReader) {
        value = reader.readVarInt()
    }

    override fun toString(): String = Integer.toBinaryString(getValue())

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