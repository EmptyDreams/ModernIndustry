package top.kmar.mi.api.graphics.components.interfaces.exps

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptRegister
import top.kmar.mi.api.utils.expands.compareTo

/**
 * 单个[Cmpt]匹配表达式
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

    override fun match(cmpt: Cmpt): Boolean {
        for (node in list) {
            when {
                node.className -> if (node.content !in cmpt.classList) return false
                node.id -> {
                    if (node.content.length != cmpt.id.length + 1) return false
                    if (!node.content.endsWith(cmpt.id)) return false
                }
                node.tag -> if (CmptRegister.find(node.content) != cmpt.javaClass) return false
                else -> return true
            }
        }
        return true
    }

    fun match(exp: String) = this == SingleCmptExp(exp)

    override fun hashCode() = list.hashCode()

    override fun compareTo(other: SingleCmptExp) = list.compareTo(other.list)

    override fun equals(other: Any?): Boolean {
        if (other !is SingleCmptExp) return false
        return list == other.list
    }

    override fun toString() = list.joinToString(" ")

    private class Node(val content: String): Comparable<Node> {

        /** 是否是类名表达式 */
        val className = content[0] == '.'

        /** 是否是类名表达式 */
        val id = content[0] == '#'

        /** 是否是类型名表达式 */
        val tag = !className && !id && !content.startsWith('*')

        override fun compareTo(other: Node) = content.compareTo(other.content)

        override fun hashCode() = content.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other !is Node) return false
            return content == other.content
        }

        override fun toString() = content

    }

}