package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagDouble
import net.minecraft.nbt.NBTTagFloat
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
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
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Double::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getDouble(obj)
        return when (val local = annotation.local(field)) {
            Double::class -> NBTTagDouble(value)
            Float::class -> NBTTagFloat(value.toFloat())
            else -> throw ClassCastException("double不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        when (val local = annotation.local(field)) {
            Double::class -> field.setDouble(obj, (reader as NBTTagDouble).double)
            Float::class -> field.setDouble(obj, (reader as NBTTagFloat).double)
            else -> throw ClassCastException("${local.qualifiedName}不能转化为double")
        }
    }

    override fun match(type: KClass<*>) = type == Double::class

    override fun write2Local(value: Double, local: KClass<*>): NBTBase {
        return when (local) {
            Double::class -> NBTTagDouble(value)
            Float::class -> NBTTagFloat(value.toFloat())
            else -> throw ClassCastException("double不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Double) -> Unit) {
        when (local) {
            Double::class -> receiver((reader as NBTTagDouble).double)
            Float::class -> receiver((reader as NBTTagFloat).double)
            else -> throw ClassCastException("${local.qualifiedName}不能转化为double")
        }
    }
}