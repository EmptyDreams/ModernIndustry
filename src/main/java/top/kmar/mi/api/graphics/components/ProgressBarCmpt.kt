package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.data.enums.VerticalDirectionEnum
import top.kmar.mi.api.utils.floorDiv2

/**
 * 进度条控件
 * @author EmptyDreams
 */
@AutoCmpt("progress")
class ProgressBarCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = ProgressBarCmptClient()
    override fun buildNewObj() = ProgressBarCmpt(attributes.copy())

    var value: Int
        get() = attributes["value", "0"].toInt()
        set(value) {
            val new = value.coerceAtLeast(0).coerceAtMost(max)
            attributes["value"] = new.toString()
        }
    var max: Int
        get() = attributes["max", "0"].toInt()
        set(value) {
            val new = value.coerceAtLeast(0)
            attributes["max"] = new.toString()
        }
    val percent: Float
        get() = value.toFloat() / max.coerceAtLeast(1)

    private var _preProgress = -1
    private var _preMax = -1

    override fun networkEvent(player: EntityPlayer) {
        if (_preProgress != value || _preMax != max) {
            val message = ByteDataOperator(5).apply {
                writeVarInt(value)
                writeVarInt(max)
            }
            send2Client(player, message)
            _preProgress = value
            _preMax = max
        }
    }

    @SideOnly(Side.CLIENT)
    inner class ProgressBarCmptClient : ICmptClient {

        override val service = this@ProgressBarCmpt
        override val style = GraphicsStyle(service).apply {
            backgroundColor = IntColor.gray
            color = IntColor.white
            borderBottom.color = IntColor(104, 104, 104)
            borderRight.color = borderBottom.color
        }

        override fun receive(message: IDataReader) {
            value = message.readVarInt()
            max = message.readVarInt()
        }

        override fun render(graphics: GuiGraphics) {
            with(style) {
                progress.render(graphics, percent)
                if (progress.showText) {
                    val textY = when (progress.textLocation) {
                        VerticalDirectionEnum.UP -> - graphics.fontRenderer.FONT_HEIGHT - 1
                        VerticalDirectionEnum.DOWN -> height + graphics.fontRenderer.FONT_HEIGHT.floorDiv2()
                        VerticalDirectionEnum.CENTER -> height.floorDiv2()
                    }
                    val textX = width.floorDiv2()
                    graphics.drawStringCenter(textX, textY, "${service.value} / $max", fontColor)
                }
            }
            renderChildren(graphics)
        }

    }

}