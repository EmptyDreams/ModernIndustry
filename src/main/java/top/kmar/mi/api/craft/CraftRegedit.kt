package top.kmar.mi.api.craft

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.craft.elements.CraftOutput
import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.craft.shapes.IShape
import top.kmar.mi.api.craft.shapes.OrderlyShape
import java.util.*

private typealias HashMap = Object2ObjectOpenHashMap<String, Pair<IShape, CraftOutput>>

/**
 * 合成表注册机
 * @author EmptyDreams
 */
class CraftRegedit : Iterable<CraftRegedit.LazyNode> {

    /** 存储有序合成表 */
    private val orderlyRegistry = HashMap()
    /** 存储无序合成表 */
    private val disorderlyRegistry = HashMap()
    /** 存储所有 ID */
    private val idList = LinkedList<String>()

    /**
     * 注册一个合成表
     * @param id 合成表的键，用于后续合成表管理
     * @param shape 合成规则
     * @param output 产物
     */
    fun registry(id: String, shape: IShape, output: CraftOutput) {
        require(id !in orderlyRegistry) { "指定的key[$id]已经被注册" }
        require(id !in disorderlyRegistry) { "指定的key[$id]已经被注册" }
        val pair = Pair(shape, output.copy())
        if (shape is OrderlyShape) {
            orderlyRegistry[id] = pair
        } else {
            disorderlyRegistry[id] = pair
        }
        idList += id
    }

    /** 移除指定合成表 */
    fun delete(id: String) {
        orderlyRegistry.remove(id)
        disorderlyRegistry.remove(id)
    }

    /** 删除满足指定条件的合成表 */
    fun deleteIf(predicate: (String, LazyNode) -> Boolean) {
        fun deleteTask(map: HashMap) {
            val itor = map.iterator()
            while (itor.hasNext()) {
                val (key, value) = itor.next()
                if (predicate(key, LazyNode(value.first, value.second)))
                    itor.remove()
            }
        }
        deleteTask(orderlyRegistry)
        deleteTask(disorderlyRegistry)
    }

    /** 通过 ID 查找合成表输出，未查询到返回`null` */
    fun findOutput(id: String) =
        (orderlyRegistry[id] ?: disorderlyRegistry[id])?.second?.copy()

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

    /** 获取迭代器，遍历时按照注册顺序遍历 */
    override fun iterator(): ListIterator<LazyNode> = CraftIterator()

    private inner class CraftIterator : ListIterator<LazyNode> {

        private val itor = idList.listIterator()

        override fun hasNext() = itor.hasNext()

        override fun hasPrevious() = itor.hasPrevious()

        override fun next(): LazyNode {
            val id = itor.next()
            val (shape, output) = orderlyRegistry[id] ?: disorderlyRegistry[id]!!
            return LazyNode(shape, output)
        }

        override fun nextIndex() = itor.nextIndex()

        override fun previous(): LazyNode {
            val id = itor.previous()
            val (shape, output) = orderlyRegistry[id] ?: disorderlyRegistry[id]!!
            return LazyNode(shape, output)
        }

        override fun previousIndex() = itor.previousIndex()

    }

}