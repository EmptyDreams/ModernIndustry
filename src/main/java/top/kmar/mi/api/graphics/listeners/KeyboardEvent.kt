package top.kmar.mi.api.graphics.listeners

import org.lwjgl.input.Keyboard

/**
 * 键盘事件
 * @author EmptyDreams
 */
class KeyboardEvent(
    /**
     * 按键
     * @see Keyboard
     */
    key: Int,
    /** 是否为按键按下 */
    pressed: Boolean,
    canCancel: Boolean = true,
    reverse: Boolean = false
) : ListenerData(canCancel, reverse) {

    /** 是否为按键释放 */
    val release = !pressed

}