package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 双精度浮点数的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object DoubleMachine : IAutoFieldRW, IAutoObjRW<Double> {

    @JvmStatic fun instance() = DoubleMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        return annotation.source(field) == Double::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        val value = field.getDouble(obj)
        when (val local = annotation.local(field)) {
            Double::class -> writer.writeDouble(value)
            Float::class -> writer.writeFloat(value.toFloat())
            else -> return RWResult.failed("double不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)!!
        when (val local = annotation.local(field)) {
            Double::class -> field.setDouble(obj, reader.readDouble())
            Float::class -> field.setDouble(obj, reader.readFloat().toDouble())
            else -> return RWResult.failed("${local.qualifiedName}不能转化为double")
        }
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = type == Double::class

    override fun write2Local(writer: IDataWriter, value: Double, local: KClass<*>): RWResult {
        when (local) {
            Double::class -> writer.writeDouble(value)
            Float::class -> writer.writeFloat(value.toFloat())
            else -> return RWResult.failed("double不能转化为${local.qualifiedName}")
        }
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (Double) -> Unit): RWResult {
        when (local) {
            Double::class -> receiver(reader.readDouble())
            Float::class -> receiver(reader.readFloat().toDouble())
            else -> return RWResult.failed("${local.qualifiedName}不能转化为double")
        }
        return RWResult.success()
    }
}