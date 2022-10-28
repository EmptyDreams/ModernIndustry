package top.kmar.mi.api.craft.shapes

import net.minecraft.item.ItemStack
import top.kmar.mi.api.craft.elements.ElementList
import java.util.*
import java.util.function.Predicate

/**
 * 无序的元素列表
 * @author EmptyDreams
 */
class DisorderlyShape private constructor(
    private val content: List<Predicate<ItemStack>>
): IShape {

    override fun match(input: ElementList): Boolean {
        val list = input.disorderly
        if (list.size != content.size) return false
        for (element in content) {
            val itor = list.iterator()
            while (itor.hasNext()) {
                if (element.test(itor.next())) {
                    itor.remove()
                    break
                }
            }
        }
        return list.isEmpty()
    }

    class Builder {

        private val list = LinkedList<Predicate<ItemStack>>()

        fun add(value: Predicate<ItemStack>): Builder {
            list.add(value)
            return this
        }

        fun build(): DisorderlyShape = DisorderlyShape(list)

    }

}