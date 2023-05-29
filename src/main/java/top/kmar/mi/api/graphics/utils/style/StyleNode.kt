package top.kmar.mi.api.graphics.utils.style

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.modes.*
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import java.util.function.Function

/**
 * GUI 样式节点
 * @author EmptyDreams
 */
class StyleNode {

    private val sheet = Object2ObjectOpenHashMap<String, Any>(16)

    /** 将指定节点合并到当前节点中，若两者中的 key 存在重复，则使用`that`中的值覆盖当前值 */
    fun merge(that: StyleNode) {
        sheet.putAll(that.sheet)
    }

    /** 设置一个数据 */
    @JvmName("putValue")
    operator fun set(key: String, value: Any) {
        sheet[key] = value
    }

    /** 判断当前节点是否显示声明了指定的属性 */
    operator fun contains(key: String): Boolean = key in sheet

    fun getIntValue(key: String): Int =
        sheet.getOrElse(key) { DEF_VALUE_MAP[key]!! } as Int

    fun getStringValue(key: String): String =
        sheet.getOrElse(key) { DEF_VALUE_MAP[key]!! } as String

    fun getColorValue(key: String): IntColor =
        sheet.getOrElse(key) { DEF_VALUE_MAP[key]!! } as IntColor

    @Suppress("UNCHECKED_CAST")
    fun getBorderValue(key: String): BorderStyle =
        sheet.getOrPut(key) { (DEF_VALUE_MAP[key] as Function<StyleNode, Any>).apply(this) } as BorderStyle

    @Suppress("UNCHECKED_CAST")
    fun getProgressBarValue(): ProgressBarData =
        sheet.getOrPut("progress") { (DEF_VALUE_MAP["progress"] as Function<StyleNode, Any>).apply(this) } as ProgressBarData

    @Suppress("UNCHECKED_CAST")
    fun getButtonValue(): ButtonStyleData =
        sheet.getOrPut("button") { (DEF_VALUE_MAP["button"] as Function<StyleNode, Any>).apply(this) } as ButtonStyleData

    @Suppress("UNCHECKED_CAST")
    fun <T : Enum<*>> getEnumValue(key: String): T =
        sheet.getOrElse(key) { DEF_VALUE_MAP[key]!! } as T

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getManager(key: String): T {
        val result = sheet.getOrPut(key) { (DEF_VALUE_MAP[key] as Function<StyleNode, Any> ).apply(this) } as T
        assert(
            result is Direction4StyleManager<*> ||
            result is Direction2StyleManager<*> ||
            result is ButtonStyleData ||
            result is ProgressBarData
        )
        return result
    }

    fun copy(): StyleNode {
        val result = StyleNode()
        result.sheet.putAll(sheet)
        return result
    }

    companion object {

        @JvmStatic
        private val DEF_VALUE_MAP =
            Object2ObjectOpenHashMap<String, Any>(64).apply {
                this["width"] = "auto"
                this["height"] = "auto"
                this["padding"] = Function<StyleNode, Any> { Direction4StyleManager<Int>(it, "padding") }
                this["margin"] = Function<StyleNode, Any> { Direction4StyleManager<Int>(it, "margin") }
                this["padding-top"] = 0
                this["padding-right"] = 0
                this["padding-bottom"] = 0
                this["padding-left"] = 0
                this["margin-top"] = 0
                this["margin-right"] = 0
                this["margin-bottom"] = 0
                this["margin-left"] = 0
                this["display"] = DisplayModeEnum.DEF
                this["position"] = PositionEnum.RELATIVE
                this["align"] = Function<StyleNode, Any> { Direction2StyleManager<IAlignMode>(it, "align") }
                this["align-vertical"] = VerticalAlignModeEnum.TOP
                this["align-horizontal"] = HorizontalAlignModeEnum.LEFT
                this["color"] = IntColor.black
                this["background-color"] = IntColor.transparent
                this["border"] = Function<StyleNode, Any> { Direction4StyleManager<BorderStyle>(it, "border") }
                this["border-top"] = Function<StyleNode, Any> { BorderStyle() }
                this["border-right"] = Function<StyleNode, Any> { BorderStyle() }
                this["border-bottom"] = Function<StyleNode, Any> { BorderStyle() }
                this["border-left"] = Function<StyleNode, Any> { BorderStyle() }
                this["button"] = Function<StyleNode, Any> { ButtonStyleData(GraphicsStyle(Cmpt.EMPTY_CMPT)) }
                this["button-style"] = ButtonStyleEnum.RECT
                this["button-direction"] = Direction2DEnum.RIGHT
                this["progress"] = Function<StyleNode, Any> { ProgressBarData(GraphicsStyle(Cmpt.EMPTY_CMPT)) }
                this["progress-style"] = ProgressBarStyle.ARROW
                this["progress-text"] = ProgressBarDirection.NONE
                this["progress-min-height"] = 3
                this["progress-min-width"] = 3
            }

    }

}