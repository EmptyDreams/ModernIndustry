package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 通用枚举类读写器
 * @author EmptyDreams
 */
class EnumMachine : IAutoFieldRW, IAutoObjRW<Enum<*>> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return Enum::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field)
        val value = field[obj] as Enum<*>? ?: return RWResult.skipNull()
        return write2Local(writer, value, local)
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val local = annotation.local(field)
        return read2Obj(reader, local) { field[obj] = it }
    }

    override fun match(type: KClass<*>) = Enum::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: Enum<*>, local: KClass<*>): RWResult {
        if (local != value::class)
            return RWResult.failed("Enum不能转化为${local.qualifiedName}")
        writer.writeString(value::class.qualifiedName)
        writer.writeString(value.name)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Enum<*>) -> Unit): RWResult {
        val className = reader.readString()
        val valueName = reader.readString()
        try {
            val enumClass = Class.forName(className)
            val method = java.lang.Enum::class.java.getMethod("values", Class::class.java, String::class.java)
            receiver(method(enumClass, valueName) as Enum<*>)
        } catch (e: Throwable) {
            return RWResult.failedWithException("枚举类读取过程中发生异常", e)
        }
        return RWResult.success()
    }

}