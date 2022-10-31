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

    class Builder {

        private val list = LinkedList<Predicate<ItemStack>>()

        fun add(value: Predicate<ItemStack>): Builder {
            list.add(value)
            return this
        }

        fun build(): DisorderlyShape = DisorderlyShape(list)

    }

}