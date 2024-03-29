package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.BaseGraphicsClient
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.modes.FixedSizeMode
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.regedits.others.AutoCmpt
import top.kmar.mi.api.utils.MISysInfo
import kotlin.math.roundToInt

/**
 * 燃烧进度条
 *
 * 注意：该控件仅支持`14*13(width*height)`尺寸
 *
 * @author EmptyDreams
 */
@AutoCmpt("burn")
class BurnCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = BurnCmptClient()
    override fun buildNewObj() = BurnCmpt(attributes.copy())

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
        get() = if (max == 0) 0.0f else 1 - (value.toFloat() / max)

    private var _progressCache = -1
    private var _maxCache = -1

    override fun networkEvent(player: EntityPlayer) {
        if (value == _progressCache && max == _maxCache) return
        val message = NBTTagCompound().apply {
            setInteger("v", value)
            setInteger("m", max)
        }
        send2Client(player, message)
        _progressCache = value
        _maxCache = max
    }

    @SideOnly(Side.CLIENT)
    inner class BurnCmptClient : CmptClient(this) {

        override fun defaultStyle() = StyleNode().apply {
            width = FixedSizeMode(14)
            height = FixedSizeMode(13)
        }

        override fun receive(message: NBTBase) {
            val nbt = message as NBTTagCompound
            value = nbt.getInteger("v")
            max = nbt.getInteger("m")
        }

        override fun render(graphics: GuiGraphics) {
            if (width != 14 || height != 13)
                return MISysInfo.err("[BurnCmpt] 控件仅支持绘制14*13的尺寸")
            with(graphics) {
                bindTexture(BaseGraphicsClient.textureKey)
                drawTexture64(0, 0, 0, 0, 13, 13)
                val percent = (percent * height).roundToInt()
                if (percent > 0)
                    drawTexture32(0, 13 - percent, 13, 13 - percent, 14, percent)
            }
            super.render(graphics)
        }

    }

}