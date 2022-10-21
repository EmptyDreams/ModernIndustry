package top.kmar.mi.api.graphics.components

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.components.interfaces.slots.ItemSlot
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.register.others.AutoCmpt
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 通用Slot控件
 * @author EmptyDreams
 */
@AutoCmpt("slot")
class SlotCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = SlotCmptClient()
    override fun buildNewObj() = SlotCmpt(attributes.copy())

    var inventory: ItemStackHandler? = null
        set(value) {
            if (field == null) field = value
        }
    var index: Int by attributes.toIntDelegate()
    var priority: Int by attributes.toIntDelegate(100)
    var drop: Boolean by attributes.toBoolDelegate()
    val slot by lazy(NONE) {
        ItemSlot(this, priority, inventory!!, index).apply {
            drop = this@SlotCmpt.drop
        }
    }

    override fun receive(message: IDataReader) {
        slot.xPos = message.readVarInt()
        slot.yPos = message.readVarInt()
    }

    override fun installParent(parent: Cmpt, gui: BaseGraphics) {
        super.installParent(parent, gui)
        gui.installSlot(slot)
    }

    override fun uninstallParent(oldParent: Cmpt, gui: BaseGraphics) {
        super.uninstallParent(oldParent, gui)
        gui.uninstallSlot(slot)
    }

    @SideOnly(Side.CLIENT)
    inner class SlotCmptClient : ICmptClient {

        override val service = this@SlotCmpt
        override val style = GraphicsStyle(service).apply {
            widthCalculator = FixedSizeMode(18)
            heightCalculator = widthCalculator
            backgroundColor = IntColor.gray
            borderTop.color = IntColor(55, 55, 55)
            borderLeft.color = borderTop.color
            borderBottom.color = IntColor.white
            borderRight.color = IntColor.white
        }

        @Suppress("DuplicatedCode")
        override fun render(graphics: GuiGraphics) {
            CmptHelper.updateSlotInfo(this, slot)
            super.render(graphics)
        }

    }

}