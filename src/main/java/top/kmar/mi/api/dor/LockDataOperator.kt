package top.kmar.mi.api.dor

import it.unimi.dsi.fastutil.bytes.ByteList

/**
 * 带锁的读写器
 *
 * 如果被锁止后仍然尝试写入数据，会抛出[IllegalStateException]异常
 *
 * @author EmptyDreams
 */
class LockDataOperator : ByteDataOperator {

    /** 是否上锁 */
    private var locked = false

    /** 构建一个指定大小的`dor`（可扩容） */
    constructor(size: Int) : super(size)

    /** 通过数组构建一个`dor` */
    constructor(byte: ByteArray) : super(byte)

    /** 通过列表构建一个`dor` */
    constructor(list: ByteList) : super(list)

    /** 将`dor`锁止，锁止后无法取消锁止 */
    fun lock() {
        locked = true
    }

    override fun nextWriteIndex(): Int {
        if (locked) throw IllegalStateException("该读写器已经被锁止")
        return super.nextWriteIndex()
    }

    override fun setWriteIndex(index: Int) {
        if (locked) throw IllegalStateException("该读写器已经被锁止")
        super.setWriteIndex(index)
    }

}