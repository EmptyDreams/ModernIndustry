package top.kmar.mi.api.graphics.listeners

/**
 * 鼠标点击事件
 * @author EmptyDreams
 */
class MouseEventData(
    /** 鼠标X轴相对坐标 */
    val x: Int,
    /** 鼠标Y轴相对坐标 */
    val y: Int,
    /** 鼠标X轴绝对坐标 */
    val clientX: Int,
    /** 鼠标Y轴绝对坐标 */
    val clientY: Int,
    /** 按键类型 */
    val state: MouseState = MouseState.NONE,
    canCancel: Boolean = true,
    reverse: Boolean = false
) : ListenerData(canCancel, reverse) {

    companion object {

        fun getEventName(mouseButton: Int): MouseState = when (mouseButton) {
            0 -> MouseState.LEFT
            1 -> MouseState.RIGHT
            2 -> MouseState.MIDDLE
            else -> throw IllegalArgumentException("无效的鼠标按键类型：$mouseButton")
        }

    }

}

enum class MouseState {

    /** 左键 */
    LEFT,
    /** 中键 */
    MIDDLE,
    /** 右键 */
    RIGHT,
    /** 没有点击动作 */
    NONE;

    /**
     * 构建一个事件对象
     * @see MouseEventData
     */
    fun build(
        x: Int, y: Int, clientX: Int, clientY: Int,
        canCancel: Boolean = true, reverse: Boolean = false
    ): MouseEventData = MouseEventData(x, y, clientX, clientY, this, canCancel, reverse)

    val clickEventName: String
        get() = when (this) {
            LEFT -> IGraphicsListener.mouseClick
            MIDDLE -> IGraphicsListener.mouseMiddleClick
            RIGHT -> IGraphicsListener.mouseRightClick
            NONE -> throw IllegalArgumentException("没有触发点击事件")
        }

    val releasedEventName = IGraphicsListener.mouseReleased

}