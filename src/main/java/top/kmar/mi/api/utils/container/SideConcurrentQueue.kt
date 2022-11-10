package top.kmar.mi.api.utils.container

import top.kmar.mi.api.utils.expands.isServer
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 *
 * @author EmptyDreams
 */
class SideConcurrentQueue<T> : Queue<T> {

    private val server = ConcurrentLinkedQueue<T>()
    private val client = ConcurrentLinkedQueue<T>()

    private val queue: Queue<T>
        get() = if (isServer()) server else client

    override fun add(element: T) = queue.add(element)

    override fun addAll(elements: Collection<T>) = queue.addAll(elements)

    override fun clear() = queue.clear()

    override fun iterator() = queue.iterator()

    override fun remove(): T = queue.remove()

    @Suppress("ConvertArgumentToSet")
    override fun retainAll(elements: Collection<T>) = queue.retainAll(elements)

    @Suppress("ConvertArgumentToSet")
    override fun removeAll(elements: Collection<T>) = queue.removeAll(elements)

    override fun remove(element: T) = queue.remove(element)

    override fun isEmpty() = queue.isEmpty()

    override fun poll(): T = queue.poll()

    override fun element(): T = queue.element()

    override fun peek(): T = queue.peek()

    override fun offer(e: T) = queue.offer(e)

    override fun containsAll(elements: Collection<T>) = queue.containsAll(elements)

    override fun contains(element: T) = queue.contains(element)

    override val size: Int
        get() = queue.size
}