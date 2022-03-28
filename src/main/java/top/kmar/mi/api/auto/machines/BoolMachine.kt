package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 布尔类型的读写器
 * @author EmptyDreams
 */
class BoolMachine : IAutoFieldRW, IAutoObjRW<Boolean> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Boolean::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getBoolean(obj)
        when (val local = annotation.local(field)) {
            Boolean::class -> writer.writeBoolean(value)
            else -> return RWResult.failed("boolean不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        field.setBoolean(obj, reader.readBoolean())
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Boolean::class

    override fun write2Local(writer: IDataWriter, value: Boolean, local: KClass<*>): RWResult {
        if (local != Boolean::class) return RWResult.failed("boolean不能转化为${local.qualifiedName}")
        writer.writeBoolean(value)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Boolean) -> Unit): RWResult {
        receiver(reader.readBoolean())
        return RWResult.success()
    }
}