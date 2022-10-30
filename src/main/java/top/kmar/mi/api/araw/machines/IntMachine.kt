package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagShort
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 整型的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object IntMachine : IAutoFieldRW, IAutoObjRW<Int> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Int::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = field.getInt(obj)
        return writeHelper(value, annotation.local(field))
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = when (val local = annotation.local(field)) {
            Int::class -> (reader as NBTTagInt).int
            Byte::class, Boolean::class -> (reader as NBTTagByte).int
            Short::class -> (reader as NBTTagShort).int
            else -> throw ClassCastException("${local.qualifiedName}不能转化为int")
        }
        field.setInt(obj, value)
    }

    override fun match(type: KClass<*>) = type == Int::class

    override fun write2Local(value: Int, local: KClass<*>): NBTBase {
        return writeHelper(value, local)
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Int) -> Unit) {
        val value = when (local) {
            Int::class -> (reader as NBTTagInt).int
            Byte::class, Boolean::class -> (reader as NBTTagByte).int
            Short::class -> (reader as NBTTagShort).int
            else -> throw ClassCastException("${local.qualifiedName}不能转化为int")
        }
        receiver(value)
    }

    private fun writeHelper(value: Int, local: KClass<*>): NBTBase {
        return when (local) {
            Int::class -> NBTTagInt(value)
            Byte::class, Boolean::class -> NBTTagByte(value.toByte())
            Short::class -> NBTTagShort(value.toShort())
            else -> throw ClassCastException("int不能转化为${local.qualifiedName}")
        }
    }

}