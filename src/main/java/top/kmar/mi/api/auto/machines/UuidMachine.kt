package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.KClass

/**
 * UUID的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object UuidMachine : IAutoFieldRW, IAutoObjRW<UUID> {

    @JvmStatic fun instance() = UuidMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == UUID::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as UUID?) ?: return RWResult.skipNull()
        when (val local = annotation.local(field)) {
            UUID::class -> writer.writeUuid(value)
            else -> return RWResult.failed(this, "UUID不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        field.set(obj, reader.readUuid())
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == UUID::class

    override fun write2Local(writer: IDataWriter, value: UUID, local: KClass<*>): RWResult {
        if (local != UUID::class)
            return RWResult.failed(this, "UUID不能转化为${local.qualifiedName}")
        writer.writeUuid(value)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (UUID) -> Unit): RWResult {
        receiver(reader.readUuid())
        return RWResult.success()
    }

}