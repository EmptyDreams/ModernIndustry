package top.kmar.mi.api.graphics.listeners

import top.kmar.mi.api.graphics.components.Cmpt

/**
 * GUI事件的父类
 * @author EmptyDreams
 */
open class ListenerData(
    /** 是否可以被阻断 */
    val canCancel: Boolean = true,
    /** 是否反转事件执行顺序，默认顺序为先执行子控件的事件，后执行父控件的事件 */
    val reverse: Boolean = false
) {

    /** 触发事件的控件对象 */
    var target: Cmpt = Cmpt.EMPTY_CMPT
        set(value) {
            field = if (field == Cmpt.EMPTY_CMPT) value else field
        }

    /** 是否阻断剩余控件触发该事件 */
    var cancel = false
        get() = canCancel && field

}