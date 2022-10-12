package top.kmar.mi.api.event

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.eventhandler.Event
import top.kmar.mi.api.graphics.BaseGraphics
import top.kmar.mi.api.graphics.components.interfaces.Cmpt

/**
 * 当玩家通过MI打开GUI时触发
 *
 * 该事件在[BaseGraphics.init]函数执行后，[Cmpt.installParent]函数执行前触发
 *
 * @author EmptyDreams
 */
class PlayerOpenGraphicsEvent(
    val player: EntityPlayer,
    val container: BaseGraphics,
    val key: ResourceLocation,
    val id: Int,
    val x: Int,
    val y: Int,
    val z: Int
) : Event()