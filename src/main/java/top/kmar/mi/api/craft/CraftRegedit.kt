package top.kmar.mi.api.craft

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.util.ResourceLocation
import top.kmar.mi.api.craft.elements.CraftOutput
import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.craft.shapes.IShape
import top.kmar.mi.api.craft.shapes.OrderlyShape

private typealias HashMap = Object2ObjectOpenHashMap<ResourceLocation, Pair<IShape, CraftOutput>>

/**
 * 合成表注册机
 * @author EmptyDreams
 */
class CraftRegedit {

    /** 存储有序合成表 */
    private val orderlyRegistry = HashMap()
    /** 存储无序合成表 */
    private val disorderlyRegistry = HashMap()

    /**
     * 注册一个合成表
     * @param id 合成表的键，用于后续合成表管理
     * @param shape 合成规则
     * @param output 产物
     */
    fun registry(id: ResourceLocation, shape: IShape, output: CraftOutput) {
        val pair = Pair(shape, output.copy())
        if (shape is OrderlyShape) {
            if (id in orderlyRegistry) throw IllegalArgumentException("指定的key[$id]已经被注册")
            orderlyRegistry[id] = pair
        } else {
            if (id in disorderlyRegistry) throw IllegalArgumentException("指定的key[$id]已经被注册")
            disorderlyRegistry[id] = pair
        }
    }

    /** 移除指定合成表 */
    fun delete(id: ResourceLocation) {
        orderlyRegistry.remove(id)
        disorderlyRegistry.remove(id)
    }

    /** 删除满足指定条件的合成表 */
    fun deleteIf(predicate: (ResourceLocation, LazyNode) -> Boolean) {
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
    fun findOutput(id: ResourceLocation) =
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
        private val craftOutput: CraftOutput
    ) {

        val output by lazy(LazyThreadSafetyMode.NONE) { craftOutput.copy() }

    }

}