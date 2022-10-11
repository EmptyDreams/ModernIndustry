package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.ModernIndustry
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import top.kmar.mi.api.utils.MISysInfo
import kotlin.math.roundToInt

/**
 * 燃烧进度条
 *
 * 注意：该控件仅支持14*13(width*height)尺寸
 *
 * @author EmptyDreams
 */
@AutoCmpt("burn")
class BurnCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = BurnCmptClient()

    var progress = 0
    var maxProcess = 1

    private var _progressCache = -1
    private var _maxCache = -1

    override fun networkEvent(player: EntityPlayer) {
        if (progress == _progressCache && maxProcess == _maxCache) return
        val message = ByteDataOperator(4).apply {
            writeVarInt(progress)
            writeVarInt(maxProcess)
        }
        send2Client(player, message)
    }

    @SideOnly(Side.CLIENT)
    inner class BurnCmptClient : CmptClient {

        override val service = this@BurnCmpt
        override val style = GraphicsStyle(service).apply {
            width = FixedSizeMode(14)
            height = FixedSizeMode(13)
        }

        override fun receive(message: IDataReader) {
            progress = message.readVarInt()
            maxProcess = message.readVarInt()
        }

        override fun render(graphics: GuiGraphics) {
            with(graphics) {
                if (width != 14 || height != 13) MISysInfo.err("[BurnCmpt] 控件仅支持绘制14*13的尺寸")
                bindTexture(textureLib)
                drawTexture(0, 0, 0, 0, 13, 13)
                val percent = (progress.toFloat() / maxProcess * height).roundToInt()
                if (percent > 0)
                    drawTexture(0, 13 - percent, 13, 0, 14, 13)
            }
            super.render(graphics)
        }

    }

    companion object {

        @SideOnly(Side.CLIENT)
        @JvmStatic
        val textureLib = ResourceLocation(ModernIndustry.MODID, "textures/gui/graph.png")

    }

}