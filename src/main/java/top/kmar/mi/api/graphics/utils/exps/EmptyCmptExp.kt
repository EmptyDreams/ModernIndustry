package top.kmar.mi.api.graphics.utils.exps

import top.kmar.mi.api.graphics.components.interfaces.Cmpt

/**
 * 空的表达式
 * @author EmptyDreams
 */
object EmptyCmptExp : ICmptExp {

    override val size = 0

    override fun match(cmpt: Cmpt) = false

    override fun matchFirst(cmpt: Cmpt) = false

    override fun removeFirst() = this

}