package top.kmar.mi.api.graphics.components.interfaces.slots

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import top.kmar.mi.api.graphics.components.interfaces.Cmpt
import top.kmar.mi.api.graphics.components.interfaces.CmptAttributes

/**
 * 独立Slot的MI实现
 * @author EmptyDreams
 */
class ItemSlot(
    override val belong: Cmpt,
    override val priority: Int,
    itemHandler: IItemHandler?, index: Int
) : IGraphicsSlot {

    var canPutIn = true
    var canPutOut = true
    override var drop = false

    /** 检查能否放入物品 */
    var inputChecker: (ItemStack) -> Boolean = { true }
    /** 检查能否取出物品 */
    var outputChecker: (ItemStack, EntityPlayer) -> Boolean = { _, _ -> true }
    /** 当物品内容变化时触发 */
    var onSlotChanged: () -> Unit = { }

    override val slot = object : SlotItemHandler(itemHandler, index, -1, -1) {

        override fun isItemValid(stack: ItemStack): Boolean {
            return canPutIn && super.isItemValid(stack) && inputChecker(stack)
        }

        override fun canTakeStack(playerIn: EntityPlayer): Boolean {
            return canPutOut && super.canTakeStack(playerIn) && outputChecker(stack, playerIn)
        }

        override fun onSlotChanged() {
            super.onSlotChanged()
            this@ItemSlot.onSlotChanged()
        }

    }

    class SlotAttributes(attributes: CmptAttributes) {

        /** slot在`handler`中的下标 */
        var index: Int by attributes.toIntDelegate()
        /** 优先级 */
        var priority: Int by attributes.toIntDelegate(100)
        /** 关闭GUI时是否丢弃slot内的物品 */
        var drop: Boolean by attributes.toBoolDelegate()
        /** 是否禁止放入物品 */
        var forbidInput: Boolean by attributes.toBoolDelegate()
        /** 是否禁止取出物品 */
        var forbidOutput: Boolean by attributes.toBoolDelegate()
        /** 检查能否放入物品 */
        var inputChecker: (ItemStack) -> Boolean = { true }
        /** 检查能否取出物品 */
        var outputChecker: (ItemStack, EntityPlayer) -> Boolean = { _, _ -> true }
        /** 当物品内容变化时触发 */
        var onSlotChanged: () -> Unit = { }

    }

}