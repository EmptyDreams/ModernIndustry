/** 数学运算相关封装 */
@file:Suppress("NOTHING_TO_INLINE")

package top.kmar.mi.api.utils.expands

import top.kmar.mi.api.utils.container.PairIntInt
import java.util.*

/** 独立于世界的随机数生成器 */
val random = Random()

/** 将字符串转换为整型，在遇到第一个非数字的字符（空格除外）时停止 */
fun String.toDecInt(start: Int = 0): Int {
    var symbol = 1
    var result = 0
    var head = start
    while (!this[head].isDigit()) {
        when (this[head++]) {
            '-' -> {
                symbol = -1
                break
            }
            '+' -> break
            ' ' -> continue
            else -> return 0
        }
    }
    for (i in head until length) {
        val item = this[i]
        if (item == ' ') continue
        if (!item.isDigit()) break
        result = (result shl 1) + (result shl 3) + (item.code xor 48)
    }
    return symbol * result
}

/** 判断是否为偶数 */
inline fun Int.isEven() = this and 1 == 0

/** 如果满足指定条件则进行减法，否则返回其本身 */
inline fun Int.minusIf(num: Int, check: Int.() -> Boolean) = minusIf(num, check(this))

/** 如果满足指定条件则进行减法，否则返回其本身 */
inline fun Int.minusIf(num: Int, bool: Boolean) = if (bool) this - num else this

/** 如果满足指定条件则交换两个数值的位置 */
inline fun Int.swapIf(other: Int, bool: Boolean): PairIntInt {
    return if (bool) PairIntInt(other, this) else PairIntInt(this, other)
}

/** 除2，向下取整 */
inline fun Int.floorDiv2(): Int {
    assert(this >= 0)
    return this shr 1
}

/** 向上取整的整除2 */
inline fun Int.ceilDiv2(): Int {
    val result = this shr 1
    return if (this and 1 == 0) result else result + 1
}

/** 向上取整的整除 */
inline infix fun Int.ceilDiv(other: Int): Int {
    val result = this / other
    return if (this % other == 0) result else result + 1
}

/** 四舍五入的除法 */
inline infix fun Int.roundDiv(other: Int): Int {
    val surplus = this % other
    val split = (other + 1).ceilDiv2()
    return (this / other) + if (surplus < split) 0 else 1
}