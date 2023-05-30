package top.kmar.mi.api.graphics.utils.style

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.exps.ICmptExp
import top.kmar.mi.api.utils.expands.floorDiv2
import top.kmar.mi.api.utils.expands.forEachFast

/**
 * GUI 样式表
 * @author EmptyDreams
 */
class StyleSheet : Iterable<Pair<ICmptExp, StyleNode>> {

    private val sheetKey = ArrayList<ICmptExp>()
    private val sheetValue = ArrayList<StyleNode>()

    /** 根据 ID 获取样式 */
    fun getStyle(list: IntList): StyleNode {
        val result = StyleNode()
        list.forEachFast { result.merge(sheetValue[it]) }
        return result
    }

    /** 根据组件匹配样式 */
    fun getStyle(cmpt: Cmpt): StyleNode {
        val result = StyleNode()
        sheetKey.forEachIndexed { index, exp ->
            if (exp.match(cmpt)) result.merge(sheetValue[index])
        }
        return result
    }

    /** 获取指定组件满足的所有表达式 */
    fun getIndex(cmpt: Cmpt): IntList {
        val result = IntArrayList(sheetKey.size.floorDiv2().coerceAtMost(10))
        sheetKey.forEachIndexed { index, exp ->
            if (exp.match(cmpt)) result += index
        }
        result.trim()
        return result
    }

    /** 获取指定组件满足的所有表达式 */
    fun getIndex(cmpt: CmptClient) = getIndex(cmpt.service)

    /** 获取指定下标的表达式 */
    @JvmName("getNode")
    operator fun get(index: Int): StyleNode = sheetValue[index]

    /** 添加一个表达式 */
    fun add(exp: ICmptExp, node: StyleNode) {
        val old = sheetKey.indexOf(exp)
        if (old == -1) {
            sheetKey += exp
            sheetValue += node
        } else {
            sheetValue[old].merge(node)
        }
    }

    override fun iterator() = object : Iterator<Pair<ICmptExp, StyleNode>> {

        var index = 0

        override fun hasNext(): Boolean = index != sheetKey.size

        override fun next(): Pair<ICmptExp, StyleNode> {
            val exp = sheetKey[index]
            val node = sheetValue[index]
            ++index
            return Pair(exp, node)
        }

    }

}