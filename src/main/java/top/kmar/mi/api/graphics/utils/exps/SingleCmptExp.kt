package top.kmar.mi.api.graphics.utils.exps

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptRegister
import top.kmar.mi.api.utils.expands.compareTo
import top.kmar.mi.api.utils.expands.startsWiths

/**
 * 单个 [Cmpt] 匹配表达式
 * @author EmptyDreams
 */
class SingleCmptExp(exp: String) : Comparable<SingleCmptExp>, ICmptExp {

    private val list = ObjectAVLTreeSet<Node>().apply {
        var left = -1
        for (i in exp.indices) {
            val it = exp[i]
            if ((it == '.' || it == '#') && i != 0) {
                if (left == -1) left = 0
                add(Node(exp.substring(left until i)))
                left = i
            }
        }
        if (left != exp.length - 1)
            add(Node(exp.substring(left.coerceAtLeast(0))))
    }

    override val size: Int
        get() = if (list.isEmpty()) 0 else 1

    override fun match(cmpt: Cmpt): Boolean {
        for (node in list) {
            when {
                node.className -> if (node.content !in cmpt.classList) return false
                node.id -> {
                    if (node.content != cmpt.id) return false
                }
                node.tag -> if (CmptRegister.find(node.content) != cmpt.javaClass) return false
                else -> return true
            }
        }
        return true
    }

    override fun matchFirst(cmpt: Cmpt) = match(cmpt)

    override fun removeFirst() = EmptyCmptExp

    fun match(exp: String) = this == SingleCmptExp(exp)

    override fun hashCode() = list.hashCode()

    override fun compareTo(other: SingleCmptExp) = list.compareTo(other.list)

    override fun equals(other: Any?): Boolean {
        if (other !is SingleCmptExp) return false
        return list == other.list
    }

    override fun toString() = list.joinToString(" ")

    private class Node(value: String): Comparable<Node> {

        /** 表达式内容（去除了前缀） */
        val content = when {
            value.startsWiths('.', '#') -> value.substring(1)
            else -> value
        }

        /** 是否是类名表达式 */
        val className = value[0] == '.'

        /** 是否是类名表达式 */
        val id = value[0] == '#'

        /** 是否是类型名表达式 */
        val tag = !className && !id && !value.startsWiths('*')

        override fun compareTo(other: Node) = content.compareTo(other.content)

        override fun hashCode() = content.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other !is Node) return false
            return content == other.content
        }

        override fun toString() = content

    }

}