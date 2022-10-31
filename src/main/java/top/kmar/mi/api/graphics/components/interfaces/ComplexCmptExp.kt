package top.kmar.mi.api.graphics.components.interfaces

import top.kmar.mi.api.utils.expands.compareTo
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
class ComplexCmptExp constructor(val list: List<SingleCmptExp>) : Comparable<ComplexCmptExp> {

    constructor(exp: String) : this(
        Collections.unmodifiableList(
            LinkedList<SingleCmptExp>().apply {
                exp.split(Regex("""\s""")).stream()
                    .filter { it.isNotBlank() }
                    .map { SingleCmptExp(it) }
                    .forEach { add(it) }
            })
    )

    /** 存储移除第一个表达式后的结果 */
    private var firstCache: WeakReference<ComplexCmptExp>? = null

    /** 移除第一个匹配表达式，生成一个新的 */
    fun removeFirst(): ComplexCmptExp {
        val cache = firstCache?.get()
        if (cache != null) return cache
        val newList = LinkedList(list)
        newList.removeFirst()
        val result = ComplexCmptExp(Collections.unmodifiableList(newList))
        firstCache = WeakReference(result)
        return result
    }

    /** 判断第一个匹配表达式是否能够匹配指定控件 */
    fun matchFirst(cmpt: Cmpt) = list.first().match(cmpt)

    val size: Int
        get() = list.size

    override fun compareTo(other: ComplexCmptExp) = list.compareTo(other.list)

    override fun equals(other: Any?): Boolean {
        if (other !is ComplexCmptExp) return false
        return list == other.list
    }

    override fun hashCode() = list.hashCode()

    override fun toString() = list.toString()

}