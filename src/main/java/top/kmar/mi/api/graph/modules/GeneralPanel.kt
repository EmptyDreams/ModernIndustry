package top.kmar.mi.api.graph.modules

import top.kmar.mi.api.graph.listeners.MouseData
import top.kmar.mi.api.graph.listeners.mouse.IMouseActionListener
import top.kmar.mi.api.graph.utils.GeneralPanel

/**
 * 不可见的按钮
 * @author EmptyDreams
 */
open class GeneralPanel(
    /** 鼠标点击时触发 */
    private var action: (Float, Float) -> Unit
) : GeneralPanel() {

    init {
        @Suppress("LeakingThis")
        registryListener(IMouseActionListener { x, y ->
            action(x, y)
            MouseData.EMPTY_DATA
        })
    }

    fun setAction(action: (Float, Float) -> Unit) {
        this.action = action
    }

}