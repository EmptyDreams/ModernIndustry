package top.kmar.mi.api.graphics.listeners

/**
 *
 * @author EmptyDreams
 */
class MouseMoveEventData(
    /** 是否为鼠标进入事件 */
    val isEnter: Boolean,
    canCancel: Boolean = true
) : ListenerData(canCancel, false, true) {

    /** 是否为鼠标退出事件 */
    val isExit = !isEnter

}