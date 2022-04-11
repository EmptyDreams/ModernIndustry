package top.kmar.mi.api.auto.machines

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
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

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj]
        return if (value == null) {
            if (Modifier.isFinal(field.modifiers)) return RWResult.failedFinal(this)
            read2Obj(reader, annotation.local(field)) { field[obj] = it }
        } else {
            val cap = getCap(annotation.local(field).java)
            cap!!.readNBT(value, null, reader.readTag())
            RWResult.success()
        }
    }

    override fun match(type: KClass<*>) = getCap(type.java) != null

    override fun write2Local(writer: IDataWriter, value: Any, local: KClass<*>): RWResult {
        if (!local.java.isAssignableFrom(value::class.java))
            return RWResult.failed(this,
                "${value::class.qualifiedName}不能转化为${local.qualifiedName}")
        val cap = getCap(local.java)
        val nbt = cap!!.writeNBT(value, null) ?: return RWResult.skipNull()
        writer.writeTag(nbt)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Any) -> Unit): RWResult {
        val value = try {
            local.java.newInstance()
        } catch (e: Throwable) {
            return RWResult.failedWithException(this, "CapabilityMachine在构造对象时出现了异常", e)
        }
        val cap = getCap(local.java)
        val nbt = reader.readTag()
        cap!!.readNBT(value, null, nbt)
        return RWResult.success()
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