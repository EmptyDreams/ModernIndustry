package top.kmar.mi.api.graphics.components

import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.dor.ByteDataOperator
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptClient
import top.kmar.mi.api.graphics.components.interfaces.GraphicsSlot
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import java.awt.Color
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 通用Slot控件
 * @author EmptyDreams
 */
@AutoCmpt("slot")
class SlotCmpt(id: String) : Cmpt(id) {

    override fun initClientObj() = SlotCmptClient()

    var inventory: ItemStackHandler? = null
        set(value) {
            if (field == null) field = value
        }
    var index: Int = -1
        set(value) {
            if (index == -1) field = value
        }
    var priority: Int = 100
    val slot by lazy(NONE) {
        GraphicsSlot(this, priority, inventory!!, index)
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

    inner class SlotCmptClient : CmptClient {

        override val service = this@SlotCmpt
        override val style = GraphicsStyle(service).apply {
            width = FixedSizeMode(18)
            height = width
            backgroundColor = Color(139, 139, 139)
            borderTop.color = Color(55, 55, 55)
            borderLeft.color = borderTop.color
            borderBottom.color = Color.WHITE
            borderRight.color = Color.WHITE
        }

        private var preX = Int.MIN_VALUE
        private var preY = Int.MIN_VALUE

        override fun render(graphics: GuiGraphics) {
            val x = style.x
            val y = style.y
            if (preX != x || preY != y) {
                val message = ByteDataOperator(4).apply {
                    writeVarInt(x)
                    writeVarInt(y)
                }
                send2Service(message)
                preX = x
                preY = y
            }
            graphics.overflowHidden = false
            super.render(graphics)
        }

    }

}