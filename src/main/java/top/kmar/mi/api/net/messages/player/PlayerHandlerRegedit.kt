package top.kmar.mi.api.net.messages.player

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase

/**
 * [IPlayerHandler] 的注册机
 * @author EmptyDreams
 */
object PlayerHandlerRegedit {

    @JvmStatic
    private val registries = Object2ObjectOpenHashMap<String, IPlayerHandler>()

    @JvmStatic
    fun registry(key: String, handler: IPlayerHandler) {
        if (key in registries) throw AssertionError("指定的 key[$key] 已经被注册")
        registries[key] = handler
    }

    @JvmStatic
    fun apply(key: String, player: EntityPlayer, data: NBTBase): Boolean {
        val handler = registries[key] ?: return false
        handler.apply(player, data)
        return true
    }

}