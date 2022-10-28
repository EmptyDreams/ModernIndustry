package top.kmar.mi.api.craft.shapes

import top.kmar.mi.api.craft.elements.ElementList

/**
 * 有序或无序的不可变元素列表
 * @author EmptyDreams
 */
interface IShape {

    /** 判断是否与指定列表相匹配 */
    fun match(input: ElementList): Boolean

}