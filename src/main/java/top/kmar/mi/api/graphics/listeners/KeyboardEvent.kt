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
    canCancel: Boolean = true
) : ListenerData(canCancel, true) {

    /** 是否为按键释放 */
    val release = !pressed

}