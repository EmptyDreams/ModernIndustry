package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 布尔类型的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object BoolMachine : IAutoFieldRW, IAutoObjRW<Boolean> {

    @JvmStatic fun instance() = this

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Boolean::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getBoolean(obj)
        return when (val local = annotation.local(field)) {
            Boolean::class -> NBTTagByte(if (value) 1 else 0)
            else -> throw ClassCastException("boolean不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val value = (reader as NBTTagByte).int
        field.setBoolean(obj, value != 0)
    }

    override fun match(type: KClass<*>) = type == Boolean::class

    override fun write2Local(value: Boolean, local: KClass<*>): NBTBase {
        if (local != Boolean::class) throw ClassCastException("boolean不能转化为${local.qualifiedName}")
        return NBTTagByte(if (value) 1 else 0)
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Boolean) -> Unit) {
        val value = (reader as NBTTagByte).int
        receiver(value != 0)
    }
}