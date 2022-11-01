package top.kmar.mi.api.utils.container

/**
 * 基于[Int]的`BitSet`
 *
 * 其中下标与二进制保持一致
 *
 * @author EmptyDreams
 */
class IntBitSet(var value: Int = 0) {

    /** 获取指定下标的值，1 为`true`，2 为`false` */
    operator fun get(index: Int): Boolean {
        assert(index in 0 until 32) { IndexOutOfBoundsException("下标[$index]应该在[0, 32)内") }
        return (value shr index).and(1) == 1
    }

    /** 设置指定下标的位置的值，`true`为 1，`false`为 0 */
    operator fun set(index: Int, value: Boolean) {
        this[index] = if (value) 1 else 0
    }

    /**
     * 将指定位置的值设定为 1 或 0
     * @throws IllegalArgumentException 如果`value`不等于 0 或 1
     */
    operator fun set(index: Int, value: Int) {
        assert(index in 0 until 32) { IndexOutOfBoundsException("下标[$index]应该在[0, 32)内") }
        assert(value == 0 || value == 1) { IllegalArgumentException("value[$value]应当等于 0 或 1") }
        if (value == 1) this.value = this.value or (1 shl index)
        else this.value = this.value and (1 shl index).inv()
    }

}