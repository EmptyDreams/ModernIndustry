package top.kmar.mi.api.araw.machines

import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * KClass的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object KClassMachine : IAutoFieldRW, IAutoObjRW<KClass<*>> {

    @JvmStatic fun instance() = KClassMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return KClass::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as KClass<*>?) ?: return RWResult.skipNull()
        if (value.qualifiedName == null) return RWResult.failed(this, "不支持匿名类的读写")
        val local = annotation.local(field)
        if (!KClass::class.java.isAssignableFrom(local.java))
            return RWResult.failed(this, "KClass<*>不能转化为${local.qualifiedName}")
         writer.writeString(value.java.name)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        try {
            field[obj] = Class.forName(reader.readString()).kotlin
        } catch (e: ClassNotFoundException) {
            return RWResult.failedWithException(this, "读取时指定的类不存在", e)
        }
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = KClass::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: KClass<*>, local: KClass<*>): RWResult {
        if (!KClass::class.java.isAssignableFrom(local.java))
            return RWResult.failed(this, "KClass<*>不能转化为${local.qualifiedName}")
        writer.writeString(value.java.name)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (KClass<*>) -> Unit): RWResult {
        val value: KClass<*>
        try {
            value = Class.forName(reader.readString()).kotlin
        } catch (e: ClassNotFoundException) {
            return RWResult.failedWithException(this, "读取时指定的类不存在", e)
        }
        receiver(value)
        return RWResult.success()
    }
}