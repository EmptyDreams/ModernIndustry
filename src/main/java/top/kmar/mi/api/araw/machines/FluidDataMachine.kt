package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidRegistry
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.fluid.data.FluidData
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * [FluidData]的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object FluidDataMachine : IAutoFieldRW, IAutoObjRW<FluidData> {

    @JvmStatic fun instance() = FluidDataMachine

    override fun allowFinal() = true

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == FluidData::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field[obj] as FluidData? ?: return null
        return write2Local(value, annotation.local(field))
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        var data: FluidData? = null
        read2Obj(reader, FluidData::class) { data = it }
        when (val value = field[obj] as FluidData?) {
            null -> {
                if (Modifier.isFinal(field.modifiers))
                    throw UnsupportedOperationException("不支持对默认值为null且为final的属性进行读写")
                field[obj] = data
            }
            else -> {
                value.setEmpty()
                value.plus(data)
            }
        }
    }

    override fun match(type: KClass<*>) = type == FluidData::class

    override fun write2Local(value: FluidData, local: KClass<*>): NBTBase {
        if (local != FluidData::class) throw ClassCastException("FluidData不能转化为${local.qualifiedName}")
        if (value.isAir) return NBTTagByte(0)
        val result = NBTTagCompound()
        result.setInteger("amount", value.amount)
        result.setString("name", value.fluid!!.name)
        return result
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (FluidData) -> Unit) {
        if (reader is NBTTagByte) receiver(FluidData.empty())
        else if (reader is NBTTagCompound) {
            val fluid = FluidRegistry.getFluid(reader.getString("name"))
            val amount = reader.getInteger("amount")
            receiver(FluidData(fluid, amount))
        }
        throw AssertionError()
    }

}