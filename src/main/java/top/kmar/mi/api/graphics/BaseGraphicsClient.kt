package top.kmar.mi.api.graphics

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import net.minecraft.client.gui.inventory.GuiContainer
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import top.kmar.mi.api.graphics.components.Cmpt
import top.kmar.mi.api.graphics.components.CmptClient
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.listeners.IGraphicsListener.Companion.keyboardPressed
import top.kmar.mi.api.graphics.listeners.IGraphicsListener.Companion.keyboardReleased
import top.kmar.mi.api.graphics.listeners.KeyboardEvent
import top.kmar.mi.api.graphics.listeners.MouseEventData
import top.kmar.mi.api.graphics.listeners.MouseScrollEventData
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.utils.data.math.Point2D

/**
 *
 * @author EmptyDreams
 */
class BaseGraphicsClient(inventorySlots: BaseGraphics) : GuiContainer(inventorySlots), CmptClient {

    /** 发布鼠标事件 */
    private fun dispatchMouseEvent(mouseX: Int, mouseY: Int, mouseButton: Int, isClick: Boolean) {
        val state = MouseEventData.getEventName(mouseButton)
        val srcMessage = state.build(mouseX, mouseY, mouseX, mouseY)
        val eventName = if (isClick) state.clickEventName else state.releasedEventName
        service.dispatchEvent(eventName, srcMessage)
    }

    /** 鼠标按下时触发 */
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        dispatchMouseEvent(mouseX, mouseY, mouseButton, true)
    }

    /** 鼠标释放时触发 */
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        dispatchMouseEvent(mouseX, mouseY, state, false)
    }

    /** 键盘监听 */
    override fun handleKeyboardInput() {
        super.handleKeyboardInput()
        val key = Keyboard.getEventKey()
        // 判断是按键按下还是释放
        if (Keyboard.getEventKeyState()) {
            service.dispatchEvent(keyboardPressed, KeyboardEvent(key, true))
        } else {
            service.dispatchEvent(keyboardReleased, KeyboardEvent(key, false))
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {

    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        activeMouseMoveEvent(mouseX, mouseY)
        activeMouseScrollEvent(mouseX, mouseY)
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }

    /** 存储鼠标覆盖的名单 */
    private val mouseEnterList = ObjectRBTreeSet<String>()

    /** 鼠标移动事件 */
    private fun activeMouseMoveEvent(mouseX: Int, mouseY: Int) {
        var enter = true
        var exit = true
        // 触发鼠标移动事件
        fun task(cmpt: Cmpt, x: Int, y: Int): Boolean {
            fun helper(it: Cmpt, isEnter: Boolean) {
                val eventName = if (isEnter) IGraphicsListener.mouseEnter else IGraphicsListener.mouseExit
                val message = MouseEventData(mouseX - style.x, mouseY - style.y, mouseX, mouseY)
                it.dispatchEvent(eventName, message)
                if (message.cancel) {
                    if (isEnter) enter = false
                    else exit = false
                } else task(it, x - style.x, y - style.y)
            }
            return null == cmpt.eachChildren {
                val style = it.client.style
                if (Point2D(mouseX, mouseY) in style.area) {
                    if (it.id in mouseEnterList) return@eachChildren null
                    mouseEnterList.add(it.id)
                    helper(it, true)
                } else {
                    if (it.id !in mouseEnterList) return@eachChildren null
                    mouseEnterList.remove(it.id)
                    if (exit) {
                        val message = MouseEventData(mouseX - style.x, mouseY - style.y, mouseX, mouseY)
                        it.dispatchEvent(IGraphicsListener.mouseExit, message)
                        if (message.cancel) exit = false
                        else task(it, x - style.x, y - style.y)
                    }
                }
                if (!(enter || exit)) it else null
            }
        }
        task(service, mouseX, mouseY)
    }

    private fun activeMouseScrollEvent(mouseX: Int, mouseY: Int) {
        val scroll = Mouse.getDWheel()
        if (scroll == 0) return
        service.dispatchEvent(IGraphicsListener.mouseScroll,
            MouseScrollEventData(scroll, mouseX, mouseY, mouseX, mouseY))
    }

    override val service = inventorySlots.document
    override val style = GraphicsStyle()

    override fun render(graphics: GuiGraphics) {
        TODO("Not yet implemented")
    }

}