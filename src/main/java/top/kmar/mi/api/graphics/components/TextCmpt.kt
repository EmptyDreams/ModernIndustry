package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.modes.CodeSizeMode
import top.kmar.mi.api.graphics.utils.style.StyleNode
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
    inner class TextCmptClient : CmptClient(this) {

        override fun defaultStyle() = StyleNode().apply {
            val fontRenderer = Minecraft.getMinecraft().fontRenderer
            width = CodeSizeMode { fontRenderer.getStringWidth(text) }
            height = CodeSizeMode { fontRenderer.FONT_HEIGHT }
        }

        override fun render(graphics: GuiGraphics) {
            graphics.drawString(0, 0, text, style.color)
            super.render(graphics)
        }

        var text: String
            get() = attributes["value"]
            set(value) {
                if (attributes["value"] == value) return
                attributes["value"] = value
                markXLayoutUpdate()
            }

    }

}