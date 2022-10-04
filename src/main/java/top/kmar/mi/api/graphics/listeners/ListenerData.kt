package top.kmar.mi.api.graphics.listeners

import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient

/**
 * GUI事件的父类
 * @author EmptyDreams
 */
open class ListenerData(
    /** 是否可以被阻断 */
    val canCancel: Boolean = true,
    /** 是否反转事件执行顺序，默认顺序为先执行子控件的事件，后执行父控件的事件 */
    val reverse: Boolean = false,
    /** 禁止事件向父级控件传递 */
    val prohibitTransfer: Boolean = false
) {

    /** 触发事件的控件对象 */
    var target: Cmpt = Cmpt.EMPTY_CMPT
        set(value) {
            field = if (field == Cmpt.EMPTY_CMPT) value else field
        }

    /**
     * 是否将事件发送到服务端
     *
     * 使用该功能时，服务端所有同名事件都将被触发，同时服务端触发时不会进行事件传播
     *
     * 如果只想触发单个事件或者希望进行事件传播需要手动调用[CmptClient.send2Service]函数（第二个参数要填`false`）
     */
    var send2Service = false

    /** 是否阻断剩余控件触发该事件 */
    var cancel = false
        get() = canCancel && field

}