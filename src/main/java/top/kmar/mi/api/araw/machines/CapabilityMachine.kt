package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import top.kmar.mi.coremod.other.ICapManagerCheck
import top.kmar.mi.coremod.other.ICapStorageType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Cap的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.GENERAL_TYPE shr 1)
object CapabilityMachine : IAutoFieldRW, IAutoObjRW<Any> {

    @JvmStatic fun instance() = CapabilityMachine

    override fun allowFinal() = true

    override fun match(field: Field) = getCap(field.type) != null

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] ?: return null
        return write2Local(value, annotation.local(field))
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj]
        if (value == null) {
            if (Modifier.isFinal(field.modifiers))
                throw UnsupportedOperationException("不支持对初始值为null且为final的属性进行写入")
            read2Obj(reader, annotation.local(field)) { field[obj] = it }
        } else {
            val cap = getCap(annotation.local(field).java)
            cap!!.readNBT(value, null, reader)
        }
    }

    override fun match(type: KClass<*>) = getCap(type.java) != null

    override fun write2Local(value: Any, local: KClass<*>): NBTBase? {
        if (!local.java.isAssignableFrom(value::class.java))
            throw ClassCastException("${value::class.qualifiedName}不能转化为${local.qualifiedName}")
        val cap = getCap(local.java)
        return cap!!.writeNBT(value, null)
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Any) -> Unit) {
        val value = local.java.newInstance()
        val cap = getCap(local.java)
        cap!!.readNBT(value, null, reader)
    }

    private fun getCap(type: Class<*>): Capability<Any>? {
        @Suppress("KotlinConstantConditions")
        val check = CapabilityManager.INSTANCE as ICapManagerCheck
        var result: Capability<Any>? = null
        check.forEachCaps {
            val getter = it as ICapStorageType
            if (getter.storageType.isAssignableFrom(type)) {
                @Suppress("UNCHECKED_CAST")
                result = it as Capability<Any>
                return@forEachCaps true
            }
            false
        }
        return result
    }

}