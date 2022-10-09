package top.kmar.mi.api.graphics.components.interfaces.slots

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import top.kmar.mi.api.graphics.components.interfaces.Cmpt

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

    override val slot = object : SlotItemHandler(itemHandler, index, 0, 0) {
        override fun isItemValid(stack: ItemStack): Boolean {
            return canPutIn && super.isItemValid(stack)
        }
    }

}