package top.kmar.mi.api.auto.machines

import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import top.kmar.mi.api.auto.interfaces.*
import top.kmar.mi.api.auto.registers.AutoTypeRegister
import top.kmar.mi.api.dor.interfaces.IDataReader
import top.kmar.mi.api.dor.interfaces.IDataWriter
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * [FluidStack]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object FluidStackMachine : IAutoFieldRW, IAutoObjRW<FluidStack> {

    @JvmStatic fun instance() = FluidStackMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == FluidStack::class
    }

    override fun write2Local(writer: IDataWriter, field: Field, obj: Any): RWResult {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as FluidStack? ?: return RWResult.skipNull()
        return write2Local(writer, value, annotation.local(field))
    }

    override fun read2Obj(reader: IDataReader, field: Field, obj: Any): RWResult =
        read2Obj(reader, FluidStack::class) { field[obj] = it }


    override fun match(type: KClass<*>) = type == FluidStack::class

    override fun write2Local(writer: IDataWriter, value: FluidStack, local: KClass<*>): RWResult {
        if (local != FluidStack::class)
            return RWResult.failed(this, "FluidStack不能转化为${local.qualifiedName}")
        writer.writeVarInt(value.amount)
        writer.writeString(value.fluid.name)
        return RWResult.success()
    }

    override fun read2Obj(reader: IDataReader, local: KClass<*>, receiver: (FluidStack) -> Unit): RWResult {
        val amount = reader.readVarInt()
        val name = reader.readString()
        val value = FluidStack(FluidRegistry.getFluid(name), amount)
        receiver(value)
        return RWResult.success()
    }

}