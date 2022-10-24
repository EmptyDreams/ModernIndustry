package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagFloat
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 单精度浮点数的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object FloatMachine : IAutoFieldRW, IAutoObjRW<Float> {

    @JvmStatic fun instance() = FloatMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Float::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getFloat(obj)
        return when (val local = annotation.local(field)) {
            Float::class -> NBTTagFloat(value)
            else -> throw ClassCastException("float不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val value = (reader as NBTTagFloat).float
        field.setFloat(obj, value)
    }

    override fun match(type: KClass<*>) = type == Float::class

    override fun write2Local(value: Float, local: KClass<*>): NBTBase {
        if (local != Float::class) throw ClassCastException("float不能转化为${local.qualifiedName}")
        return NBTTagFloat(value)
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Float) -> Unit) {
        val value = (reader as NBTTagFloat).float
        receiver(value)
    }
}