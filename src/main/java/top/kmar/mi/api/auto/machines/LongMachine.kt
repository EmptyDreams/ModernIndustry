package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 长整型的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object LongMachine : IAutoFieldRW, IAutoObjRW<Long> {

    @JvmStatic fun instance() = LongMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Long::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getLong(obj)
        when (val local = annotation.local(field)) {
            Long::class -> writer.writeLong(value)
            Int::class -> writer.writeInt(value.toInt())
            Short::class -> writer.writeShort(value.toShort())
            Byte::class -> writer.writeByte(value.toByte())
            else -> return RWResult.failed(this, "long不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        when (val local = annotation.local(field)) {
            Long::class -> field.setLong(obj, reader.readLong())
            Int::class -> field.setLong(obj, reader.readInt().toLong())
            Short::class -> field.setLong(obj, reader.readShort().toLong())
            Byte::class -> field.setLong(obj, reader.readByte().toLong())
            else -> return RWResult.failed(this, "${local.qualifiedName}不能转化为long")
        }
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Long::class

    override fun write2Local(writer: IDataWriter, value: Long, local: KClass<*>): RWResult {
        when (local) {
            Long::class -> writer.writeLong(value)
            Int::class -> writer.writeInt(value.toInt())
            Short::class -> writer.writeShort(value.toShort())
            Byte::class -> writer.writeByte(value.toByte())
            else -> return RWResult.failed(this, "long不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Long) -> Unit): RWResult {
        when (local) {
            Long::class -> receiver(reader.readLong())
            Int::class -> receiver(reader.readInt().toLong())
            Short::class -> receiver(reader.readShort().toLong())
            Byte::class -> receiver(reader.readByte().toLong())
            else -> return RWResult.failed(this, "long不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }
}