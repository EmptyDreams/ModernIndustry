/** 通用拓展及其它拓展 */
package top.kmar.mi.api.utils.expands

import top.kmar.mi.api.utils.container.PairIntInt

/** 移除所有空格 */
fun String.removeAllSpace(): String {
    return replace(Regex("""\s"""), "")
}

/** 比较两个列表 */
fun <T : Comparable<T>> Collection<T>.compareTo(other: Collection<T>): Int {
    if (size != other.size) return size.compareTo(other.size)
    val itor0 = iterator()
    val itor1 = other.iterator()
    while (itor0.hasNext()) {
        val res = itor0.next().compareTo(itor1.next())
        if (res != 0) return res
    }
    return 0
}

/**
 * 计算一个字符串的开头有多少个空格，一个制表符当作4个空格
 * @return `first` - 第一个非空白符字符的下标，`second` - 空白符长度
 */
fun String.countStartSpace(): PairIntInt {
    var count = 0
    var index = 0
    for (c in this) {
        when (c) {
            ' ' -> ++count
            '\t' -> count += 4
            else -> break
        }
        ++index
    }
    return PairIntInt(index, count)
}

/** 比较字符串是否相等（忽略大小写） */
fun String.equalsIgnoreCase(that: String): Boolean {
    if (length != that.length) return false
    for (i in indices) {
        if (this[i].lowercaseChar() != that[i].lowercaseChar())
            return false
    }
    return true
}

/** 在客户端执行一段代码 */
inline fun <T> T.applyClient(block: T.() -> Unit): T {
    if (isClient()) block()
    return this
}