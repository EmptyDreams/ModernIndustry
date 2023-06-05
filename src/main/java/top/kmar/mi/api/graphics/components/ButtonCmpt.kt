package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord
import net.minecraft.client.resources.I18n
import net.minecraft.init.SoundEvents
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.modes.ButtonStyleEnum
import top.kmar.mi.api.graphics.utils.modes.OverflowMode
import top.kmar.mi.api.graphics.utils.style.StyleNode
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
    inner class ButtonCmptClient : CmptClient(this) {

        override fun defaultStyle() = StyleNode().apply {
            backgroundColor = IntColor.gray
            border.forEachAll { it.color = IntColor.black }
            overflowX = OverflowMode.HIDDEN
        }

        override fun render(graphics: GuiGraphics) {
            style.buttonStyle.render(this, graphics, mouseOn)
            if (style.buttonStyle.style == ButtonStyleEnum.RECT) renderBorder(graphics)
            val text = attributes["value", ""].run {
                if (startsWith("i18n:", true)) {
                    I18n.format(substring(5))
                } else this
            }
            graphics.drawStringCenter(
                width.floorDiv2(), height.floorDiv2(),
                text, style.color
            )
            renderChildren(graphics)
        }

    }

}