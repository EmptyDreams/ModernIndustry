package top.kmar.mi.api.graphics.utils.exps

import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.utils.expands.compareTo
import top.kmar.mi.api.utils.expands.flip
import java.lang.ref.WeakReference
import java.util.*

/**
 * 复合[Cmpt]匹配表达式
 *
 * 格式：
 *
 *   + `#id` - 通过id匹配
 *   + `.className` - 通过类名匹配
 *   + `tagName` - 通过标签名匹配
 *   + `tagName.className1.className2` - 匹配同时满足相连条件的控件
 *   + `.className1 .className2` - 匹配在`1`控件内的`2`控件
 *
 * 注：如果要传入`list`请手动拷贝列表
 *
 * @author EmptyDreams
 */
class ComplexCmptExp(
    val list: List<SingleCmptExp>
) : Comparable<ComplexCmptExp>, ICmptExp {

    override val size: Int
        get() = list.size

    constructor(exp: String) : this(
        Collections.unmodifiableList(
            LinkedList<SingleCmptExp>().apply {
                exp.split(Regex("""\s""")).stream()
                    .filter { it.isNotBlank() }
                    .map { SingleCmptExp(it) }
                    .forEach { add(it) }
            })
    )

    constructor(first: ICmptExp, second: ICmptExp) : this(merge(first, second))

    /** 存储移除第一个表达式后的结果 */
    private var firstCache: WeakReference<ComplexCmptExp>? = null

    override fun removeFirst(): ComplexCmptExp {
        val cache = firstCache?.get()
        if (cache != null) return cache
        val newList = LinkedList(list)
        newList.removeFirst()
        val result = ComplexCmptExp(Collections.unmodifiableList(newList))
        firstCache = WeakReference(result)
        return result
    }

    override fun matchFirst(cmpt: Cmpt) = list.first().match(cmpt)

    override fun match(cmpt: Cmpt): Boolean {
        var num = 0
        var target = cmpt
        o@ for (exp in list.flip()) {
            if (target === Cmpt.EMPTY_CMPT) break
            if (num == 0) {
                if (exp.match(cmpt)) {
                    num = 1
                    target = cmpt.parent
                } else return false
            } else if (exp.match(target)) {
                target = target.parent
                ++num
            } else {
                do {
                    target = target.parent
                    if (target === Cmpt.EMPTY_CMPT) break@o
                } while (!exp.match(target))
                ++num
            }
            if (num == list.size) return true
        }
        return num == list.size
    }

    override fun compareTo(other: ComplexCmptExp) = list.compareTo(other.list)

    override fun equals(other: Any?): Boolean {
        if (other !is ComplexCmptExp) return false
        return list == other.list
    }

    override fun hashCode() = list.hashCode()

    override fun toString() = list.toString()

    companion object {

        private fun merge(first: ICmptExp, second: ICmptExp): List<SingleCmptExp> {
            val result = LinkedList<SingleCmptExp>()
            when (first) {
                is SingleCmptExp -> result += first
                is ComplexCmptExp -> result.addAll(first.list)
                is EmptyCmptExp -> {}
                else -> throw AssertionError()
            }
            when (second) {
                is SingleCmptExp -> result += second
                is ComplexCmptExp -> result.addAll(second.list)
                is EmptyCmptExp -> {}
                else -> throw AssertionError()
            }
            return result
        }

    }

}