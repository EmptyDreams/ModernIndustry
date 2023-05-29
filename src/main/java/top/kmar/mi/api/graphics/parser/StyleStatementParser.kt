package top.kmar.mi.api.graphics.parser

import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.modes.*
import top.kmar.mi.api.graphics.utils.style.Direction2StyleManager
import top.kmar.mi.api.graphics.utils.style.Direction4StyleManager
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.expands.checkInt
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
            "padding", "margin" -> parserIntDirection(value, node.getValue(key))
            "padding-left", "padding-right", "padding-top", "padding-bottom",
            "margin-left",  "margin-right",  "margin-top",  "margin-bottom" -> {
                assert(value.checkInt()) { "值不是整数：$value" }
                value.toDecInt()
            }
            "display" -> parserDisplay(value)
            "position" -> parserPosition(value)
            "align" -> parserAlign(value, node.getValue(key))
            "align-vertical" -> VerticalAlignModeEnum.from(value)
            "align-horizontal" -> HorizontalAlignModeEnum.from(value)
            "color", "background-color" -> IntColor(value)
            "border" -> parserBorderAll(value, node.getValue(key))
            "border-left", "border-right", "border-top", "border-bottom" -> {
                val border = node.getValue<BorderStyle>(key)
                parserBorderContent(
                    value,
                    { color -> border.color = color },
                    { weight -> border.weight = weight }
                )
            }
            "button" -> parserButton(value, node.getValue("button"))
            "button-style" -> ButtonStyleEnum.from(value)
            "button-direction", "progress-direction" -> Direction2DEnum.from(value)
            "progress-style" -> ProgressBarStyle.from(value)
            "progress-text" -> ProgressBarDirection.from(value)
            "progress-min-height", "progress-max-height" -> {
                assert(value.checkInt()) { "值不是整数：$value" }
                value.toDecInt()
            }
            else -> throw IllegalArgumentException("未知的属性名称：$key")
        }
        node[key] = item
    }

    private fun parserButton(value: String, button: ButtonStyleData): ButtonStyleData {
        val list = value.split(' ').filter { it.isNotBlank() }
        when (list.size) {
            1 -> {
                when (val name = list.first()) {
                    "rect" -> button.style = ButtonStyleEnum.RECT
                    "triangle" -> button.style = ButtonStyleEnum.TRIANGLE
                    else -> button.direction = Direction2DEnum.from(name)
                }
            }
            2 -> {
                button.style = ButtonStyleEnum.from(list.first())
                button.direction = Direction2DEnum.from(list.last())
            }
            else -> throw IllegalArgumentException("非法表达式：$value")
        }
        return button
    }

    /** 解析 border 单个表达式 */
    private inline fun parserBorderContent(value: String, colorEditor: (IntColor) -> Unit, weightEditor: (Int) -> Unit) {
        val list = value.split(' ').filter { it.isNotBlank() }
        when (list.size) {
            1 -> {
                val content = list.first()
                if (content.checkInt()) {
                    // weight
                    weightEditor(content.toDecInt())
                } else {
                    // color
                    colorEditor(IntColor(content))
                }
            }
            2 -> {
                // weight color
                assert(list.first().checkInt()) { "weight color 中的 weight 应当为一个整数" }
                weightEditor(list.first().toDecInt())
                colorEditor(IntColor(list.last()))
            }
            else -> throw IllegalArgumentException("非法表达式：$value")
        }
    }

    private fun parserBorderAll(
        value: String, manager: Direction4StyleManager<BorderStyle>
    ): Direction4StyleManager<BorderStyle> {
        val list = value.split(',')
        when (list.size) {
            1 -> parserBorderContent(
                    list.first(),
                    { color -> manager.forEachAll { it.color = color } },
                    { weight -> manager.forEachAll { it.weight = weight } }
                )
            2 -> {
                parserBorderContent(
                    list.first(),
                    { color ->
                        manager.top.color = color
                        manager.bottom.color = color
                    },
                    { weight ->
                        manager.top.weight = weight
                        manager.bottom.weight = weight
                    }
                )
                parserBorderContent(
                    list.last(),
                    { color ->
                        manager.left.color = color
                        manager.right.color = color
                    },
                    { weight ->
                        manager.left.weight = weight
                        manager.right.weight = weight
                    }
                )
            }
            4 -> {
                val itor = list.iterator()
                manager.forEachAll { border ->
                    parserBorderContent(
                        itor.next(),
                        { color -> border.color = color },
                        { weight -> border.weight = weight}
                    )
                }
            }
            else -> throw IllegalArgumentException("非法表达式：$value")
        }
        return manager
    }

    /** 解析 align 表达式 */
    private fun parserAlign(value: String, manager: Direction2StyleManager<AlignMode>): Direction2StyleManager<AlignMode> {
        val list = value.split(' ').filter { it.isNotBlank() }
        assert(list.size == 2)
        manager.vertical = VerticalAlignModeEnum.from(list.first())
        manager.horizontal = HorizontalAlignModeEnum.from(list.last())
        return manager
    }

    /** 解析 position 表达式 */
    private fun parserPosition(value: String): PositionEnum =
        when (value) {
            "relative" -> PositionEnum.RELATIVE
            "absolute" -> PositionEnum.ABSOLUTE
            "fixed" -> PositionEnum.FIXED
            else -> throw IllegalArgumentException("未知的表达式：$value")
        }

    /** 解析 display 表达式 */
    private fun parserDisplay(value: String): DisplayModeEnum =
        when (value) {
            "block" -> DisplayModeEnum.DEF
            "inline" -> DisplayModeEnum.INLINE
            "none" -> DisplayModeEnum.NONE
            else -> throw IllegalArgumentException("未知的表达式：$value")
        }

    /** 解析带方向的数字表达式 */
    private fun parserIntDirection(value: String, manager: Direction4StyleManager<Int>): Direction4StyleManager<Int> {
        val list = value.split(' ')
            .filter { it.isNotBlank() }
            .map {
                assert(it.checkInt()) { "值中包含非整数类型" }
                it.toDecInt()
            }
        when (list.size) {
            1 -> manager.setAll(list.first())
            2 -> {
                manager.setTopAndBottom(list.first())
                manager.setLeftAndRight(list.last())
            }
            4 -> manager.setAll(list)
            else -> throw IllegalArgumentException("错误的参数：$value")
        }
        return manager
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
                    assert(calc.checkInt(end = calc.length - 1)) { "calc(x%) 中的 x 仅支持整数" }
                    val percent = calc.toDecInt() / 100.0
                    PercentSizeMode(percent, 0, function)
                } else {
                    val index = calc.indexOfFirst { it == '-' || it == '+' }
                    if (index == -1) {
                        // calc(x)
                        assert(calc.checkInt()) { "calc(x) 中的 x 仅支持整数" }
                        FixedSizeMode(calc.toDecInt())
                    } else {
                        val xStr = calc.substring(0, index).trim()
                        assert(calc.checkInt(start = index)) { "calc(? +- y) 中的 y 仅支持整数" }
                        val x = xStr.toDecInt()
                        val y = calc.toDecInt(index)
                        if (xStr.endsWith('%')) {
                            // calc(x% +- y)
                            assert(xStr.checkInt(end = xStr.length - 1)) { "calc(x% +- y) 中的 x 仅支持整数" }
                            PercentSizeMode(x / 100.0, y, function)
                        } else {
                            // calc(x +- y)
                            assert(xStr.checkInt()) { "calc(x +- y) 中的 x 仅支持整数" }
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