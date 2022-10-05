package top.kmar.mi.api.graphics.components

import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord
import net.minecraft.init.SoundEvents
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.listeners.IGraphicsListener
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.applyClient
import top.kmar.mi.api.utils.toInt
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

    companion object {

        @SideOnly(Side.CLIENT)
        @JvmStatic
        private val textureLib = ResourceLocation("textures/gui/widgets.png")

    }

    @SideOnly(Side.CLIENT)
    inner class ButtonCmptClient : CmptClient {

        override val service = this@ButtonCmpt
        override val style = GraphicsStyle(service).apply {
            color = Color(0, 127, 255, 75)
            borderTop.color = Color.BLACK
            borderRight.color = Color.BLACK
            borderBottom.color = Color.BLACK
            borderLeft.color = Color.BLACK
        }

        override fun render(graphics: GuiGraphics) {
            val rectSize = 15
            with(graphics) {
                bindTexture(textureLib)
                // 中央区域坐标
                val startX = style.borderTop.weight
                val startY = style.borderLeft.weight
                val endX = style.width() - style.borderRight.weight
                val endY = style.height() - style.borderBottom.weight
                // 绘制中央材质
                for (y in startX until endY step rectSize) {
                    for (x in startY until endX step rectSize) {
                        drawTexture(x, y, 2, 68, rectSize, rectSize)
                    }
                }
                // 中央区域尺寸
                val centerWidth = endX - startX
                val centerHeight = endY - startX
                val lightColor = Color(255, 255, 255, 60).toInt()
                val darkColor = Color(0, 0, 0, 60).toInt()
                // 绘制左上角高亮
                fillRect(startX, startY, centerWidth, 1, lightColor)
                fillRect(startX, startY + 1, 1, centerHeight - 1, lightColor)
                // 绘制右下角阴影
                fillRect(startX, endY - 2, centerWidth, 2, darkColor)
                fillRect(endX - 1, startY, 1, centerHeight - 2, darkColor)
                // 如果被鼠标覆盖则绘制覆盖图层
                if (mouseOn) fillRect(startX, startY, centerWidth, centerHeight, style.color.toInt())

                renderBorder(this)
                renderChildren(this)
            }
        }

    }

}