@file:Suppress("DuplicatedCode")

package top.kmar.mi.api.graphics.utils

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.utils.WorldUtil
import top.kmar.mi.api.utils.data.math.Rect2D
import java.awt.Color

/**
 * 控件样式表
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class GraphicsStyle(
    private val cmpt: Cmpt
) {

    companion object {

        /** 透明色 */
        val transparent = Color(0, 0, 0, 0)

    }

    var width: Int = 0
    var height: Int = 0

    /** 颜色 */
    var color: Color = Color.BLACK
    /** 背景色 */
    var backgroundColor = transparent

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
        set(value) {
            field = value
            posChange = true
        }
    /** 右边距 */
    var marginRight = 0
        set(value) {
            field = value
            posChange = true
        }
    /** 下编剧 */
    var marginBottom = 0
        set(value) {
            field = value
            posChange = true
        }
    /** 左边距 */
    var marginLeft = 0
        set(value) {
            field = value
            posChange = true
        }

    /** 水平对齐方式 */
    var alignHorizontal = HorizontalAlignModeEnum.MIDDLE
        set(value) {
            field = value
            posChange = true
        }
    /** 垂直对齐方式 */
    var alignVertical = VerticalAlignModeEnum.MIDDLE
        set(value) {
            field = value
            posChange = true
        }

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
        set(value) {
            field = value
            posChange = true
        }
    /** 原始Y坐标 */
    private var srcY: Int = 0
        set(value) {
            field = value
            posChange = true
        }
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var top = 0
        set(value) {
            field = value
            posChange = true
        }
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var right = 0
        set(value) {
            field = value
            posChange = true
        }
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var bottom = 0
        set(value) {
            field = value
            posChange = true
        }
    /** 优先级：`top` > `right` > `bottom` > `left` */
    var left = 0
        set(value) {
            field = value
            posChange = true
        }

    /** 控件X坐标，相对于窗体 */
    val x: Int
        get() {
            alignChildren()
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
            alignChildren()
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

    private val parentStyle: GraphicsStyle
        get() = cmpt.parent.client.style

    /** 返回控件所占区域 */
    val area: Rect2D
        get() = Rect2D(x, y, width, height)

    var posChange = true

    fun alignChildren() {
        if (!posChange) return
        posChange = false
        alignHorizontal(cmpt.client) { it, x -> it.style.srcX = x }
        alignVertical(cmpt.client) {it, y -> it.style.srcY = y}
    }

}