package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.*
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 长整型的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object LongMachine : IAutoFieldRW, IAutoObjRW<Long> {

    @JvmStatic fun instance() = LongMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Long::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getLong(obj)
        return when (val local = annotation.local(field)) {
            Long::class -> NBTTagLong(value)
            Int::class -> NBTTagInt(value.toInt())
            Short::class -> NBTTagShort(value.toShort())
            Byte::class -> NBTTagByte(value.toByte())
            else -> throw ClassCastException("long不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = when (val local = annotation.local(field)) {
            Long::class -> (reader as NBTTagLong).long
            Int::class -> (reader as NBTTagInt).long
            Short::class -> (reader as NBTTagShort).long
            Byte::class -> (reader as NBTTagByte).long
            else -> throw ClassCastException("${local.qualifiedName}不能转化为long")
        }
        field.setLong(obj, value)
    }

    override fun match(type: KClass<*>) = type == Long::class

    override fun write2Local(value: Long, local: KClass<*>): NBTBase {
        return when (local) {
            Long::class -> NBTTagLong(value)
            Int::class -> NBTTagInt(value.toInt())
            Short::class -> NBTTagShort(value.toShort())
            Byte::class -> NBTTagByte(value.toByte())
            else -> throw ClassCastException("long不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Long) -> Unit) {
        val value = when (local) {
            Long::class -> (reader as NBTTagLong).long
            Int::class -> (reader as NBTTagInt).long
            Short::class -> (reader as NBTTagShort).long
            Byte::class -> (reader as NBTTagByte).long
            else -> throw ClassCastException("${local.qualifiedName}不能转化为long")
        }
        receiver(value)
    }
}