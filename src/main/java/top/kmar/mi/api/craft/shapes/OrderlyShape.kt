package top.kmar.mi.api.craft.shapes

import top.kmar.mi.api.craft.elements.ElementList
import top.kmar.mi.api.craft.elements.ICraftElement
import top.kmar.mi.api.craft.elements.handler.CraftTypeRegedit

/**
 * 有序列表
 * @author EmptyDreams
 */
class OrderlyShape private constructor(
    private val content: Array<Array<ICraftElement>>,
    override val type: Class<Any>
) : IShape {

    val height = content.size
    val width = content[0].size

    override fun match(input: ElementList<Any>): Boolean {
        if (input.type != type) return false
        val handler = CraftTypeRegedit.getHandler(type)
        val realInput = handler.clipSpace(input)
        if (realInput.width != width || realInput.height != height) return false
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (!content[y][x].match(input[x, y]))
                    return false
            }
        }
        return true
    }

}