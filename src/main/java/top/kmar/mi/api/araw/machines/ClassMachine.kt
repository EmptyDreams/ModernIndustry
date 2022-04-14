package top.kmar.mi.api.araw.machines

import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Class的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object ClassMachine : IAutoFieldRW, IAutoObjRW<Class<*>> {

    @JvmStatic fun instance() = ClassMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Class::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as Class<*>?) ?: return RWResult.skipNull()
        when (val local = annotation.local(field)) {
            Class::class -> writer.writeString(value.name)
            else -> return RWResult.failed(this, "Class<?>不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        try {
            field[obj] = Class.forName(reader.readString())
        } catch (e: ClassNotFoundException) {
            return RWResult.failedWithException(this, "读取时指定的类不存在", e)
        }
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Class::class

    override fun write2Local(writer: IDataWriter, value: Class<*>, local: KClass<*>): RWResult {
        if (local != Class::class)
            return RWResult.failed(this, "Class<?>不能转化为${local.qualifiedName}")
        writer.writeString(value.name)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Class<*>) -> Unit): RWResult {
        val value: Class<*>
        try {
            value = Class.forName(reader.readString())
        } catch (e: ClassCastException) {
            return RWResult.failedWithException(this, "读取时指定的类不存在", e)
        }
        receiver(value)
        return RWResult.success()
    }
}