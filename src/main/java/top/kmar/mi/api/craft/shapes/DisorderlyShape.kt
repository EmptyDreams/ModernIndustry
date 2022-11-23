package top.kmar.mi.api.craft.shapes

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.utils.expands.ceilDiv
import java.util.*
import kotlin.math.min

/**
 * 无序的元素列表
 * @author EmptyDreams
 */
class DisorderlyShape private constructor(
    private val content: List<Ingredient>
): IShape {

    override fun match(input: ElementList): Boolean {
        val list = input.flat
        if (list.size != content.size) return false
        val content = LinkedList(content)
        list.forEach {
            val itor = content.iterator()
            while (itor.hasNext()) {
                if (itor.next().test(it)) {
                    itor.remove()
                    return@forEach
                }
            }
            return false
        }
        return true
    }
    
    override fun getAllInput(maxWidth: Int): Array<Array<Array<ItemStack>>> {
        val itor = content.iterator()
        val width = min(maxWidth, content.size)
        return Array(content.size.ceilDiv(maxWidth)) {
            Array(width) { if (itor.hasNext()) itor.next().matchingStacks else arrayOf(ItemStack.EMPTY) }
        }
    }
    
    class Builder {

        private val list = LinkedList<Ingredient>()

        fun add(value: Ingredient): Builder {
            list.add(value)
            return this
        }

        fun build(): DisorderlyShape = DisorderlyShape(list)

    }

}