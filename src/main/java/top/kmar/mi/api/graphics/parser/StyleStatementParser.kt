package top.kmar.mi.api.graphics.parser

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.modes.*
import top.kmar.mi.api.graphics.utils.style.Direction2StyleManager
import top.kmar.mi.api.graphics.utils.style.Direction4StyleManager
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import top.kmar.mi.api.utils.expands.checkInt
import top.kmar.mi.api.utils.expands.toDecInt

/**
 * 样式表语句解析器
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
object StyleStatementParser {

    /**
     * 解析一条语句
     * @param statement 要解析的语句
     * @param node 结果存放的节点
     * @return 是否解析成功
     */
    @JvmName("parser")
    operator fun invoke(statement: String, node: StyleNode): Boolean {
        val list = statement.split(' ', ':', limit = 2).map { it.trim() }
        if (list.size != 2) return false
        val (key, value) = list
        val item: Any = when (key) {
            "width" -> parserCalcStatement(value, false)
            "height" -> parserCalcStatement(value, true)
            "padding" -> parserIntDirection(value, node.padding)
            "margin" -> parserIntDirection(value, node.margin)
            "padding-left", "padding-right", "padding-top", "padding-bottom",
            "margin-left",  "margin-right",  "margin-top",  "margin-bottom" -> {
                assert(value.checkInt()) { "值不是整数：$value" }
                value.toDecInt()
            }
            "display" -> DisplayModeEnum.of(value)
            "position" -> PositionEnum.of(value)
            "align" -> parserAlign(value, node.align)
            "align-y" -> VerticalAlignModeEnum.of(value)
            "align-x" -> HorizontalAlignModeEnum.of(value)
            "color", "background-color", "progress-text-color" -> IntColor(value)
            "border" -> parserBorderAll(value, node.border)
            "border-left", "border-right", "border-top", "border-bottom" -> {
                val border = node.getValue<BorderStyle>(key)
                parserBorderContent(
                    value,
                    { color -> border.color = color },
                    { weight -> border.weight = weight }
                )
            }
            "button-style" -> parserButton(value, node.buttonStyle)
            "button-variety" -> ButtonStyleEnum.of(value)
            "button-direction", "progress-direction" -> Direction2DEnum.of(value)
            "progress-style" -> parserProgress(value, node.progressStyle)
            "progress-variety" -> ProgressBarStyle.of(value)
            "progress-text" -> ProgressBarTextEnum.of(value)
            "progress-min-width", "progress-max-height" -> {
                assert(value.checkInt()) { "值不是整数：$value" }
                value.toDecInt()
            }
            "overflow" -> parserOverflow(value, node.overflow)
            "overflow-x", "overflow-y" -> OverflowMode.of(value)
            else -> return false
        }
        node[key] = item
        return true
    }

    private fun parserOverflow(
        value: String, manager: Direction2StyleManager<OverflowMode>
    ): Direction2StyleManager<OverflowMode> {
        val list = value.split(' ').filter { it.isNotBlank() }
        when (list.size) {
            1 -> manager.setAll(OverflowMode.of(list.first()))
            2 -> {
                manager.x = OverflowMode.of(list.first())
                manager.y = OverflowMode.of(list.last())
            }
            else -> throw IllegalArgumentException("无效的参数数量：$value")
        }
        return manager
    }

    private fun parserProgress(value: String, progress: ProgressBarData): ProgressBarData {
        var numIndex = 0
        value.splitToSequence(' ')
            .filter { it.isNotBlank() }
            .forEach { item ->
                when (item) {
                    "rect" -> progress.style = ProgressBarStyle.RECT
                    "arrow" -> progress.style = ProgressBarStyle.ARROW
                    "top" -> progress.direction = Direction2DEnum.UP
                    "right" -> progress.direction = Direction2DEnum.RIGHT
                    "bottom" -> progress.direction = Direction2DEnum.DOWN
                    "left" -> progress.direction = Direction2DEnum.LEFT
                    else -> {
                        val text = ProgressBarTextEnum.tryOf(item)
                        if (text != null) {
                            progress.text = text
                        } else if (item.checkInt()) {
                            if (numIndex++ == 0) progress.minWidth = item.toDecInt()
                            else progress.minHeight = item.toDecInt()
                        } else {
                            progress.color = IntColor(item)
                        }
                    }
                }
            }
        return progress
    }

    private fun parserButton(value: String, button: ButtonStyleData): ButtonStyleData {
        val list = value.split(' ').filter { it.isNotBlank() }
        when (list.size) {
            1 -> {
                when (val name = list.first()) {
                    "rect" -> button.style = ButtonStyleEnum.RECT
                    "triangle" -> button.style = ButtonStyleEnum.TRIANGLE
                    else -> button.direction = Direction2DEnum.of(name)
                }
            }
            2 -> {
                button.style = ButtonStyleEnum.of(list.first())
                button.direction = Direction2DEnum.of(list.last())
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
    private fun parserAlign(value: String, manager: Direction2StyleManager<IAlignMode>): Direction2StyleManager<IAlignMode> {
        val list = value.split(' ').filter { it.isNotBlank() }
        assert(list.size == 2)
        manager.y = VerticalAlignModeEnum.of(list.first())
        manager.x = HorizontalAlignModeEnum.of(list.last())
        return manager
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
        value: String, isHeight: Boolean
    ): ISizeMode =
        when {
            // 百分比计算（x%）
            value.endsWith('%') -> {
                val percent = value.toDecInt() / 100.0
                PercentSizeMode(percent, 0, isHeight)
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
                    PercentSizeMode(percent, 0, isHeight)
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
                            PercentSizeMode(x / 100.0, y, isHeight)
                        } else {
                            // calc(x +- y)
                            assert(xStr.checkInt()) { "calc(x +- y) 中的 x 仅支持整数" }
                            RelativeSizeMode(y, isHeight)
                        }
                    }
                }
            }
            value == "inherit" -> InheritSizeMode.instance(isHeight)
            value == "auto" -> AutoSizeMode.instance(isHeight)
            else -> FixedSizeMode(value.toDecInt())
        }

}