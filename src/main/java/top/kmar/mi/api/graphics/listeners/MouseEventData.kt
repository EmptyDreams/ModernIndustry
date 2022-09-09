package top.kmar.mi.api.graphics.listeners

/**
 * 鼠标点击事件
 * @author EmptyDreams
 */
class MouseEventData(
    /** 鼠标X轴相对坐标 */
    val x: Float,
    /** 鼠标Y轴相对坐标 */
    val y: Float,
    /** 鼠标X轴绝对坐标 */
    val clientX: Float,
    /** 鼠标Y轴绝对坐标 */
    val clientY: Float,
    canCancel: Boolean = true
) : ListenerData(canCancel)