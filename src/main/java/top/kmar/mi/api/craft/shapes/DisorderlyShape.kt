package top.kmar.mi.api.craft.shapes

import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.craft.elements.ICraftElement
import top.kmar.mi.api.craft.elements.handler.CraftTypeRegedit

/**
 * 无序的元素列表
 * @author EmptyDreams
 */
class DisorderlyShape private constructor(
    private val content: List<ICraftElement>,
    override val type: Class<Any>
): IShape {

    override fun match(input: ElementList<Any>): Boolean {
        if (type != input.type) return false
        val list = CraftTypeRegedit.getHandler(type).merge(input)
        if (list.size != content.size) return false
        for (element in content) {
            val itor = list.iterator()
            while (itor.hasNext()) {
                if (element.match(itor.next())) {
                    itor.remove()
                    break
                }
            }
        }
        return list.isEmpty()
    }

}