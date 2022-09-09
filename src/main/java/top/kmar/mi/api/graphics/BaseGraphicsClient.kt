package top.kmar.mi.api.graphics

import net.minecraft.client.gui.inventory.GuiContainer
import org.lwjgl.input.Keyboard
import top.kmar.mi.api.graphics.components.Cmpt
import top.kmar.mi.api.graphics.components.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import java.util.*

/**
 *
 * @author EmptyDreams
 */
class BaseGraphicsClient(inventorySlots: BaseGraphics) : GuiContainer(inventorySlots), CmptClient {

    /** 控件列表 */
    private val cmptList = LinkedList<Cmpt>()
    /** 注册在GUI上的事件 */

    /** 鼠标按下时触发 */
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    /** 鼠标释放时触发 */
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
    }

    /** 鼠标移动时触发 */
    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    /** 键盘监听 */
    override fun handleKeyboardInput() {
        super.handleKeyboardInput()
        val key = Keyboard.getEventKey()
        // 判断是按键按下还是释放
        if (Keyboard.getEventKeyState()) {
            // 按下
        } else {
            // 释放
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {

    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }

    override val service = inventorySlots.document
    override val style = GraphicsStyle()

    override fun render(graphics: GuiGraphics) {
        TODO("Not yet implemented")
    }

}