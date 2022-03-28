package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Byte读写器
 * @author EmptyDreams
 */
class ByteMachine : IAutoFieldRW, IAutoObjRW<Byte> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        return annotation.source(field) == Byte::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        val value = field.getByte(obj)
        when (val local = annotation.local(field)) {
            Byte::class -> writer.writeByte(value)
            Boolean::class -> writer.writeBoolean(value.toInt() != 0)
            else -> return RWResult.failed("byte不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        when (val local = annotation.local(field)) {
            Byte::class -> field.setByte(obj, reader.readByte())
            Boolean::class -> field.setByte(obj, if (reader.readBoolean()) 1 else 0)
            else -> return RWResult.failed("${local.qualifiedName}不能转化为byte")
        }
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Byte::class

    override fun write2Local(writer: IDataWriter, value: Byte, local: KClass<*>): RWResult {
        when (local) {
            Byte::class -> writer.writeByte(value)
            Boolean::class -> writer.writeBoolean(value.toInt() != 0)
            else -> return RWResult.failed("byte不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Byte) -> Unit): RWResult {
        when (local) {
            Byte::class -> receiver(reader.readByte())
            Boolean::class -> receiver(if (reader.readBoolean()) 1 else 0)
            else -> return RWResult.failed("byte不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }


}