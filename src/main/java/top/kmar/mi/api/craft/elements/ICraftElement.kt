package top.kmar.mi.api.craft.elements

/**
 * 表示合成表中的一个元素（不可变）
 * @author EmptyDreams
 */
interface ICraftElement {

    /** 判断指定输入能否和当前元素匹配 */
    fun match(input: Any): Boolean

    /**
     * 从输入中删除合成表需要的元素
     * @param input 输入的元素，函数可以修改该元素的内容
     * @return 删除后的元素
     */
    fun <T : Any> reduce(input: T): T

}