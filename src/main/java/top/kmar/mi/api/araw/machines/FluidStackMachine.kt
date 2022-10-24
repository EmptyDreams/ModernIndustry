package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * [FluidStack]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object FluidStackMachine : IAutoFieldRW, IAutoObjRW<FluidStack?> {

    @JvmStatic fun instance() = FluidStackMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == FluidStack::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as FluidStack?
        return write2Local(value, annotation.local(field))
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) =
        read2Obj(reader, FluidStack::class) { field[obj] = it }

    override fun match(type: KClass<*>) = type == FluidStack::class

    override fun write2Local(value: FluidStack?, local: KClass<*>): NBTBase {
        if (local != FluidStack::class)
            throw ClassCastException("FluidStack不能转化为${local.qualifiedName}")
        if (value == null) return NBTTagByte(0)
        val result = NBTTagCompound()
        result.setInteger("amount", value.amount)
        result.setString("name", value.fluid.name)
        return result
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (FluidStack?) -> Unit) {
        if (reader is NBTTagByte) receiver(null)
        else if (reader is NBTTagCompound) {
            val amount = reader.getInteger("amount")
            val name = reader.getString("name")
            receiver(FluidStack(FluidRegistry.getFluid(name), amount))
        }
        throw AssertionError()
    }

}