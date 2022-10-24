package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagShort
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Short的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object ShortMachine : IAutoFieldRW, IAutoObjRW<Short> {

    @JvmStatic fun instance() = ShortMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Short::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getShort(obj)
        return when (val local = annotation.local(field)) {
            Short::class -> NBTTagShort(value)
            Byte::class, Boolean::class -> NBTTagByte(value.toByte())
            else -> throw ClassCastException("short不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = when (val local = annotation.local(field)) {
            Short::class -> (reader as NBTTagShort).short
            Byte::class, Boolean::class -> (reader as NBTTagByte).short
            else -> throw ClassCastException("${local.qualifiedName}不能转化为short")
        }
        field.setShort(obj, value)
    }

    override fun match(type: KClass<*>) = type == Short::class

    override fun write2Local(value: Short, local: KClass<*>): NBTBase {
        return when (local) {
            Short::class -> NBTTagShort(value)
            Byte::class, Boolean::class -> NBTTagByte(value.toByte())
            else -> throw ClassCastException("short不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Short) -> Unit) {
        val value = when (local) {
            Short::class -> (reader as NBTTagShort).short
            Byte::class, Boolean::class -> (reader as NBTTagByte).short
            else -> throw ClassCastException("${local.qualifiedName}不能转化为short")
        }
        receiver(value)
    }
}