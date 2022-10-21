package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.utils.*
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.floorDiv2

/**
 * 标题控件
 * @author EmptyDreams
 */
@AutoCmpt("title")
class TittleCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = TittleCmptClient()

    override fun buildNewObj() = TittleCmpt(attributes.copy())

    @SideOnly(Side.CLIENT)
    inner class TittleCmptClient : ICmptClient {

        override val service = this@TittleCmpt
        override val style = GraphicsStyle(service).apply {
            widthCalculator = InheritSizeMode { it.width }
            heightCalculator = FixedSizeMode(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT)
            marginTop = 8
            position = PositionEnum.ABSOLUTE
        }

        override fun render(graphics: GuiGraphics) {
            val text = attributes["value"].run {
                if (startsWith("i18n:", true)) I18n.format(substring(5))
                else this
            }
            graphics.drawStringCenter(
                style.width.floorDiv2(), style.height.floorDiv2(),
                text, style.fontColor
            )
            super.render(graphics)
        }

    }

}