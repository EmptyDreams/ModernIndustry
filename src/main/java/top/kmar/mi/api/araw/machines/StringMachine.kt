package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagString
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * 字符串的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.BASE_TYPE)
object StringMachine : IAutoFieldRW, IAutoObjRW<String> {

    @JvmStatic fun instance() = StringMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == String::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as String?) ?: return NBTTagByte(0)
        return when (val local = annotation.local(field)) {
            String::class -> NBTTagString(value)
            Int::class -> NBTTagInt(value.toInt())
            else -> throw ClassCastException("String不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        if (reader is NBTTagByte) field[obj] = null
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = when (val local = annotation.local(field)) {
            String::class -> (reader as NBTTagString).string
            Int::class -> (reader as NBTTagInt).int.toString()
            else -> throw ClassCastException("${local.qualifiedName}不能转化为String")
        }
        field[obj] = value
    }

    override fun match(type: KClass<*>) = type == String::class

    override fun write2Local(value: String, local: KClass<*>): NBTBase {
        return when (local) {
            String::class -> NBTTagString(value)
            Int::class -> NBTTagInt(value.toInt())
            else -> throw ClassCastException("String不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (String) -> Unit) {
        val value = when (local) {
            String::class -> (reader as NBTTagString).string
            Int::class -> (reader as NBTTagInt).int.toString()
            else -> throw ClassCastException("${local.qualifiedName}不能转化为String")
        }
        receiver(value)
    }
}