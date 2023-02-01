package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.modes.InheritSizeMode
import top.kmar.mi.api.regedits.others.AutoCmpt

/**
 * 文本控件
 * @author EmptyDreams
 */
@AutoCmpt("p")
class TextCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = TextCmptClient()
    override fun buildNewObj() = TextCmpt(attributes.copy())

    @SideOnly(Side.CLIENT)
    inner class TextCmptClient : ICmptClient {

        override val service = this@TextCmpt
        override val style = GraphicsStyle(service).apply {
            val fontRenderer = Minecraft.getMinecraft().fontRenderer
            widthCalculator = InheritSizeMode { fontRenderer.getStringWidth(text) }
            heightCalculator = InheritSizeMode { fontRenderer.FONT_HEIGHT }
        }

        override fun render(graphics: GuiGraphics) {
            graphics.drawString(0, 0, text, style.fontColor)
            super.render(graphics)
        }

        var text: String
            get() = attributes["value"]
            set(value) {
                if (attributes["value"] == value) return
                attributes["value"] = value
                style.markXChange()
            }

    }

}