package top.kmar.mi.api.graphics.components

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemStackHandler
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes
import top.kmar.mi.api.graphics.components.interfaces.ICmptClient
import top.kmar.mi.api.graphics.components.interfaces.IntColor
import top.kmar.mi.api.graphics.components.interfaces.slots.ItemSlot
import top.kmar.mi.api.graphics.utils.FixedSizeMode
import top.kmar.mi.api.graphics.utils.GraphicsStyle
import top.kmar.mi.api.graphics.utils.GuiGraphics
import top.kmar.mi.api.regedits.others.AutoCmpt
import top.kmar.mi.api.utils.floorDiv2
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 通用Slot控件
 *
 * 类中的属性必须在使用slot**之前**完成初始化
 *
 * @author EmptyDreams
 */
@AutoCmpt("slot")
open class SlotCmpt(attributes: CmptAttributes) : Cmpt(attributes) {

    override fun initClientObj() = SlotCmptClient()
    override fun buildNewObj() = SlotCmpt(attributes.copy())

    var handler: ItemStackHandler? = null
        set(value) {
            if (field == null) field = value
        }
    val slotAttributes = ItemSlot.SlotAttributes(attributes)
    val slot by lazy(NONE) {
        ItemSlot(this, slotAttributes.priority, handler!!, slotAttributes.index).apply {
            drop = slotAttributes.drop
            canPutIn = !slotAttributes.forbidInput
            canPutOut = !slotAttributes.forbidOutput
            inputChecker = slotAttributes.inputChecker
            outputChecker = slotAttributes.outputChecker
            onSlotChanged = slotAttributes.onSlotChanged
        }
    }

    override fun receive(message: NBTBase) {
        val nbt = message as NBTTagCompound
        slot.xPos = nbt.getInteger("x")
        slot.yPos = nbt.getInteger("y")
    }

    override fun initHandler(handler: ItemStackHandler) {
        this.handler = handler
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

        override fun render(graphics: GuiGraphics) {
            val style = client.style
            val x = style.x + style.width.floorDiv2() + style.borderLeft.weight - 9
            val y = style.y + style.height.floorDiv2() + style.borderTop.weight - 9
            if (x != slot.xPos && y != slot.yPos) {
                val message = NBTTagCompound().apply {
                    setInteger("x", x)
                    setInteger("y", y)
                }
                client.send2Service(message)
                slot.xPos = x
                slot.yPos = y
            }
            super.render(graphics)
        }

    }

}