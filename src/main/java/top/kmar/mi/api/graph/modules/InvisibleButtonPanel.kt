package top.kmar.mi.api.graph.modules

import top.kmar.mi.api.graph.listeners.MouseData
import top.kmar.mi.api.graph.listeners.mouse.IMouseActionListener
import top.kmar.mi.api.graph.utils.GeneralPanelClient
import top.kmar.mi.api.graph.utils.GuiPainter

/**
 * 不可视按钮的客户端实现
 * @author EmptyDreams
 */
open class InvisibleButtonPanelClient(
    x: Int, y: Int, width: Int, height: Int,
    private var action: (Float, Float) -> Unit
) : GeneralPanelClient(x, y, width, height) {

    init {
        @Suppress("LeakingThis")
        registryListener(IMouseActionListener { mouseX, mouseY ->
            action(mouseX, mouseY)
            MouseData(mouseX, mouseY, Int.MIN_VALUE, Int.MIN_VALUE, true)
        })
    }

    fun setAction(action: (Float, Float) -> Unit) {
        this.action = action
    }

    override fun paint(painter: GuiPainter) { }

}