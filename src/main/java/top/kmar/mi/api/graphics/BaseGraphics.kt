package top.kmar.mi.api.graphics

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container

/**
 *
 * @author EmptyDreams
 */
class BaseGraphics : Container() {

    /** 是否可以被指定玩家打开 */
    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return true
    }

}