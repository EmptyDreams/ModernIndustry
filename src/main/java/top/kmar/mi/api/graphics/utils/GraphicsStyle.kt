package top.kmar.mi.api.graphics.utils

import top.kmar.mi.api.utils.data.math.Rect2D
import java.awt.Color

/**
 * 控件样式表
 * @author EmptyDreams
 */
class GraphicsStyle {

    companion object {

        val transparent = Color(0, 0, 0, 0)

    }

    var x: Int = 0
    var y: Int = 0
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

    /** 返回控件所占区域 */
    val area: Rect2D
        get() = Rect2D(x, y, width, height)

    class BorderStyle {

        /** 描边颜色 */
        var color = transparent
        /** 描边粗细 */
        var weight = 1

    }

}