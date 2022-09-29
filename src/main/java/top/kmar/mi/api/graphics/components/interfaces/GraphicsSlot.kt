package top.kmar.mi.api.graphics.components.interfaces

import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * Slot的MI实现
 * @author EmptyDreams
 */
class GraphicsSlot(
    /**持有该Slot的控件对象 */
    val belong: Cmpt,
    /** 放置物品时的优先级（越小优先级越高） */
    val priority: Int,
    itemHandler: IItemHandler?, index: Int
) : SlotItemHandler(itemHandler, index, 0, 0), Comparable<GraphicsSlot> {

    override fun compareTo(other: GraphicsSlot): Int {
        val result = priority.compareTo(other.priority)
        if (result != 0) return result
        return slotNumber.compareTo(other.slotNumber)
    }

}