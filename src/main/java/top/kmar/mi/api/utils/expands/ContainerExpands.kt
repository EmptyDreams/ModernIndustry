/** 与容器相关的操作的封装 */
@file:Suppress("NOTHING_TO_INLINE")

package top.kmar.mi.api.utils.expands

import top.kmar.mi.api.utils.iterators.ArrayFlipIterator
import java.util.*
import java.util.function.Supplier
import java.util.stream.Stream
import kotlin.collections.ArrayDeque

/**
 * 将队列从指定位置分为两部分，一部分在原有基础上修改，另一部分存储到传入的队列中
 *
 * @param receiver 接受被划分出来的元素的队列
 * @param index 切分位置，切分位置的元素将被划分到右半部分或被丢弃
 * @param retain 是否保留 [index]指向的元素，为 `false` 时会将其移除
 * @param def 分隔点左侧（不包括分割点）的元素是否保留在当前对象中
 */
fun <T> ArrayDeque<T>.clipAt(receiver: MutableList<T>, index: Int, retain: Boolean, def: Boolean) {
    if (def) {
        // 将左半部分保留在当前队列中
        val start = if (retain) index else index + 1
        for (i in start until size)
            receiver.add(this[i])
        for (i in index until size)
            removeLast()
    } else {
        // 将右半部分保留在当前队列中
        for (i in 0 until index) {
            receiver.add(first())
            removeFirst()
        }
        if (!retain) removeFirst()
    }
}

/** 获取数组中指定位置的值，值不存在时调用`supplier`进行获取并修改数组 */
inline fun <T> Array<T>.computeIfAbsent(index: Int, supplier: Supplier<T>): T {
    if (this[index] == null) this[index] = supplier.get()
    return this[index]
}

inline fun <T> Array<T>.stream(): Stream<T> = Arrays.stream(this)

/** 如果表达式为真则倒序遍历，否则正序遍历 */
infix fun <T> Array<T>.flipIf(isFlip: Boolean) =
    if (isFlip) flip() else this.asIterable()

/** 指定起始遍历位置的倒序迭代器 */
infix fun <T> Array<T>.flip(startIndex: Int) =
    Iterable { ArrayFlipIterator(this, startIndex) }

/** 获取倒序遍历的迭代器 */
fun <T> Array<T>.flip() =
    Iterable { ArrayFlipIterator(this, this.size - 1) }