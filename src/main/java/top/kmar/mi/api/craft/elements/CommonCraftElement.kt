package top.kmar.mi.api.craft.elements

/**
 * 基于[Any]的合成表元素
 * @author EmptyDreams
 */
class CommonCraftElement(private val value: Any) : ICraftElement {

    override fun match(input: Any) = input == value

    override fun <T : Any> reduce(input: T): T {
        throw UnsupportedOperationException("该类型不支持reduce操作")
    }

}