package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord
import net.minecraft.client.resources.I18n
import net.minecraft.init.SoundEvents
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.utils.modes.ButtonStyleEnum
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.regedits.others.AutoCmpt
import top.kmar.mi.api.utils.expands.applyClient
import top.kmar.mi.api.utils.expands.floorDiv2

/**
 * 按钮控件
 * @author EmptyDreams
 */
@AutoCmpt("button")
class ButtonCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = ButtonCmptClient()
    override fun buildNewObj() = ButtonCmpt(attributes.copy())

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
    inner class ButtonCmptClient : ICmptClient {

        override val service = this@ButtonCmpt
        override val style = GraphicsStyle(service).apply {
            color = IntColor(0, 127, 255, 75)
            backgroundColor = IntColor.gray
            borderTop.color = IntColor.black
            borderRight.color = IntColor.black
            borderBottom.color = IntColor.black
            borderLeft.color = IntColor.black
        }

        override fun render(graphics: GuiGraphics) {
            with(graphics) {
                style.button.render(this, mouseOn)
                if (style.button.style == ButtonStyleEnum.RECT) renderBorder(this)
                val text = attributes["value", ""].run {
                    if (startsWith("i18n:", true)) {
                        I18n.format(substring(5))
                    } else this
                }
                drawStringCenter(
                    style.width.floorDiv2(), style.height.floorDiv2(),
                    text, style.fontColor
                )
                renderChildren(this)
            }
        }

    }

}