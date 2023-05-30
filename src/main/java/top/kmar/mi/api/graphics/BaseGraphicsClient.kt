package top.kmar.mi.api.graphics

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.listeners.*
import top.kmar.mi.api.graphics.listeners.IGraphicsListener.Companion.keyboardPressed
import top.kmar.mi.api.graphics.listeners.IGraphicsListener.Companion.keyboardReleased
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.style.StyleSheet
import top.kmar.mi.api.utils.data.math.Point2D
import java.util.*

/**
 * 客户端GUI对象
 *
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
class BaseGraphicsClient(inventorySlots: BaseGraphics) : GuiContainer(inventorySlots) {

    val service = inventorySlots.document
    val client = service.client
    val style = StyleSheet()
    private val taskList = LinkedList<() -> Unit>()

    override fun initGui() {
        xSize = width
        ySize = height
        super.initGui()
    }

    override fun setWorldAndResolution(mc: Minecraft, width: Int, height: Int) {
        if (this.width != width) client.markXLayoutUpdate()
        if (this.height != height) client.markYLayoutUpdate()
        super.setWorldAndResolution(mc, width, height)
    }

    override fun setGuiSize(width: Int, height: Int) {
        if (this.width != width) client.markXLayoutUpdate()
        if (this.height != height) client.markYLayoutUpdate()
        super.setGuiSize(width, height)
    }

    /** 发布鼠标事件 */
    private fun dispatchMouseEvent(mouseX: Int, mouseY: Int, mouseButton: Int, isClick: Boolean) {
        if (width == 0) return
        val state = MouseEventData.getEventName(mouseButton)
        val srcMessage = state.build(mouseX, mouseY, mouseX, mouseY)
        val eventName = if (isClick) state.clickEventName else state.releasedEventName
        client.searchCmpt(mouseX, mouseY).dispatchEvent(eventName, srcMessage)
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
        if (width == 0) return
        val key = Keyboard.getEventKey()
        // 判断是按键按下还是释放
        if (Keyboard.getEventKeyState()) {
            service.dispatchEvent(keyboardPressed, KeyboardEvent(key, true))
        } else {
            service.dispatchEvent(keyboardReleased, KeyboardEvent(key, false))
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        taskList.forEach { it() }
        taskList.clear()
        val graphics = GuiGraphics(0, 0, width, height, this)
        client.renderChildren(graphics)
        val inventorySlots = this.inventorySlots as BaseGraphics
        GuiLoader.invokeClientLoopTask(inventorySlots)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (width == 0) return
        super.drawScreen(mouseX, mouseY, partialTicks)
        activeMouseMoveEvent(mouseX, mouseY)
        activeMouseScrollEvent(mouseX, mouseY)
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
                val message = MouseMoveEventData(isEnter)
                it.dispatchEvent(eventName, message)
                if (message.cancel) {
                    if (isEnter) enter = false
                    else exit = false
                }
            }
            return null == cmpt.eachChildren {
                if (Point2D(mouseX, mouseY) in it.client) {
                    if (it.id !in mouseEnterList) {
                        mouseEnterList.add(it.id)
                        if (enter) helper(it, true)
                    }
                } else {
                    if (it.id in mouseEnterList) {
                        mouseEnterList.remove(it.id)
                        if (exit) helper(it, false)
                    }
                }
                task(it, x, y)
                if (!(enter || exit)) it else null
            }
        }
        task(service, mouseX, mouseY)
    }

    /** 触发鼠标滚轮事件 */
    private fun activeMouseScrollEvent(mouseX: Int, mouseY: Int) {
        val scroll = Mouse.getDWheel()
        if (scroll == 0) return
        service.dispatchEvent(IGraphicsListener.mouseScroll,
            MouseScrollEventData(scroll, mouseX, mouseY, mouseX, mouseY))
    }

    /** 添加一个初始化任务 */
    fun addInitTask(task: () -> Unit) {
        taskList.add(task)
    }

}