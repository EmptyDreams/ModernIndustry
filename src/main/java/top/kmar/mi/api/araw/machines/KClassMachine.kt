package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagString
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.register.others.AutoRWType
import java.lang.reflect.Field
import javax.activation.UnsupportedDataTypeException
import kotlin.reflect.KClass

/**
 * KClass的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object KClassMachine : IAutoFieldRW, IAutoObjRW<KClass<*>?> {

    @JvmStatic fun instance() = KClassMachine

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return KClass::class.java.isAssignableFrom(annotation.source(field).java)
    }

    override fun write2Local(field: Field, obj: Any): NBTBase {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as KClass<*>?) ?: return NBTTagByte(0)
        if (value.qualifiedName == null) throw UnsupportedDataTypeException("不支持匿名类的读写")
        val local = annotation.local(field)
        if (!KClass::class.java.isAssignableFrom(local.java))
            throw ClassCastException("KClass<*>不能转化为${local.qualifiedName}")
        return NBTTagString(value.java.name)
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        field[obj] = if (reader is NBTTagByte) null
                else Class.forName((reader as NBTTagString).string).kotlin
    }

    override fun match(type: KClass<*>) = KClass::class.java.isAssignableFrom(type.java)

    override fun write2Local(value: KClass<*>?, local: KClass<*>): NBTBase {
        if (!KClass::class.java.isAssignableFrom(local.java))
            throw ClassCastException("KClass<*>不能转化为${local.qualifiedName}")
        return if (value == null) NBTTagByte(0) else NBTTagString(value.java.name)
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (KClass<*>?) -> Unit) {
        receiver(
            if (reader is NBTTagByte) null
            else Class.forName((reader as NBTTagString).string).kotlin
        )
    }
}