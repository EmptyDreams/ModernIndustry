package top.kmar.mi.api.graphics.components.interfaces.slots

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

    override val slot = SlotItemHandler(itemHandler, index, 0, 0)

}