package top.kmar.mi.api.graphics.components

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.graphics.utils.modes.ProgressBarTextEnum
import top.kmar.mi.api.graphics.utils.style.StyleNode
import top.kmar.mi.api.regedits.others.AutoCmpt
import top.kmar.mi.api.utils.expands.floorDiv2

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
            val message = NBTTagCompound().apply {
                setInteger("v", value)
                setInteger("m", max)
            }
            send2Client(player, message)
            _preProgress = value
            _preMax = max
        }
    }

    @SideOnly(Side.CLIENT)
    inner class ProgressBarCmptClient : CmptClient(this) {

        override fun defaultStyle() = StyleNode().apply {
            backgroundColor = IntColor.gray
            color = IntColor.white
            val borderColor = IntColor(104, 104, 104)
            borderBottom.color = borderColor
            borderRight.color = borderColor
        }

        override fun receive(message: NBTBase) {
            val nbt = message as NBTTagCompound
            max = nbt.getInteger("m")
            value = nbt.getInteger("v")
        }

        override fun render(graphics: GuiGraphics) {
            val progress = style.progressStyle
            progress.render(this, graphics, percent)
            if (progress.showText) {
                val textY = when (progress.text) {
                    ProgressBarTextEnum.HEAD -> - graphics.fontRenderer.FONT_HEIGHT - 1
                    ProgressBarTextEnum.TAIL -> height + graphics.fontRenderer.FONT_HEIGHT.floorDiv2()
                    ProgressBarTextEnum.MIDDLE -> height.floorDiv2()
                    else -> throw AssertionError()
                }
                val textX = width.floorDiv2()
                val bar = service as ProgressBarCmpt
                graphics.drawStringCenter(textX, textY, "${bar.value} / $max", style.progressTextColor)
            }
            renderChildren(graphics)
        }

    }

}