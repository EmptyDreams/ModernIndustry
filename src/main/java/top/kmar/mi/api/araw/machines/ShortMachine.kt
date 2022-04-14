package top.kmar.mi.api.araw.machines

import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Short的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object ShortMachine : IAutoFieldRW, IAutoObjRW<Short> {

    @JvmStatic fun instance() = ShortMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Short::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getShort(obj)
        when (val local = annotation.local(field)) {
            Short::class -> writer.writeShort(value)
            Byte::class -> writer.writeByte(value.toByte())
            Boolean::class -> writer.writeBoolean(value.toInt() != 0)
            else -> return RWResult.failed(this, "short不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        when (val local = annotation.local(field)) {
            Short::class -> field.setShort(obj, reader.readShort())
            Byte::class -> field.setShort(obj, reader.readByte().toShort())
            Boolean::class -> field.setShort(obj, if (reader.readBoolean()) 1 else 0)
            else -> return RWResult.failed(this, "${local.qualifiedName}不能转化为short")
        }
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Short::class

    override fun write2Local(writer: IDataWriter, value: Short, local: KClass<*>): RWResult {
        when (local) {
            Short::class -> writer.writeShort(value)
            Byte::class -> writer.writeByte(value.toByte())
            Boolean::class -> writer.writeBoolean(value.toInt() != 0)
            else -> return RWResult.failed(this, "short不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Short) -> Unit): RWResult {
        when (local) {
            Short::class -> receiver(reader.readShort())
            Byte::class -> receiver(reader.readByte().toShort())
            Boolean::class -> receiver(if (reader.readBoolean()) 1 else 0)
            else -> return RWResult.failed(this, "${local.qualifiedName}不能转化为short")
        }
        return RWResult.success()
    }
}