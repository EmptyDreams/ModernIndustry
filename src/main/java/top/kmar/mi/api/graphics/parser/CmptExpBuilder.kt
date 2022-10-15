package top.kmar.mi.api.graphics.parser

import top.kmar.mi.api.graphics.components.interfaces.ComplexCmptExp
import top.kmar.mi.api.graphics.components.interfaces.SingleCmpExp
import java.util.*

/**
 * 表达式构建器
 * @author EmptyDreams
 */
class CmptExpBuilder {

    private val list = LinkedList<LinkedList<ComplexCmptExp>>()

    fun next() {
        list.addLast(LinkedList())
    }

    fun prev() {
        list.removeLast()
    }

    fun addExp(exp: ComplexCmptExp) {
        list.last.add(exp)
    }

    fun toExp(consumer: (ComplexCmptExp) -> Unit) {
        var resultList = LinkedList<LinkedList<SingleCmpExp>>()
        resultList.add(LinkedList())
        for (external in list) {
            if (external.isEmpty()) continue
            if (external.size == 1) resultList.forEach { it.addAll(external.first.list) }
            else {
                val src = resultList
                resultList = LinkedList()
                for (exp in external) {
                    val newList = LinkedList(src)
                    for (linkedList in newList) {
                        linkedList.addAll(exp.list)
                    }
                    resultList.addAll(newList)
                }
            }
        }
        resultList.forEach { consumer(ComplexCmptExp(Collections.unmodifiableList(it))) }
    }

}