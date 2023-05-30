package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.*
import top.kmar.mi.api.graphics.utils.modes.FixedSizeMode
import top.kmar.mi.api.graphics.utils.modes.InheritSizeMode
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.regedits.others.AutoCmpt
import top.kmar.mi.api.utils.expands.floorDiv2

/**
 * 标题控件
 * @author EmptyDreams
 */
@AutoCmpt("title")
class TittleCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = TittleCmptClient()

    override fun buildNewObj() = TittleCmpt(attributes.copy())

    @SideOnly(Side.CLIENT)
    inner class TittleCmptClient : CmptClient(this) {

        override fun defaultStyle() = StyleNode().apply {
            width = InheritSizeMode { it.width }
            height = FixedSizeMode(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT)
            marginTop = 8
            marginBottom = 2
        }

        override fun render(graphics: GuiGraphics) {
            val text = attributes["value"].run {
                if (startsWith("i18n:", true)) I18n.format(substring(5))
                else this
            }
            graphics.drawStringCenter(
                width.floorDiv2(), height.floorDiv2(),
                text, style.color
            )
            super.render(graphics)
        }

    }

}