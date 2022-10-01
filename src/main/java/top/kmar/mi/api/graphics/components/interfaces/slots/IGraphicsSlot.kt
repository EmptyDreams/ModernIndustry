package top.kmar.mi.api.graphics.components.interfaces.slots

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.utils.copy
import kotlin.math.min

/**
 *
 * @author EmptyDreams
 */
interface IGraphicsSlot : Comparable<IGraphicsSlot> {

    /**持有该Slot的控件对象 */
    val belong: Cmpt

    /** 放置物品时的优先级（越小优先级越高） */
    val priority: Int

    val slot: Slot

    var xPos: Int
        get() = slot.xPos
        set(value) {
            slot.xPos = value
        }
    var yPos: Int
        get() = slot.yPos
        set(value) {
            slot.yPos = value
        }

    var stack: ItemStack
        get() = slot.stack
        set(value) { slot.putStack(value) }

    val hasStack: Boolean
        get() = slot.hasStack

    fun canTakeStack(player: EntityPlayer) = slot.canTakeStack(player)

    /**
     * 尝试将指定物品放入Slot中（不会修改传入的ItemStack）
     * @return 剩余未放入的物品数量
     */
    fun putStack(content: ItemStack): Int {
        if (content.isEmpty || !slot.isEnabled || !slot.isItemValid(content)) return content.count
        if (stack.isEmpty || (
                    stack.item == content.item &&
                    (!stack.hasSubtypes || stack.metadata == content.metadata) &&
                    ItemStack.areItemStackTagsEqual(stack, content)
               )
        ) {
            val maxCout = min(stack.maxStackSize, slot.slotStackLimit)
            val cout = min(content.count, maxCout - stack.count)
            if (cout == 0) return content.count
            if (stack.isEmpty) stack = content.copy(cout)
            else {
                stack.grow(cout)
                slot.onSlotChanged()
            }
            return content.count - cout
        }
        return content.count
    }

    override fun compareTo(other: IGraphicsSlot): Int {
        val result = priority.compareTo(other.priority)
        if (result != 0) return result
        return slot.slotNumber.compareTo(other.slot.slotNumber)
    }

}