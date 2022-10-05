package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import java.awt.Color

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
            backgroundColor = Color(139, 139, 139)
            color = Color.WHITE
            borderBottom.color = Color(104, 104, 104)
            borderRight.color = borderBottom.color
        }

        override fun receive(message: IDataReader) {
            progress = message.readVarInt()
            maxProgress = message.readVarInt()
        }

        override fun render(graphics: GuiGraphics) {
            style.progress.style.render(graphics, style, percent)
            renderChildren(graphics)
        }

    }

}