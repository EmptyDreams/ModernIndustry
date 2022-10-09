package top.kmar.mi.api.graphics.components

import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.components.interfaces.slots.ItemSlot
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt

/**
 * 仅允许输出的Slot控件
 * @author EmptyDreams
 */
@AutoCmpt("output")
class SlotOutputCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = SlotOutputCmptClient()

    var inventory: ItemStackHandler? = null
        set(value) {
            if (field == null) field = value
        }
    val slot by lazy(LazyThreadSafetyMode.NONE) {
        ItemSlot(
            this,
            attributes["priority", "100"].toInt(),
            inventory!!,
            attributes["index", "0"].toInt()
        ).apply { canPutIn = false }
    }

    override fun receive(message: IDataReader) {
        slot.xPos = message.readVarInt()
        slot.yPos = message.readVarInt()
    }

    override fun installParent(parent: Cmpt) {
        installSlot(slot)
    }

    override fun uninstallParent(oldParent: Cmpt) {
        uninstallSlot(slot)
    }

    inner class SlotOutputCmptClient : CmptClient {

        override val service = this@SlotOutputCmpt
        override val style = GraphicsStyle(service).apply {
            width = FixedSizeMode(26)
            height = width
            backgroundColor = IntColor.gray
            borderTop.color = IntColor(55, 55, 55)
            borderLeft.color = borderTop.color
            borderBottom.color = IntColor.white
            borderRight.color = IntColor.white
        }

        @Suppress("DuplicatedCode")
        override fun render(graphics: GuiGraphics) {
            val x = style.run { this.x + borderLeft.weight }
            val y = style.run { this.y + borderTop.weight }
            if (slot.xPos != x || slot.yPos != y) {
                val message = ByteDataOperator(4).apply {
                    writeVarInt(x)
                    writeVarInt(y)
                }
                send2Service(message)
                slot.xPos = x
                slot.yPos = y
            }
            super.render(graphics)
        }

    }

}