package top.kmar.mi.api.craft.elements.handler

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import kotlin.reflect.KClass

/**
 * 合成表元素类型注册机
 * @author EmptyDreams
 */
object CraftTypeRegedit {

    private val regedit = Object2ObjectOpenHashMap<KClass<*>, ICraftTypeHandler<*>>()

    init {
        registry(ItemStack::class, ItemStackCraftTypeHandler)
        registry(Int::class, IntCraftTypeHandler)
        MinecraftForge.EVENT_BUS.post(CraftTypeRegistryEvent())
    }

    /** 注册一个类型处理器 */
    fun registry(key: KClass<*>, handler: ICraftTypeHandler<*>) {
        if (key in regedit) throw IllegalArgumentException("指定的key[${key.qualifiedName}]已经存在")
        regedit[key] = handler
    }

    /** 获取指定类型的handler */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getHandler(type: KClass<T>): ICraftTypeHandler<T> =
        (regedit[type] ?: throw NullPointerException("指定类型[$type]未注册handler")) as ICraftTypeHandler<T>

    /** 获取指定类型的handler */
    fun <T : Any> getHandler(type: Class<T>): ICraftTypeHandler<T> = getHandler(type.kotlin)

    class CraftTypeRegistryEvent : Event() {

        fun registry(key: KClass<*>, handler: ICraftTypeHandler<*>) {
            CraftTypeRegedit.registry(key, handler)
        }

    }

}