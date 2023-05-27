package top.kmar.mi.api.graphics.parser

import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.modes.*
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.utils.expands.toDecInt
import top.kmar.mi.api.utils.interfaces.Obj2IntFunction

/**
 * 样式表语句解析器
 * @author EmptyDreams
 */
object StyleStatementParser {

    /**
     * 解析一条语句
     * @param statement 要解析的语句
     * @param node 结果存放的节点
     */
    @JvmName("parser")
    operator fun invoke(statement: String, node: StyleNode) {
        val (key, value) = statement.split(' ', ':', limit = 2).map { it.trim() }
        val item: Any = when (key) {
            "width" -> parserCalcStatement(value, false) { it.width }
            "height" -> parserCalcStatement(value, true) { it.height }
            else -> {}
        }
        node[key] = item
    }

    /** 解析计算表达式 */
    private fun parserCalcStatement(
        value: String, isHeight: Boolean, function: Obj2IntFunction<GraphicsStyle>
    ): ISizeMode =
        when {
            // 百分比计算（x%）
            value.endsWith('%') -> {
                val percent = value.toDecInt() / 100.0
                PercentSizeMode(percent, 0, function)
            }
            // calc 公式计算（calc(x% +- y)）
            value.startsWith("calc") -> {
                val start = value.indexOf('(')
                val end = value.lastIndexOf(')')
                val calc = value.substring(start, end).trim()
                if (calc.endsWith('%')) {
                    // calc(x%)
                    val percent = calc.toDecInt() / 100.0
                    PercentSizeMode(percent, 0, function)
                } else {
                    val index = calc.indexOfFirst { it == '-' || it == '+' }
                    if (index == -1) {
                        // calc(x)
                        FixedSizeMode(calc.toDecInt())
                    } else {
                        val xStr = calc.substring(0, index).trim()
                        val x = xStr.toDecInt()
                        val y = calc.toDecInt(index)
                        if (xStr.endsWith('%')) {
                            // calc(x% +- y)
                            PercentSizeMode(x / 100.0, y, function)
                        } else {
                            // calc(x +- y)
                            RelativeSizeMode(y, function)
                        }
                    }
                }
            }
            value == "inherit" -> InheritSizeMode(function)
            value == "auto" -> AutoSizeMode(isHeight)
            else -> FixedSizeMode(value.toDecInt())
        }

}