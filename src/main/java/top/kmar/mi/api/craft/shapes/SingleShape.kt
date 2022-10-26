package top.kmar.mi.api.craft.shapes

import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.craft.elements.ICraftElement

/**
 * 只含有一个元素的物品列表
 * @author EmptyDreams
 */
class SingleShape(
    private val value: ICraftElement,
    override val type: Class<Any>
) : IShape {

    override fun match(input: ElementList<Any>): Boolean {
        if (input.size != 1) return false
        return type == input.type && value.match(input[0])
    }

}