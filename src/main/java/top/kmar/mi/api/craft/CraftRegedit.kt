package top.kmar.mi.api.craft

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import top.kmar.mi.api.craft.elements.CraftOutput
import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.craft.shapes.IShape
import top.kmar.mi.api.craft.shapes.OrderlyShape
import top.kmar.mi.api.utils.expands.removeFirst

private typealias List = ArrayList<Pair<String, Pair<IShape, CraftOutput>>>

/**
 * 合成表注册机
 * @author EmptyDreams
 */
class CraftRegedit : Iterable<CraftRegedit.LazyNode> {

    /** 存储有序合成表 */
    private val orderlyRegistry = List(64)
    /** 存储无序合成表 */
    private val disorderlyRegistry = List(64)
    /** 存储所有 ID */
    private val idRecord = ObjectRBTreeSet<String>()

    /**
     * 注册一个合成表
     * @param id 合成表的键，用于后续合成表管理
     * @param shape 合成规则
     * @param output 产物
     */
    fun registry(id: String, shape: IShape, output: CraftOutput) {
        require(id !in idRecord) { "指定的key[$id]已经被注册" }
        val pair = Pair(id, Pair(shape, output.copy()))
        if (shape is OrderlyShape) {
            orderlyRegistry += pair
        } else {
            disorderlyRegistry += pair
        }
        idRecord += id
    }

    /** 移除指定合成表 */
    fun delete(id: String) {
        idRecord -= id
        if (!orderlyRegistry.removeFirst { it.first == id })
            disorderlyRegistry.removeFirst { it.first == id }
    }

    /** 删除满足指定条件的合成表 */
    fun deleteIf(predicate: (String, LazyNode) -> Boolean) {
        orderlyRegistry.removeIf { predicate(it.first, LazyNode(it.second.first, it.second.second)) }
        disorderlyRegistry.removeIf { predicate(it.first, LazyNode(it.second.first, it.second.second)) }
    }

    /** 通过下标获取合成表元素 */
    operator fun get(index: Int): LazyNode {
        val (shape, output) = (if (index < orderlyRegistry.size) orderlyRegistry[index]
                    else disorderlyRegistry[index]).second
        return LazyNode(shape, output)
    }

    /** 通过 ID 查找合成表输出，未查询到返回`null` */
    fun findOutput(id: String): CraftOutput? {
        val orderly = orderlyRegistry.find { it.first == id }
        if (orderly != null) return orderly.second.second.copy()
        return disorderlyRegistry.find { it.first == id }?.second?.second?.copy()
    }

    /** 通过输入查询合成表输出，未查询到返回`null` */
    fun findOutput(input: ElementList): CraftOutput? =
        findOrderlyOutput(input) ?: findDisorderlyOutput(input)

    /** 通过输入查询有序合成表的输出，未查询到返回`null` */
    fun findOrderlyOutput(input: ElementList): CraftOutput? {
        orderlyRegistry.forEach { (_, value) ->
            if (value.first.match(input))
                return value.second.copy()
        }
        return null
    }

    /** 通过输入查询无序合成表的输出，未查询到返回`null` */
    fun findDisorderlyOutput(input: ElementList): CraftOutput? {
        disorderlyRegistry.forEach { (_, value) ->
            if (value.first.match(input))
                return value.second.copy()
        }
        return null
    }

    class LazyNode(
        val shape: IShape,
        craftOutput: CraftOutput
    ) {

        val output by lazy(LazyThreadSafetyMode.NONE) { craftOutput.copy() }

        operator fun component1() = shape

        operator fun component2() = output

    }

    /** 获取迭代器，遍历时按照注册顺序遍历（优先遍历有序合成表） */
    override fun iterator(): MutableListIterator<LazyNode> = CraftIterator()

    private inner class CraftIterator : MutableListIterator<LazyNode> {

        private val orderlyItor = orderlyRegistry.listIterator()
        private val disorderlyItor = disorderlyRegistry.listIterator()

        override fun add(element: LazyNode) {
            throw UnsupportedOperationException("该迭代器不允许插入操作")
        }

        override fun hasNext() = orderlyItor.hasNext() || disorderlyItor.hasNext()

        override fun hasPrevious() = disorderlyItor.hasPrevious() || orderlyItor.hasPrevious()

        override fun next(): LazyNode {
            val value = if (orderlyItor.hasNext()) orderlyItor.next() else disorderlyItor.next()
            val pair = value.second
            return LazyNode(pair.first, pair.second)
        }

        override fun nextIndex() =
            if (orderlyItor.hasNext()) orderlyItor.nextIndex() else orderlyRegistry.size + disorderlyItor.nextIndex()

        override fun previous(): LazyNode {
            val value = if (disorderlyItor.hasPrevious()) disorderlyItor.previous() else orderlyItor.previous()
            val pair = value.second
            return LazyNode(pair.first, pair.second)
        }

        override fun previousIndex() =
            if (disorderlyItor.hasPrevious()) orderlyRegistry.size + disorderlyItor.previousIndex() else orderlyItor.previousIndex()

        override fun remove() {
            if (orderlyItor.hasNext()) orderlyItor.remove()
            else disorderlyItor.remove()
        }

        override fun set(element: LazyNode) {
            throw UnsupportedOperationException("该迭代器不支持设置操作")
        }


    }

}