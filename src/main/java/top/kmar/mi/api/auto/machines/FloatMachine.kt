package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 单精度浮点数的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
class FloatMachine : IAutoFieldRW, IAutoObjRW<Float> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Float::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getFloat(obj)
        when (val local = annotation.local(field)) {
            Float::class -> writer.writeFloat(value)
            else -> return RWResult.failed("float不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        field.setFloat(obj, reader.readFloat())
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Float::class

    override fun write2Local(writer: IDataWriter, value: Float, local: KClass<*>): RWResult {
        if (local != Float::class) return RWResult.failed("float不能转化为${local.qualifiedName}")
        writer.writeFloat(value)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Float) -> Unit): RWResult {
        receiver(reader.readFloat())
        return RWResult.success()
    }
}