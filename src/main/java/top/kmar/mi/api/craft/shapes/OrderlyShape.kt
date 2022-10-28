package top.kmar.mi.api.craft.shapes

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import top.kmar.mi.api.craft.elements.ElementList
import java.util.*
import java.util.function.Predicate

/**
 * 有序列表
 * @author EmptyDreams
 */
class OrderlyShape private constructor(
    private val content: Array<Array<Predicate<ItemStack>>>
) : IShape {

    val height = content.size
    val width = content[0].size

    override fun match(input: ElementList): Boolean {
        val realInput = input.minimum
        if (realInput.width != width || realInput.height != height) return false
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (!content[y][x].test(realInput[x, y]))
                    return false
            }
        }
        return true
    }

    class Builder {

        private var width = 0
        private var height = 0
        private val list = ArrayList<MutableList<Predicate<ItemStack>>>(5)

        /** 向最后一行末尾插入一个元素 */
        fun insert(predicate: Predicate<ItemStack>): Builder {
            val endLine = list.last()
            endLine.add(predicate)
            width = width.coerceAtLeast(endLine.size)
            return this
        }

        /** 加入一个新行 */
        fun newLine(): Builder {
            list.add(LinkedList())
            ++height
            return this
        }

        fun build(): OrderlyShape {
            val array = Array(height) { y ->
                Array(width) { x ->
                    if (x < list[y].size) list[y][x]
                    else Ingredient.EMPTY
                }
            }
            return OrderlyShape(array)
        }

    }

}