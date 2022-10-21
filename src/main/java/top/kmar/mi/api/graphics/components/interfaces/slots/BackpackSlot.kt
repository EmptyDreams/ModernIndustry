package top.kmar.mi.api.graphics.components.interfaces.slots

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import top.kmar.mi.api.graphics.components.interfaces.Cmpt

/**
 * 玩家背包
 * @author EmptyDreams
 */
class BackpackSlot(
    override val belong: Cmpt,
    override val priority: Int,
    val player: EntityPlayer,
    index: Int
): IGraphicsSlot {

    @Suppress("UNUSED_PARAMETER")
    override var drop: Boolean
        get() = false
        set(value) {}

    override val slot = Slot(player.inventory, index, Int.MIN_VALUE, Int.MIN_VALUE)

}