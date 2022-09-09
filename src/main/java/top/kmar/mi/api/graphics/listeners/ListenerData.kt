package top.kmar.mi.api.graphics.listeners

import top.kmar.mi.api.graphics.components.Cmpt

/**
 * GUI事件的父类
 * @author EmptyDreams
 */
open class ListenerData(
    /** 是否可以被阻断 */
    val canCancel: Boolean = true,
    /** 是否向下传递事件 */
    val transfer: Boolean = false
) {

    /** 触发事件的控件对象 */
    var target: Cmpt = Cmpt.EMPTY_CMPT
        private set

    /** 是否阻断剩余控件触发该事件 */
    var cancel = false
        get() = canCancel && field

}