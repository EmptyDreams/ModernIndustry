package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Byte读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object ByteMachine : IAutoFieldRW, IAutoObjRW<Byte> {

    @JvmStatic fun instance() = this

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Byte::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getByte(obj)
        return when (val local = annotation.local(field)) {
            Byte::class, Boolean::class -> NBTTagByte(value)
            else -> throw ClassCastException("byte不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (reader as NBTTagByte).byte
        when (val local = annotation.local(field)) {
            Byte::class, Boolean::class -> field.setByte(obj, value)
            else -> throw ClassCastException("${local.qualifiedName}不能转化为byte")
        }
    }

    override fun match(type: KClass<*>) = type == Byte::class

    override fun write2Local(value: Byte, local: KClass<*>): NBTBase {
        return when (local) {
            Byte::class, Boolean::class -> NBTTagByte(value)
            else -> throw ClassCastException("byte不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Byte) -> Unit) {
        val value = (reader as NBTTagByte).byte
        when (local) {
            Byte::class, Boolean::class -> receiver(value)
            else -> throw ClassCastException("byte不能转化为${local.qualifiedName}")
        }
    }


}