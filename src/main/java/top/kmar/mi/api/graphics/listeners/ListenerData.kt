package top.kmar.mi.api.graphics.listeners

import top.kmar.mi.api.graphics.components.Cmpt

/**
 * GUI事件的父类
 * @author EmptyDreams
 */
open class ListenerData(
    /** 是否可以被阻断 */
    val canCancel: Boolean = true,
    /** 向下传递事件时更新值，传`null`表示禁止向下传递事件，返回`null`表明单次事件不向下传递 */
    val transfer: ((ListenerData, Cmpt) -> ListenerData?)? = null
) {

    /** 触发事件的控件对象 */
    var target: Cmpt = Cmpt.EMPTY_CMPT
        private set

    /** 是否阻断剩余控件触发该事件 */
    var cancel = false
        get() = canCancel && field

}