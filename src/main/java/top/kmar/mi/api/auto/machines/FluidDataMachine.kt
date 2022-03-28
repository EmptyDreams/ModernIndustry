package top.kmar.mi.api.auto.machines

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.fluid.data.FluidData
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * [FluidData]的读写器
 * @author EmptyDreams
 */
class FluidDataMachine : IAutoFieldRW, IAutoObjRW<FluidData> {

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == FluidData::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as FluidData? ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult {
        var data: FluidData? = null
        val check = read2Obj(reader, FluidData::class) { data = it }
        if (!check.isSuccessful()) return check
        val value = field[obj] as FluidData?
        if (value == null) field[obj] = data
        else {
            value.setEmpty()
            value.plus(data)
        }
        return check
    }

    override fun match(type: KClass<*>) = type == FluidData::class

    override fun write2Local(writer: IDataWriter, value: FluidData, local: KClass<*>): RWResult {
        if (local != FluidData::class)
            return RWResult.failed("FluidData不能转化为${local.qualifiedName}")
        writer.writeBoolean(value.isAir)
        if (!value.isAir) writer.writeString(value.fluid!!.name)
        writer.writeVarInt(value.amount)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (FluidData) -> Unit): RWResult {
        val fluid: Fluid? = if (reader.readBoolean()) null else {
            val name = reader.readString()
            FluidRegistry.getFluid(name)
        }
        val amount = reader.readVarInt()
        receiver(FluidData(fluid, amount))
        return RWResult.success()
    }

}