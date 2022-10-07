package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord
import net.minecraft.init.SoundEvents
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.utils.ButtonStyleEnum
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.applyClient
import java.awt.Color

/**
 * 按钮控件
 * @author EmptyDreams
 */
@AutoCmpt("button")
class ButtonCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = ButtonCmptClient()

    /** 鼠标是否在按钮上方 */
    var mouseOn = false
        private set

    init {
        addEventListener(IGraphicsListener.mouseExit) { mouseOn = false }
        addEventListener(IGraphicsListener.mouseEnter) {
            mouseOn = true
        }
        addEventListener(IGraphicsListener.mouseClick) {
            applyClient {
                val soundHandlerIn = Minecraft.getMinecraft().soundHandler
                soundHandlerIn.playSound(getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            }
        }
    }

    @SideOnly(Side.CLIENT)
    inner class ButtonCmptClient : CmptClient {

        override val service = this@ButtonCmpt
        override val style = GraphicsStyle(service).apply {
            color = Color(0, 127, 255, 75)
            backgroundColor = Color(139, 139, 139)
            borderTop.color = Color.BLACK
            borderRight.color = Color.BLACK
            borderBottom.color = Color.BLACK
            borderLeft.color = Color.BLACK
        }

        override fun render(graphics: GuiGraphics) {
            style.button.render(graphics, mouseOn)
            if (style.button.style == ButtonStyleEnum.RECT) renderBorder(graphics)
            renderChildren(graphics)
        }

    }

}