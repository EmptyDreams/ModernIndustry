package top.kmar.mi.api.graphics.utils

import top.kmar.mi.api.utils.data.math.Rect2D
import java.awt.Color

/**
 * 控件样式表
 * @author EmptyDreams
 */
class GraphicsStyle {

    var x: Int = 0
    var y: Int = 0
    var width: Int = 0
    var height: Int = 0
    /** 颜色 */
    var color: Color = Color.BLACK
    /** 背景色 */
    var backgroundColor: Color = Color(0, 0, 0, 0)

    /** 返回控件所占区域 */
    val area: Rect2D
        get() = Rect2D(x, y, width, height)

}