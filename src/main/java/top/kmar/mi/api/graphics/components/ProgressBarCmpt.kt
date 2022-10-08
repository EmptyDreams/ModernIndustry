package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
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

    /** 当前进度 */
    var progress: Int = 0
    /** 最大进度 */
    var maxProgress: Int = 1
    /** 百分比 */
    val percent: Float
        get() = progress.toFloat() / maxProgress

    override fun initClientObj() = ProgressBarCmptClient()

    private var _preProgress = -1
    private var _preMax = -1

    override fun networkEvent(player: EntityPlayer) {
        if (_preProgress != progress || _preMax != maxProgress) {
            val message = ByteDataOperator(5).apply {
                writeVarInt(progress)
                writeVarInt(maxProgress)
            }
            send2Client(player, message)
            _preProgress = progress
            _preMax = maxProgress
        }
    }

    @SideOnly(Side.CLIENT)
    inner class ProgressBarCmptClient : CmptClient {

        override val service = this@ProgressBarCmpt
        override val style = GraphicsStyle(service).apply {
            backgroundColor = IntColor.gray
            color = IntColor.white
            borderBottom.color = IntColor(104, 104, 104)
            borderRight.color = borderBottom.color
        }

        override fun receive(message: IDataReader) {
            progress = message.readVarInt()
            maxProgress = message.readVarInt()
        }

        override fun render(graphics: GuiGraphics) {
            with(style) {
                progress.render(graphics, percent)
                if (progress.showText) {
                    val textY = when (progress.textLocation) {
                        VerticalDirectionEnum.UP -> y - graphics.fontRenderer.FONT_HEIGHT - 1
                        VerticalDirectionEnum.DOWN -> endY + 1
                        VerticalDirectionEnum.CENTER -> y + (graphics.height.floorDiv2())
                    }
                    val textX = x + (graphics.width.floorDiv2())
                    graphics.drawStringCenter(textX, textY, "$progress / $maxProgress", fontColor)
                }
            }
            renderChildren(graphics)
        }

    }

}