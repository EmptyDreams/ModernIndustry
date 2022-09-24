package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.toInt
import java.awt.Color

/**
 * 蒙版
 * @author EmptyDreams
 */
@AutoCmpt("mask")
class MaskGraphics(override val id: String) : Cmpt() {

    override fun initClientObj() = MaskClient()

    inner class MaskClient : CmptClient {

        override val style = GraphicsStyle().apply {
            backgroundColor = Color(0, 0, 0, 120)
        }
        override val service = this@MaskGraphics

        override fun render(graphics: GuiGraphics) {
            graphics.overflowHidden = false
            val container = graphics.container
            val mc = Minecraft.getMinecraft()
            graphics.fillRect(
                -container.guiLeft, -container.guiTop,
                mc.displayWidth, mc.displayHeight,
                style.backgroundColor.toInt()
            )
        }

    }

}