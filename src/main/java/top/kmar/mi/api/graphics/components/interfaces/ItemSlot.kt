package top.kmar.mi.api.graphics.components.interfaces

import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * 独立Slot的MI实现
 * @author EmptyDreams
 */
class ItemSlot(
    override val belong: Cmpt,
    override val priority: Int,
    itemHandler: IItemHandler?, index: Int
) : IGraphicsSlot {

    override val slot = SlotItemHandler(itemHandler, index, 0, 0)

    override fun compareTo(other: IGraphicsSlot): Int {
        val result = priority.compareTo(other.priority)
        if (result != 0) return result
        return slot.slotNumber.compareTo(other.slot.slotNumber)
    }

}