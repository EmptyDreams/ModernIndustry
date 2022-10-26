package top.kmar.mi.api.craft.elements.handler

import top.kmar.mi.api.craft.elements.ElementList
import java.util.*

/**
 * 预处理[ElementList]的接口
 * @author EmptyDreams
 */
interface ICraftTypeHandler<T : Any> {

    val type: Class<T>

    /** 判断两个元素能否合并 */
    fun canMerge(left: T, right: T): Boolean

    /** 判断指定元素是否为空 */
    fun isEmpty(value: T): Boolean

    /** 从指定值中取出一部分，并返回新值（可在原有基础上修改） */
    fun shrink(original: T, value: T): T

    /** 将一定数量的值放入指定值中并返回新值（可在原有基础上修改） */
    fun grow(original: T, value: T): T

    /** 缺省值 */
    fun defValue(): T

    /** 合并同类项，可在原有基础上修改 */
    fun merge(list: ElementList<T>): MutableList<T> {
        val result = LinkedList<T>()
        list.asSequence()
            .filter { !isEmpty(it) }
            .forEach {
                for (output in result) {
                    if (canMerge(output, it)) {
                        grow(output, it)
                        return@forEach
                    }
                }
                result.add(it)
            }
        return result
    }

    /** 在不移动已有元素的情况下裁剪掉多余元素 */
    fun clipSpace(list: ElementList<T>): ElementList<T> {
        var left = 0
        var top = 0
        var right = list.width - 1
        var bottom = list.height - 1
        // 查找第一个包含非空元素的行
        o@for (y in 0 .. bottom) {
            for (x in 0 .. right) {
                if (!isEmpty(list[x, y])) {
                    top = y
                    break@o
                }
            }
            @Suppress("UNCHECKED_CAST")
            return ElementList.empty as ElementList<T>
        }
        // 查找最后一个包含非空元素的行
        o@for (y in bottom downTo top) {
            for (x in 0 .. right) {
                if (!isEmpty(list[x, y])) {
                    bottom = y
                    break@o
                }
            }
        }
        // 查找第一个包含非空元素的列
        o@for (x in 0 .. right) {
            for (y in top .. bottom) {
                if (!isEmpty(list[x, y])) {
                    left = x
                    break@o
                }
            }
        }
        // 查找最后一个包含非空元素的列
        o@for (x in right downTo left) {
            for (y in top .. bottom) {
                if (!isEmpty(list[x, y])) {
                    right = x
                    break@o
                }
            }
        }
        val result = ElementList(right - left + 1, bottom - top + 1, defValue(), type)
        for (y in top .. bottom) {
            for (x in left .. right)
                result[x - left, y - top] = list[x, y]
        }
        return result
    }

}