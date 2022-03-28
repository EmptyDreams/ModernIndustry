package top.kmar.mi.api.auto.machines

import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.electricity.interfaces.IVoltage
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 电压的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object VoltageMachine : IAutoFieldRW, IAutoObjRW<IVoltage> {

    @JvmStatic fun instance() = VoltageMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return IVoltage::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as IVoltage? ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        field[obj] = reader.readVoltage()
        return RWResult.success()
    }

    override fun match(type: KClass<*>) = IVoltage::class.java.isAssignableFrom(type.java)

    override fun write2Local(writer: IDataWriter, value: IVoltage, local: KClass<*>): RWResult {
        if (!local::class.java.isAssignableFrom(value::class.java))
            return RWResult.failed("Voltage不能转化为${local.qualifiedName}")
        writer.writeVoltage(value)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (IVoltage) -> Unit): RWResult {
        receiver(reader.readVoltage())
        return RWResult.success()
    }

}