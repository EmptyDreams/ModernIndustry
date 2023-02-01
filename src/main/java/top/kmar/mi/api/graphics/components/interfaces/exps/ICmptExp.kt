package top.kmar.mi.api.graphics.components.interfaces.exps

import top.kmar.mi.api.graphics.components.interfaces.Cmpt

/**
 * 控件匹配表达式
 * @author EmptyDreams
 */
interface ICmptExp {

    /** 判断指定控件与该表达式是否相匹配 */
    fun match(cmpt: Cmpt): Boolean

}