package top.kmar.mi.api.graphics.listeners

/**
 * 鼠标滚轮事件
 * @author EmptyDreams
 */
class MouseScrollEventData(
    /** 滚动的距离 */
    val value: Int,
    /** 鼠标X轴相对坐标 */
    val x: Int,
    /** 鼠标Y轴相对坐标 */
    val y: Int,
    /** 鼠标X轴绝对坐标 */
    val clientX: Int,
    /** 鼠标Y轴绝对坐标 */
    val clientY: Int,
    canCancel: Boolean = true,
    reverse: Boolean = false
) : ListenerData(canCancel, reverse) {

    /** 是否为向上滚动 */
    val up = value > 0

    /** 是否为向下滚动 */
    val down = value < 0

}