package top.kmar.mi.api.graphics.components.interfaces

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet

/**
 * [Cmpt]匹配表达式
 * @author EmptyDreams
 */
class CmptSearchExp(private val value: String) {

    private val list = ObjectRBTreeSet<Node>().apply {
        var left = 0
        for (i in 1 until value.length) {
            val it = value[i]
            if (it == '.' || it == '#') {
                add(Node(value.substring(left until i)))
                left = i
            }
        }
        if (left != value.length - 1)
            add(Node(value.substring(left)))
    }

    fun match(cmpt: Cmpt): Boolean {
        for (node in list) {
            if (node.className) {
                if (node.content !in cmpt.classList) return false
            } else if (node.id) {
                if (node.content.length != cmpt.id.length + 1) return false
                if (!node.content.endsWith(cmpt.id)) return false
            } else {
                if (CmptRegister.find(node.content) != cmpt.javaClass) return false
            }
        }
        return true
    }

    fun match(exp: String) = this == CmptSearchExp(exp)

    override fun hashCode() = list.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is CmptSearchExp) return false
        return list == other.list
    }


    private class Node(val content: String): Comparable<Node> {

        /** 是否是类名表达式 */
        val className = content[0] == '.'

        /** 是否是类名表达式 */
        val id = content[0] == '#'

        /** 是否是类型名表达式 */
        val tag = !className && !id

        override fun compareTo(other: Node) = content.compareTo(other.content)

        override fun hashCode() = content.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other !is Node) return false
            return content == other.content
        }

    }

}