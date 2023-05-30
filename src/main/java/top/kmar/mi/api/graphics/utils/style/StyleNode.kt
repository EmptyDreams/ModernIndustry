package top.kmar.mi.api.graphics.utils.style

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.modes.*
import top.kmar.mi.api.utils.data.enums.Direction2DEnum
import java.util.function.Function
import java.util.function.IntSupplier
import kotlin.reflect.KProperty

/**
 * GUI 样式节点
 * @author EmptyDreams
 */
@Suppress("UNCHECKED_CAST")
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

    var width: ISizeMode by ValueDelegate
    var height: ISizeMode by ValueDelegate
    val padding: Direction4StyleManager<Int> by ObjectDelegate
    val margin: Direction4StyleManager<Int> by ObjectDelegate
    var paddingTop: Int by ValueDelegate
    var paddingRight: Int by ValueDelegate
    var paddingBottom: Int by ValueDelegate
    var paddingLeft: Int by ValueDelegate
    var marginTop: Int by ValueDelegate
    var marginRight: Int by ValueDelegate
    var marginBottom: Int by ValueDelegate
    var marginLeft: Int by ValueDelegate
    var display: DisplayModeEnum by ValueDelegate
    var position: PositionEnum by ValueDelegate
    val align: Direction2StyleManager<IAlignMode> by ObjectDelegate
    var alignVertical: VerticalAlignModeEnum by ValueDelegate
    var alignHorizontal: HorizontalAlignModeEnum by ValueDelegate
    var color: IntColor by ValueDelegate
    var backgroundColor: IntColor by ValueDelegate
    val border: Direction4StyleManager<BorderStyle> by ObjectDelegate
    val borderTop: BorderStyle by ObjectDelegate
    val borderRight: BorderStyle by ObjectDelegate
    val borderBottom: BorderStyle by ObjectDelegate
    val borderLeft: BorderStyle by ObjectDelegate
    val button: ButtonStyleData by ObjectDelegate
    var buttonStyle: ButtonStyleEnum by ValueDelegate
    var buttonDirection: Direction2DEnum by ValueDelegate
    val progress: ProgressBarData by ObjectDelegate
    var progressStyle: ProgressBarStyle by ValueDelegate
    var progressText: ProgressBarDirection by ValueDelegate
    var progressMinWidth: Int by ValueDelegate
    var progressMinHeight: Int by ValueDelegate

    fun copy(): StyleNode {
        val result = StyleNode()
        result.sheet.putAll(sheet)
        return result
    }

    private fun <T : Any> getValue(key: String): T =
        sheet.getOrElse(key) { DEF_VALUE_MAP[key] } as T

    private fun <T : Any> getObject(key: String): T =
        sheet.getOrPut(key) { (DEF_VALUE_MAP[key] as Function<StyleNode, Any>).apply(this) } as T

    companion object {

        @JvmStatic
        private val DEF_VALUE_MAP =
            Object2ObjectOpenHashMap<String, Any>(64).apply {
                this["width"] = AutoSizeMode(false)
                this["height"] = AutoSizeMode(true)
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

        @JvmStatic
        private val KEY_MAP = Object2ObjectOpenHashMap<String, String>(64).apply {
            DEF_VALUE_MAP.keys.forEach { key ->
                val words = key.split('-')
                val sb = StringBuilder(key.length)
                sb.append(words[0])
                for (i in 1 until words.size) {
                    sb.append(words[i].replaceFirstChar { it.uppercase() })
                }
                put(sb.toString(), key)
            }
        }

    }

    private object ValueDelegate {

        operator fun <T : Any> getValue(thisRef: StyleNode, property: KProperty<*>): T {
            val key = KEY_MAP[property.name]!!
            return thisRef.sheet.getOrElse(key) { DEF_VALUE_MAP[key] } as T
        }

        operator fun <T : Any> setValue(thisRef: StyleNode, property: KProperty<*>, value: T) {
            val key = KEY_MAP[property.name]!!
            thisRef.sheet[key] = value
        }

    }

    private object ObjectDelegate {

        operator fun <T : Any> getValue(thisRef: StyleNode, property: KProperty<*>): T {
            val key = KEY_MAP[property.name]!!
            return thisRef.sheet.getOrPut(key) {
                (DEF_VALUE_MAP[key] as Function<StyleNode, Any>).apply(thisRef)
            } as T
        }

    }

}