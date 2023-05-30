package top.kmar.mi.api.graphics.parser

import top.kmar.mi.api.graphics.utils.exps.ComplexCmptExp
import top.kmar.mi.api.graphics.utils.exps.EmptyCmptExp
import top.kmar.mi.api.graphics.utils.exps.ICmptExp
import top.kmar.mi.api.graphics.utils.exps.SingleCmptExp
import java.util.*

/**
 * 表达式构建器
 * @author EmptyDreams
 */
class CmptExpBuilder {

    private val list = LinkedList<LinkedList<ComplexCmptExp>>()

    /**
     * 将构建器转移到指定层级（该层级一定为空层级）
     *
     * + 若 level 小于等于活跃层级，则会将 >= level 的层级移除，然后添加一个空的层级
     * + 若 level 大于活跃层级，则会添加新的层级，直到活跃层级为 level
     */
    fun goto(level: Int) {
        while (level < list.size) prev()
        while (level > list.size - 1) next()
    }

    /** 新建一个层级 */
    fun next() {
        list.addLast(LinkedList())
    }

    /** 移除活跃层级并移动到上一层 */
    fun prev() {
        list.removeLast()
    }

    /** 向活跃层级添加一个表达式 */
    fun addExp(exp: ComplexCmptExp) {
        list.last.add(exp)
    }

    /** 导出表达式 */
    fun toExp(consumer: (ICmptExp) -> Unit) {
        var resultList = LinkedList<LinkedList<SingleCmptExp>>()
        resultList.add(LinkedList())
        for (external in list) {
            if (external.isEmpty()) continue
            if (external.size == 1) resultList.forEach { it.addAll(external.first.list) }
            else {
                val src = resultList
                resultList = LinkedList()
                for (exp in external) {
                    val newList = LinkedList<LinkedList<SingleCmptExp>>()
                    for (linkedList in src) {
                        newList.add(LinkedList(linkedList))
                        newList.last.addAll(exp.list)
                    }
                    resultList.addAll(newList)
                }
            }
        }
        resultList.forEach {
            if (it.isEmpty()) consumer(EmptyCmptExp)
            else consumer(ComplexCmptExp(Collections.unmodifiableList(it)))
        }
    }

}