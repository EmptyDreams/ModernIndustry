package top.kmar.mi.api.auto.registers

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlin.reflect.KClass

/**
 *
 * @author EmptyDreams
 */
object AutoTypeRegister {

    private val MACHINE_MAP = Object2ObjectOpenHashMap<KClass<*>, List<IAutoRW<Any>>>()



}