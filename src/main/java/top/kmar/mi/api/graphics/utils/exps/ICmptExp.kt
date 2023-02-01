package top.kmar.mi.api.graphics.utils.exps

import top.kmar.mi.api.graphics.components.interfaces.Cmpt

/**
 * 控件匹配表达式
 * @author EmptyDreams
 */
interface ICmptExp {

    /** 表达式数量 */
    val size: Int

    /** 判断指定控件与该表达式是否相匹配 */
    fun match(cmpt: Cmpt): Boolean

    /** 判断指定控件与第一个表达式是否相匹配 */
    fun matchFirst(cmpt: Cmpt): Boolean

    /** 返回一个不包含第一个表达式的对象，返回的对象与当前对象相互独立 */
    fun removeFirst(): ICmptExp

}