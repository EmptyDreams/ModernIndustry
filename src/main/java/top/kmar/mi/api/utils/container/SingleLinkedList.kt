package top.kmar.mi.api.utils.container

import java.util.*
import kotlin.streams.toList

/**
 * 保证元素不重复的链表
 * @author EmptyDreams
 */
class SingleLinkedList<T> : LinkedList<T>() {

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return super.addAll(index, elements.stream().filter { it !in this }.toList())
    }

    override fun addLast(e: T) {
        if (e !in this) super.addLast(e)
    }

    override fun addFirst(e: T) {
        if (e !in this) super.addFirst(e)
    }

    override fun add(index: Int, element: T) {
        if (element !in this) super.add(index, element)
    }

    override fun add(element: T): Boolean {
        if (element in this) return false
        return super.add(element)
    }

}