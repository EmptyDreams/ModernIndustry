package top.kmar.mi.api.araw.machines

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagString
import top.kmar.mi.api.araw.interfaces.*
import top.kmar.mi.api.araw.registers.AutoTypeRegister
import top.kmar.mi.api.regedits.others.AutoRWType
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Class的读写器
 * @author EmptyDreams
 */
@AutoRWType(AutoTypeRegister.VALUE_TYPE)
object ClassMachine : IAutoFieldRW, IAutoObjRW<Class<*>> {

    override fun allowFinal() = false

    override fun match(field: Field): Boolean {
        val annotation = field.getAnnotation(AutoSave::class.java)
        return annotation.source(field) == Class::class
    }

    override fun write2Local(field: Field, obj: Any): NBTBase? {
        val annotation = field.getAnnotation(AutoSave::class.java)
        val value = (field[obj] as Class<*>?) ?: return null
        return when (val local = annotation.local(field)) {
            Class::class -> NBTTagString(value.name)
            else -> throw ClassCastException("Class<?>不能转化为${local.qualifiedName}")
        }
    }

    override fun read2Obj(reader: NBTBase, field: Field, obj: Any) {
        val name = (reader as NBTTagString).string
        field[obj] = Class.forName(name)
    }

    override fun match(type: KClass<*>) = type == Class::class

    override fun write2Local(value: Class<*>, local: KClass<*>): NBTBase {
        if (local != Class::class)
            throw ClassCastException("Class<?>不能转化为${local.qualifiedName}")
        return NBTTagString(value.name)
    }

    override fun read2Obj(reader: NBTBase, local: KClass<*>, receiver: (Class<*>) -> Unit) {
        val name = (reader as NBTTagString).string
        receiver(Class.forName(name))
    }
}