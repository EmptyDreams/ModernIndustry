@file:Suppress("DuplicatedCode")

package top.kmar.mi.api.graphics.utils

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.container.CacheContainer
import top.kmar.mi.api.utils.data.math.Rect2D
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 控件样式表
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
open class GraphicsStyle(
    private val cmpt: Cmpt
) {

    var width: Int = -1
        get() {
            if (field != -1) return field
            val parent: Cmpt
            if (widthCalculator.relyOnParent) {
                var dist = cmpt.parent
                while (dist.client.style.widthCalculator.relyOnChild) {
                    dist = dist.parent
                }
                parent = dist
            } else parent = cmpt.parent
            field = widthCalculator(parent.client.style)
            return field
        }
        private set
    var height: Int = -1
        get() {
            if (field != -1) return field
            val parent: Cmpt
            if (heightCalculator.relyOnParent) {
                var dist = cmpt
                while (dist.client.style.heightCalculator.relyOnChild) {
                    dist = dist.parent
                }
                parent = dist
            } else parent = cmpt.parent
            field = heightCalculator(parent.client.style)
            return field
        }
        private set
    /** 子控件的宽度 */
    var childrenWidth: Int = -1
        get() {
            if (field != -1) return field
            field = groupCache().stream()
                .mapToInt { it.stream().mapToInt { style -> style.spaceWidth }.sum() }
                .max()
                .orElse(0)
            return field
        }
        private set
    /** 子控件高度 */
    var childrenHeight: Int = -1
        get() {
            if (field != -1) return field
            field = groupCache().stream()
                .mapToInt { it.stream().mapToInt { style -> style.spaceHeight }.max().orElse(0) }
                .sum()
            return field
        }
        private set

    var widthCalculator: ISizeMode = AutoSizeMode(cmpt, false)
    var heightCalculator: ISizeMode = AutoSizeMode(cmpt, true)

    /** 颜色 */
    var color = IntColor.black
    /** 背景色 */
    var backgroundColor = IntColor.transparent
    /** 文本颜色 */
    var fontColor = IntColor.black

    /** 上描边 */
    val borderTop = BorderStyle()
    /** 右描边 */
    val borderRight = BorderStyle()
    /** 底描边 */
    val borderBottom = BorderStyle()
    /** 左描边 */
    val borderLeft = BorderStyle()

    /** 上边距 */
    var marginTop = 0
    /** 右边距 */
    var marginRight = 0
    /** 下编剧 */
    var marginBottom = 0
    /** 左边距 */
    var marginLeft = 0

    /** 水平对齐方式 */
    var alignHorizontal = HorizontalAlignModeEnum.MIDDLE
    /** 垂直对齐方式 */
    var alignVertical = VerticalAlignModeEnum.MIDDLE

    /** 控件占用空间宽度 */
    val spaceWidth: Int
        get() = width + marginLeft + marginRight
    /** 控件占用控件高度 */
    val spaceHeight: Int
        get() = height + marginTop + marginBottom

    /** 定位方法 */
    var position = PositionEnum.RELATIVE
    /** 原始X坐标 */
    private var srcX: Int = 0
    /** 原始Y坐标 */
    private var srcY: Int = 0
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var top = 0
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var right = 0
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var bottom = 0
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var left = 0

    /** 显示方式 */
    var display = DisplayModeEnum.DEF
    /** 是否隐藏超出边界的部分 */
    var overflowHidden = false

    /** 进度条样式 */
    val progress by lazy(NONE) { ProgressBarData(this) }
    /** 按钮样式 */
    val button by lazy(NONE) { ButtonStyleData(this) }

    /** 控件X坐标，相对于窗体 */
    val x: Int
        get() {
            return when (position) {
                PositionEnum.RELATIVE ->
                    if (left != 0) srcX + left
                    else srcX - right
                PositionEnum.ABSOLUTE ->
                    if (left != 0) parentStyle.left + left
                    else parentStyle.endX - width - right
                PositionEnum.FIXED ->
                    if (left != 0) left
                    else (WorldUtil.getClientPlayer().openContainer as BaseGraphics).client.width - width - right
            }
        }
    /** 控件Y坐标，相对于窗体 */
    val y: Int
        get() {
            return when (position) {
                PositionEnum.RELATIVE ->
                    if (top != 0) srcY + top
                    else srcY - bottom
                PositionEnum.ABSOLUTE ->
                    if (top != 0) parentStyle.top + top
                    else parentStyle.endY - height - bottom
                PositionEnum.FIXED ->
                    if (top != 0) top
                    else (WorldUtil.getClientPlayer().openContainer as BaseGraphics).client.height - height - right
            }
        }
    val endX: Int
        get() = x + width
    val endY: Int
        get() = y + height

    val parentStyle: GraphicsStyle
        get() = cmpt.parent.client.style

    /** 返回控件所占区域 */
    val area: Rect2D
        get() = Rect2D(x, y, width, height)

    private var xPosChange = true
    private var yPosChange = true
    // Y轴分组数据
    private val groupCache = CacheContainer {
        LinkedList<LinkedList<GraphicsStyle>>().apply {
            var prev = DisplayModeEnum.NONE
            cmpt.eachAllChildren {
                with(it.client.style) {
                    if (!display.isDisplay()) return@eachAllChildren
                    if (!position.isRelative()) {
                        markYChange()
                        return@eachAllChildren
                    }
                    if (display == prev && display.isInline()) last.addLast(this)
                    else {
                        val newList = LinkedList<GraphicsStyle>()
                        newList.addLast(this)
                        addLast(newList)
                        prev = display
                    }
                }
            }
        }
    }

    /** 标记X轴方向坐标变化 */
    fun markXChange() {
        xPosChange = true
        width = -1
        childrenWidth = -1
    }

    /** 标记Y轴方向坐标变化 */
    fun markYChange() {
        yPosChange = true
        groupCache.clear()
        height = -1
        childrenHeight = -1
    }

    /** 标记X及Y轴方向坐标变化 */
    fun markPosChange() {
        markXChange()
        markYChange()
    }

    /** 排列子控件 */
    fun alignChildren() {
        val groupList = groupCache()
        if (yPosChange) {
            yPosChange = false
            alignVertical(this@GraphicsStyle, groupList) { it, y -> it.srcY = y + it.marginTop }
        }
        if (xPosChange) {
            xPosChange = false
            for (group in groupList) {
                alignHorizontal(this, group) { it, x -> it.srcX = x + it.marginLeft }
            }
            cmpt.childrenStream()
                .map { it.client.style }
                .filter { it.display.isDisplay() }
                .forEach { it.markXChange() }
        }
        for (it in cmpt.childrenIterator()) {
            it.client.style.alignChildren()
        }
    }

}