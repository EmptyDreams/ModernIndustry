package top.kmar.mi.api.graphics.components.interfaces

import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot

/**
 * Slot的MI实现
 * @author EmptyDreams
 */
class GraphicsSlot(
    /**持有该Slot的控件对象 */
    val belong: Cmpt,
    /** 放置物品时的优先级（越小优先级越高） */
    val priority: Int,
    inventoryIn: IInventory, index: Int, xPosition: Int, yPosition: Int
) : Slot(inventoryIn, index, xPosition, yPosition), Comparable<GraphicsSlot> {

    override fun compareTo(other: GraphicsSlot): Int {
        val result = priority.compareTo(other.priority)
        if (result != 0) return result
        return slotNumber.compareTo(other.slotNumber)
    }

}