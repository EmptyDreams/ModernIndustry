package top.kmar.mi.api.craft.elements.handler

/**
 * [Int]的handler
 * @author EmptyDreams
 */
object IntCraftTypeHandler : ICraftTypeHandler<Int> {

    override val type = Int::class.java

    override fun defValue() = 0

    override fun grow(original: Int, value: Int) = original + value

    override fun shrink(original: Int, value: Int) = original - value

    override fun isEmpty(value: Int) = false

    override fun canMerge(left: Int, right: Int) = true
}